package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class FlyCommand {


    public FlyCommand(JavaPlugin plugin, Commands commands) {
        register(plugin, commands);
    }

    private void register(JavaPlugin plugin, Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> flyBuilder = Commands.literal("fly")
            .executes((source) -> {
                final CommandSourceStack sourceStack = source.getSource();
                final CommandSender sender = sourceStack.getSender();
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>You must be a player to use this command");
                    return 0;
                }
                if (!PlayerUtil.playerHasPermission(player, "smcore.fly")) return 0;
                player.setAllowFlight(!player.getAllowFlight());
                player.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Fly mode <blue>\{(player.getAllowFlight() ? "ENABLED" : "DISABLED")}");
                return Command.SINGLE_SUCCESS;
            })
            .then(
                Commands.argument("player", ArgumentTypes.player())
                    .executes(context -> {
                        final Player target = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource()).getFirst();
                        if (!PlayerUtil.playerHasPermission(target, "smcore.fly.other")) return 0;
                        target.setAllowFlight(!target.getAllowFlight());
                        target.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Fly mode <blue>\{(target.getAllowFlight() ? "ENABLED" : "DISABLED")} <gray>by \{(context.getSource().getSender() instanceof Player ? PlayerUtil.getPlayerNameWithRank((Player) context.getSource().getSender()) : "<bold><red>CONSOLE")}");
                        context.getSource().getSender().sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>You have \{(target.getAllowFlight() ? "ENABLED" : "DISABLED")} fly mode for \{PlayerUtil.getPlayerNameWithRank(target)}");
                        return Command.SINGLE_SUCCESS;
                    }
            ));

        commands.register(plugin.getPluginMeta(), flyBuilder.build(), "fly", List.of());
    }
}
