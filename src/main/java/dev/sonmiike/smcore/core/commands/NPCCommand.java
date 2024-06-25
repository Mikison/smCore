package dev.sonmiike.smcore.core.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sonmiike.smcore.SmCore;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class NPCCommand {

    private static final String TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTcxOTMzODA1MDQzNCwKICAicHJvZmlsZUlkIiA6ICIxYWMxMzAzZGUyNzQ0OTlmYjNmNDY5MWI4NmM3ZjIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTbm9mZmZlZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NGU4NDVlNzhmOWRiZTIxZWZiOTNmNjdhZGQ2YTAzZjY2NzA0NGVjZmU4MDU4OGEwMGI5MmY4NWNmZTc0MGVkIgogICAgfSwKICAgICJDQVBFIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yMzQwYzBlMDNkZDI0YTExYjE1YThiMzNjMmE3ZTllMzJhYmIyMDUxYjI0ODFkMGJhN2RlZmQ2MzVjYTdhOTMzIgogICAgfQogIH0KfQ==";

    private static final String SIGNATURE = "SK0kE0scliNyPexYOP3InkFd7bOWpTxU7a4wmp5Gd/L0lzLJcuPvdSWwMfOdAchxTwCO2kSfMZyTbd4QyVLuo0zCrGGKfiyh2lRhf/j6A7HydY4Vfij7AEfOC0TUr0W/xjCohM+i4gj6A8EPblzL6o9Q7nEMGkISu6DHbADYkZkfolY2uo9dzU59r+UJacW55EqNghd2vOuZjQBF6RPTDgLFjyGUgRSqF1zdC2/mIEgEbdHwwAJT/KussaImfPqdmJ8FPlPt7J51uK/olIBfkEkOthxc/h0TkDDrmVIF+YDWXOYV7GPaCfXwCVTMhPJg0FuWAZzX9br+pTU0hxyDE2wtEkQ9pZzt8MnzbCT7kobZqHi3hMaZ6Gc8SBxvCwrz5v38FWg1pFbAbUE71DBO1DFi6w7kECLRFwb533epcTCrYHY7UUMRe0alL80DoooBECpqVjE93r77UyBtBUPnRudFGNI84skydjKrGuErsZj+z/xV62iO7gCYbImce5gmNOI4Lto3qszGzK2dhL7qL9u/yjW5h+SZDCUEpMG0xqjPac9847ztXC7lQX7Q+FMHHUKMdB5OwEhsRXXtnyXBexeyIYgsNux4VSG/sLivWN0VlVQkhoeeldrwOc4HuvsT8+1srzVBsPTkggj4vq19aGdCOAoYmXIenDLtfFvFIVU=";

    public static void register(SmCore plugin, Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> npc = Commands.literal("npc")
            .executes((source) -> {
                CommandSourceStack sourceStack = source.getSource();
                if (sourceStack.getSender() instanceof Player) {
                    Player player = (Player) sourceStack.getSender();
                    CraftPlayer craftPlayer = (CraftPlayer) player;
                    ServerPlayer serverPlayer = craftPlayer.getHandle();
                    if (serverPlayer == null) return 0;

                    MinecraftServer server = serverPlayer.getServer();
                    ServerLevel level = serverPlayer.serverLevel();

                    GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "Rynek");
                    gameProfile.getProperties().put("textures", new Property("textures", TEXTURE, SIGNATURE));

                    ServerPlayer npcPlayer = new ServerPlayer(server, level, gameProfile, ClientInformation.createDefault());

                    npcPlayer.setGlowingTag(true);

                    Location location = player.getLocation();
                    npcPlayer.setPos(location.getX(), location.getY(), location.getZ());


                    ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(
                        gameProfile.getId(), gameProfile, false, 1, net.minecraft.world.level.GameType.SURVIVAL,
                        Component.literal(gameProfile.getName()), null);

                    ClientboundPlayerInfoUpdatePacket playerInfoUpdatePacket = new ClientboundPlayerInfoUpdatePacket(
                        EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER), entry);

                    ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(
                        npcPlayer, 0, new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()));

                    for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                        p.connection.send(playerInfoUpdatePacket);
                        p.connection.send(addEntityPacket);
                    }
                    plugin.getNpcManager().addNPC(npcPlayer);

                }
                return 1;
            });
        commands.register(plugin.getPluginMeta(), npc.build(), "NPC", List.of());
    }
}
