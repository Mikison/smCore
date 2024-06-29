package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class TeleportCommand {

    public TeleportCommand(JavaPlugin plugin, Commands commands) {
        register(plugin, commands);
    }

    private void register(JavaPlugin plugin, Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> teleportBuilder = Commands.literal("tp")
            .then(
                Commands.argument("player", ArgumentTypes.player())
                    .executes(ctx -> teleportToPlayer(ctx, ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst()))
            )
            .then(
                Commands.argument("x", IntegerArgumentType.integer())
                    .then(Commands.argument("y", IntegerArgumentType.integer())
                        .then(Commands.argument("z", IntegerArgumentType.integer())
                            .executes(ctx -> teleportToCoordinates(ctx, ctx.getArgument("x", Integer.class), ctx.getArgument("y", Integer.class), ctx.getArgument("z", Integer.class)))
                        )
                    )
            )
            .then(
                Commands.argument("player1", ArgumentTypes.player())
                    .then(Commands.argument("player2", ArgumentTypes.player())
                        .executes(ctx -> teleportPlayerToPlayer(ctx, ctx.getArgument("player1", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst(), ctx.getArgument("player2", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst()))
                    )
            );

        commands.register(plugin.getPluginMeta(), teleportBuilder.build(), "tp", List.of());
    }

    private int teleportToPlayer(CommandContext<CommandSourceStack> context, Player target) {
        final CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player player) {
            player.teleport(target.getLocation());
            sender.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Teleported to \{PlayerUtil.getPlayerNameWithRank(target)}");
        } else {
            sender.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>You must be a player to use this command");
        }
        return Command.SINGLE_SUCCESS;
    }

    private int teleportToCoordinates(CommandContext<CommandSourceStack> context, int x, int y, int z) {
        final CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player player) {
            Location location = new Location(player.getWorld(), x, y, z);
            player.teleport(location);
            sender.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Teleported to <bold>X: <blue>\{x} <gray>Y: <blue>\{y} <gray>Z: <blue>\{z}");
        } else {
            sender.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>You must be a player to use this command");
        }
        return Command.SINGLE_SUCCESS;
    }

    private int teleportPlayerToPlayer(CommandContext<CommandSourceStack> context, Player player1, Player player2) {
        player1.teleport(player2.getLocation());
        context.getSource().getSender().sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Teleported \{PlayerUtil.getPlayerNameWithRank(player1)} to \{PlayerUtil.getPlayerNameWithRank(player2)}");
        return Command.SINGLE_SUCCESS;
    }
}
