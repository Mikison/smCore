package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
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

public class ClearCommand {

    public ClearCommand(JavaPlugin plugin, Commands commands) {
        register(plugin, commands);
    }

    private void register(JavaPlugin plugin, Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> clearBuilder = Commands.literal("clear")
            .executes(ctx -> clearInventory(ctx, null))
            .then(
                Commands.argument("player", ArgumentTypes.player())
                    .executes(ctx -> {
                        final Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                        return clearInventory(ctx, target);
                    })
            );
        commands.register(plugin.getPluginMeta(), clearBuilder.build(), "clear", List.of("ci"));
    }

    private int clearInventory(CommandContext<CommandSourceStack> context, Player target) {
        final CommandSourceStack sourceStack = context.getSource();
        final CommandSender sender = sourceStack.getSender();

        if (!(sender instanceof Player) && target == null) {
            sender.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>You must be a player to use this command");
            return 0;
        }

        if (target == null) {
            final Player player = (Player) sender;
            if (!PlayerUtil.playerHasPermission(player, "smcore.clear")) return 0;
            player.getInventory().clear();
            player.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Your inventory has been cleared");
            return Command.SINGLE_SUCCESS;
        }
        if (!PlayerUtil.playerHasPermission(sender, "smcore.clear.other")) return 0;

        target.getInventory().clear();
        target.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Your inventory has been cleared by \{PlayerUtil.getPlayerNameWithRank(sender)}");
        sender.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>You have cleared the inventory of \{PlayerUtil.getPlayerNameWithRank(target)}");

        return Command.SINGLE_SUCCESS;
    }
}
