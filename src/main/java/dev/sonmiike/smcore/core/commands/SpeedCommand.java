package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sonmiike.smcore.SmCore;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class SpeedCommand {

    public SpeedCommand(SmCore plugin, Commands commands) {
        register(plugin, commands);
    }

    private void register(SmCore plugin, Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> speedBuilder = Commands.literal("speed")
            .then(createSpeedSubcommand("walk", Player::setWalkSpeed))
            .then(createSpeedSubcommand("fly", Player::setFlySpeed));

        commands.register(plugin.getPluginMeta(), speedBuilder.build(), "speed", List.of());
    }

    private LiteralArgumentBuilder<CommandSourceStack> createSpeedSubcommand(String type, SpeedSetter speedSetter) {
        return Commands.literal(type)
            .then(
                Commands.argument("speed", IntegerArgumentType.integer(1, 10))
                    .executes(context -> {
                        final CommandSourceStack sourceStack = context.getSource();
                        final int speed = context.getArgument("speed", Integer.class);
                        final CommandSender sender = sourceStack.getSender();

                        if (!(sender instanceof Player player)) {
                            sender.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <red>You must be a player to use this command");
                            return 0;
                        }
                        if (!PlayerUtil.playerHasPermission(player, "smcore.speed")) return 0;

                        speedSetter.setSpeed(player, speed / 10f);
                        player.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Your \{type} speed has been set to <blue>\{speed}");
                        return Command.SINGLE_SUCCESS;
                    })
                    .then(
                        Commands.argument("player", ArgumentTypes.player())
                            .executes(context -> {
                                final CommandSourceStack sourceStack = context.getSource();
                                final CommandSender sender = sourceStack.getSender();
                                final Player target = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(sourceStack).getFirst();
                                final int speed = context.getArgument("speed", Integer.class);

                                if (!PlayerUtil.playerHasPermission(sender, "smcore.speed.others")) return 0;

                                speedSetter.setSpeed(target, speed / 10f);
                                target.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Your \{type} speed has been set to <blue>\{speed} <gray>by \{sender instanceof Player player ? PlayerUtil.getPlayerNameWithRank(player) : "<bold><red>CONSOLE"} ");
                                sender.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Set \{PlayerUtil.getPlayerNameWithRank(target)}<gray> \{type} speed to <blue>\{speed}");
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );
    }

    @FunctionalInterface
    private interface SpeedSetter {
        void setSpeed(Player player, float speed);
    }
}
