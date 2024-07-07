package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sonmiike.smcore.SmCore;
import dev.sonmiike.smcore.core.commands.customArguments.MuteArgument;
import dev.sonmiike.smcore.core.managers.MuteManager;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class MuteCommand
{

    private final MuteManager muteManager;

    public MuteCommand(SmCore plugin, Commands commands, MuteManager muteManager)
    {
        this.muteManager = muteManager;
        registerMute(plugin, commands);
        registerUnMute(plugin, commands);
    }

    private void registerMute(SmCore plugin, Commands commands)
    {
        final LiteralArgumentBuilder<CommandSourceStack> muteBuilder = Commands.literal("mute")
                .then(Commands.argument("player", ArgumentTypes.player())
                        .then(Commands.argument("duration", new MuteArgument()).executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            if (!PlayerUtil.playerHasPermission(sender, "smcore.mute"))
                                return 0;

                            executeMute(ctx.getSource(), ctx.getArgument("player", PlayerSelectorArgumentResolver.class)
                                            .resolve(ctx.getSource()).getFirst(),
                                    ctx.getArgument("duration", MuteArgument.DurationPair.class), null);
                            return Command.SINGLE_SUCCESS;
                        }).then((Commands.argument("reason", StringArgumentType.greedyString()).executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            if (!PlayerUtil.playerHasPermission(sender, "smcore.mute"))
                                return 0;

                            executeMute(ctx.getSource(), ctx.getArgument("player", PlayerSelectorArgumentResolver.class)
                                            .resolve(ctx.getSource()).getFirst(),
                                    ctx.getArgument("duration", MuteArgument.DurationPair.class),
                                    ctx.getArgument("reason", String.class));
                            return Command.SINGLE_SUCCESS;
                        })))));

        commands.register(plugin.getPluginMeta(), muteBuilder.build(), "mute", List.of());
    }

    private void executeMute(CommandSourceStack sourceStack, Player target, MuteArgument.DurationPair durationPair,
            String reason)
    {
        CommandSender sender = sourceStack.getSender();

        int durationInSeconds = durationPair.durationInSeconds;
        String humanReadableDuration = durationInSeconds > 0 ? durationPair.humanReadable : "PERMANENT";
        LocalDateTime muteDate = LocalDateTime.now();
        LocalDateTime muteExpiry = durationInSeconds > 0 ? muteDate.plus(Duration.ofSeconds(durationInSeconds)) : null;

        Component muteMessage = MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>\{target.getName()}<red> got muted by \{PlayerUtil.getPlayerNameWithRank(
                sender)} <red>for <white>\{humanReadableDuration} ";
        Component targetMessage = MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <red>You got muted! ";
        if (reason != null)
        {
            muteMessage = muteMessage.append(MM."<dark_gray>| <red>Reason: <gray>\{reason}");
            targetMessage = targetMessage.append(MM."<red>Reason: <gray>\{reason}");
        }

        Component finalMuteMessage = muteMessage;
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(finalMuteMessage));
        target.sendMessage(targetMessage);

        muteManager.mutePlayer(target.getUniqueId(), reason, sender.getName(), muteDate, muteExpiry);
    }

    private void registerUnMute(SmCore plugin, Commands commands)
    {
        final LiteralArgumentBuilder<CommandSourceStack> unmuteBuilder = Commands.literal("unmute")
                .then(Commands.argument("player", ArgumentTypes.player()).executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    if (!PlayerUtil.playerHasPermission(sender, "smcore.unmute"))
                        return 0;
                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class)
                            .resolve(ctx.getSource()).getFirst();
                    executeUnMute(ctx.getSource(), target);
                    return Command.SINGLE_SUCCESS;
                }));

        commands.register(plugin.getPluginMeta(), unmuteBuilder.build(), "unmute", List.of());
    }

    private void executeUnMute(CommandSourceStack sourceStack, Player target)
    {
        CommandSender sender = sourceStack.getSender();
        if (!muteManager.isPlayerMuted(target.getUniqueId()))
        {
            sender.sendMessage(
                    MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>\{target.getName()}<red> is not muted!");
            return;
        }
        sender.sendMessage(
                MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>\{target.getName()}<red> got unmuted!");
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(
                MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>\{target.getName()}<red> was unmuted by \{PlayerUtil.getPlayerNameWithRank(
                        sender)}"));
        target.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <red>You got unmuted!");
        muteManager.unmutePlayer(target.getUniqueId());
    }
}
