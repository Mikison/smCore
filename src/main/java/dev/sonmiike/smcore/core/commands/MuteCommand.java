package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sonmiike.smcore.SmCore;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class MuteCommand {

    public MuteCommand(SmCore plugin, Commands commands) {
        register(plugin, commands);
    }

    private void register(SmCore plugin, Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> muteBuilder = Commands.literal("mute")
            .then(Commands.argument("player", ArgumentTypes.player())
                .then(Commands.argument("duration", ArgumentTypes.time())
                    .executes(ctx -> executeMute(ctx.getSource(), ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst(), ctx.getArgument("duration", Integer.class), "NULL", plugin))
                    .then(Commands.argument("reason", StringArgumentType.greedyString())
                        .executes(ctx -> executeMute(ctx.getSource(), ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst(), ctx.getArgument("duration", Integer.class), ctx.getArgument("reason", String.class), plugin))
                    )
                )
            );

        commands.register(plugin.getPluginMeta(), muteBuilder.build(), "mute", List.of());
    }

    private int executeMute(CommandSourceStack sourceStack, Player target, int duration, String reason, SmCore plugin) {
        CommandSender sender = sourceStack.getSender();
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>\{target.getName()} was muted by \{sender.getName()} for \{duration} seconds"));
        plugin.getDatabaseManager().mutePlayer(target.getUniqueId(), reason, sender.getName(), LocalDateTime.now(), LocalDateTime.now().plus(Duration.ofSeconds(duration)));
        return Command.SINGLE_SUCCESS;
    }
}
