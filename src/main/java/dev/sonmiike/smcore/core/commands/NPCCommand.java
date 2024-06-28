package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sonmiike.smcore.SmCore;
import dev.sonmiike.smcore.core.managers.NPCManager;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class NPCCommand {

    public NPCCommand(SmCore plugin, Commands commands, NPCManager npcManager) {
        register(plugin, commands, npcManager);
    }

    private static final String TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTcxOTMzODA1MDQzNCwKICAicHJvZmlsZUlkIiA6ICIxYWMxMzAzZGUyNzQ0OTlmYjNmNDY5MWI4NmM3ZjIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTbm9mZmZlZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NGU4NDVlNzhmOWRiZTIxZWZiOTNmNjdhZGQ2YTAzZjY2NzA0NGVjZmU4MDU4OGEwMGI5MmY4NWNmZTc0MGVkIgogICAgfSwKICAgICJDQVBFIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yMzQwYzBlMDNkZDI0YTExYjE1YThiMzNjMmE3ZTllMzJhYmIyMDUxYjI0ODFkMGJhN2RlZmQ2MzVjYTdhOTMzIgogICAgfQogIH0KfQ==";
    private static final String SIGNATURE = "SK0kE0scliNyPexYOP3InkFd7bOWpTxU7a4wmp5Gd/L0lzLJcuPvdSWwMfOdAchxTwCO2kSfMZyTbd4QyVLuo0zCrGGKfiyh2lRhf/j6A7HydY4Vfij7AEfOC0TUr0W/xjCohM+i4gj6A8EPblzL6o9Q7nEMGkISu6DHbADYkZkfolY2uo9dzU59r+UJacW55EqNghd2vOuZjQBF6RPTDgLFjyGUgRSqF1zdC2/mIEgEbdHwwAJT/KussaImfPqdmJ8FPlPt7J51uK/olIBfkEkOthxc/h0TkDDrmVIF+YDWXOYV7GPaCfXwCVTMhPJg0FuWAZzX9br+pTU0hxyDE2wtEkQ9pZzt8MnzbCT7kobZqHi3hMaZ6Gc8SBxvCwrz5v38FWg1pFbAbUE71DBO1DFi6w7kECLRFwb533epcTCrYHY7UUMRe0alL80DoooBECpqVjE93r77UyBtBUPnRudFGNI84skydjKrGuErsZj+z/xV62iO7gCYbImce5gmNOI4Lto3qszGzK2dhL7qL9u/yjW5h+SZDCUEpMG0xqjPac9847ztXC7lQX7Q+FMHHUKMdB5OwEhsRXXtnyXBexeyIYgsNux4VSG/sLivWN0VlVQkhoeeldrwOc4HuvsT8+1srzVBsPTkggj4vq19aGdCOAoYmXIenDLtfFvFIVU=";

    private void register(SmCore plugin, Commands commands, NPCManager npcManager) {
        final LiteralArgumentBuilder<CommandSourceStack> npc = Commands.literal("npc")
            .executes(
                commandContext -> {
                    CommandSourceStack sourceStack = commandContext.getSource();
                    if (!PlayerUtil.playerHasPermission(sourceStack.getSender(), "smcore.npc")) return 0;
                    createNPC(sourceStack, npcManager);
                    return Command.SINGLE_SUCCESS;

                })
            .then(
                Commands.literal("skin")
                    .executes(ctx -> {
                        CommandSourceStack sourceStack = ctx.getSource();
                        if (sourceStack.getSender() instanceof Player player) {
                            player.sendMessage(MM."<yellow>Pitch: \{player.getLocation().getPitch()}");
                            player.sendMessage(MM."<yellow>Yaw: \{player.getLocation().getYaw()}");
                        }
                        return Command.SINGLE_SUCCESS;
                    }));

        commands.register(plugin.getPluginMeta(), npc.build(), "NPC", List.of());
    }

    private void createNPC(CommandSourceStack sourceStack, NPCManager npcManager) {
        if (sourceStack.getSender() instanceof Player player) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            ServerPlayer serverPlayer = craftPlayer.getHandle();
            MinecraftServer server = serverPlayer.getServer();
            ServerLevel level = serverPlayer.serverLevel();
            Location location = player.getLocation();

            npcManager.createNPC(server, level, TEXTURE, SIGNATURE, location);
        }
    }
}
