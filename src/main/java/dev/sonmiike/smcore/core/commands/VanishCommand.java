package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sonmiike.smcore.core.managers.VanishManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class VanishCommand {

    private final VanishManager vanishManager;

    public VanishCommand(JavaPlugin plugin, Commands commands,VanishManager vanishManager) {
        this.vanishManager = vanishManager;
        register(plugin, commands);
    }

    public void register(JavaPlugin plugin, Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> vanishBuilder = Commands.literal("vanish")
            .executes((source) -> {
                CommandSourceStack sourceStack = source.getSource();
                CommandSender sender = sourceStack.getSender();
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>You must be a player to use this command");
                    return 0;
                }
                vanishManager.toggleVanish(player);
                player.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>You are now <blue>\{vanishManager.isVanished(player) ? "VANISHED" : "VISIBLE"}");
                return Command.SINGLE_SUCCESS;
            });

        commands.register(plugin.getPluginMeta(), vanishBuilder.build(), "vanish", List.of("v"));
    }





}
