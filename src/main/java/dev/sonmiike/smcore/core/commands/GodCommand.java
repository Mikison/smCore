package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sonmiike.smcore.core.managers.GodManager;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class GodCommand {

    private final GodManager godManager;

    public GodCommand(JavaPlugin plugin, Commands commands, GodManager godManager) {
        this.godManager = godManager;
        register(plugin,commands);
    }

    private void register(JavaPlugin plugin, Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> vanishBuilder = Commands.literal("god")
            .executes((source) -> {
                final CommandSourceStack sourceStack = source.getSource();
                final CommandSender sender = sourceStack.getSender();
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>You must be a player to use this command");
                    return 0;
                }
                if (!PlayerUtil.playerHasPermission(player, "smcore.god")) return 0;
                godManager.toggleGod(player);
                return Command.SINGLE_SUCCESS;
            });

        commands.register(plugin.getPluginMeta(), vanishBuilder.build(), "god", List.of());
    }
}
