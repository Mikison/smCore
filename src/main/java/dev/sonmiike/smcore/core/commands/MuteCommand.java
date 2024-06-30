package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class MuteCommand {

    public MuteCommand(JavaPlugin plugin, Commands commands) {
        register(plugin, commands);
    }

    private void register(JavaPlugin plugin, Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> muteBuilder = Commands.literal("mute")
            .then(
                Commands.argument("player", ArgumentTypes.player())
                    .then(
                        Commands.argument("duration", ArgumentTypes.time())
                            .executes(ctx -> {
                                final CommandSourceStack sourceStack = ctx.getSource();
                                final CommandSender sender = sourceStack.getSender();
                                final Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(sourceStack).getFirst();
                                final Integer duration = ctx.getArgument("duration", Integer.class);
                                sender.sendMessage(MM."Czas: \{duration}");
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );


        commands.register(plugin.getPluginMeta(), muteBuilder.build(), "mute", List.of());
    }
}
