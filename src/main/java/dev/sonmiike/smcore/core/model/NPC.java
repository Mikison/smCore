package dev.sonmiike.smcore.core.model;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboardManager;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.UUID;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class NPC
{
    @Getter private final ServerPlayer npcPlayer;
    private final GameProfile gameProfile;
    private final Location location;
    private TextDisplay textDisplay;

    public NPC(MinecraftServer server, ServerLevel level, String texture, String signature, Location location)
    {
        this.gameProfile = createGameProfile(texture, signature);
        this.npcPlayer = new ServerPlayer(server, level, gameProfile, ClientInformation.createDefault());
        this.location = location;
        setLocation(location);
        enableSkinParts();
        createTextDisplayEntity(location);
    }

    private GameProfile createGameProfile(String texture, String signature)
    {
        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), RandomStringUtils.randomAlphabetic(10));
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
        return gameProfile;
    }

    private void setLocation(Location location)
    {
        npcPlayer.absMoveTo(location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5);
    }

    private void enableSkinParts()
    {
        final SynchedEntityData dataWatcher = npcPlayer.getEntityData();
        final EntityDataAccessor<Byte> skinParts = new EntityDataAccessor<>(17, EntityDataSerializers.BYTE);
        byte currentSkinParts = dataWatcher.get(skinParts);
        byte enableAllExceptCape = (byte) 0x7E;
        byte newSkinParts = (byte) (currentSkinParts | enableAllExceptCape);
        dataWatcher.set(skinParts, newSkinParts);
    }

    private void createTextDisplayEntity(Location location)
    {
        final Location add = location.clone()
                .set(location.getBlockX() + 0.5, location.getBlockY() + 2.3, location.getBlockZ() + 0.5);
        this.textDisplay = add.getWorld().spawn(add, TextDisplay.class, text -> {
            text.text(MM."<bold><red>CFEL");
            text.setPersistent(true);
            text.setAlignment(TextDisplay.TextAlignment.CENTER);
            text.setBillboard(Display.Billboard.CENTER);
        });
    }

    private @NotNull PlayerTeam getPlayerTeam()
    {
        final CraftScoreboardManager scoreboardManager = (CraftScoreboardManager) Bukkit.getServer()
                .getScoreboardManager();
        final CraftScoreboard mainScoreboard = scoreboardManager.getMainScoreboard();
        final Scoreboard scoreboard = mainScoreboard.getHandle();

        boolean isPresent = scoreboard.getTeamNames().contains("npcTeam");
        final PlayerTeam team = isPresent ?
                (PlayerTeam) mainScoreboard.getTeam("npcTeam") :
                new PlayerTeam(scoreboard, "npcTeam");
        team.setCollisionRule(Team.CollisionRule.NEVER);
        team.setNameTagVisibility(Team.Visibility.NEVER);

        return team;
    }

    public void sendPacketsToPlayers()
    {
        final SynchedEntityData dataWatcher = npcPlayer.getEntityData();

        // ADDING THE NPC
        final ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(
                npcPlayer.getGameProfile().getId(), npcPlayer.getGameProfile(), false, 1, GameType.SURVIVAL,
                Component.literal(npcPlayer.getGameProfile().getName()), null);
        final ClientboundPlayerInfoUpdatePacket playerInfoUpdatePacket = new ClientboundPlayerInfoUpdatePacket(
                EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER), entry);

        final ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(npcPlayer.getId(),
                npcPlayer.getUUID(), location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5, 0, 0,
                npcPlayer.getType(), 0, new Vec3(0, 0, 0), 0);

        final ClientboundSetEntityDataPacket entityDataPacket = new ClientboundSetEntityDataPacket(npcPlayer.getId(),
                dataWatcher.packAll());

        // ADDING NPC TO TEAM
        final PlayerTeam team = getPlayerTeam();

        final ClientboundSetPlayerTeamPacket addOrModifyPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(
                team, true);
        final ClientboundSetPlayerTeamPacket playerPacket = ClientboundSetPlayerTeamPacket.createPlayerPacket(team,
                npcPlayer.displayName, ClientboundSetPlayerTeamPacket.Action.ADD);

        for (ServerPlayer p : npcPlayer.getServer().getPlayerList().getPlayers())
        {
            p.connection.send(playerInfoUpdatePacket);
            p.connection.send(addEntityPacket);
            p.connection.send(entityDataPacket);
            p.connection.send(addOrModifyPacket);
            p.connection.send(playerPacket);
        }
    }

    public void removeTextDisplay()
    {
        if (textDisplay != null && !textDisplay.isDead())
        {
            textDisplay.remove();
        }
    }
}
