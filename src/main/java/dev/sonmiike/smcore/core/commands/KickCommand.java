package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class KickCommand {

    private final JavaPlugin plugin;
    private final Commands commands;

    public KickCommand(JavaPlugin plugin, Commands commands) {
        this.plugin = plugin;
        this.commands = commands;
        register();
    }

    private void register() {
        final LiteralArgumentBuilder<CommandSourceStack> kickBuilder = Commands.literal("kick")
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes(context -> kickPlayer(context.getSource(), context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource()).getFirst(), null))
                .then(Commands.argument("reason", StringArgumentType.greedyString())
                    .executes(context -> kickPlayer(context.getSource(), context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource()).getFirst(), context.getArgument("reason", String.class)))
                )
            );
        commands.register(plugin.getPluginMeta(), kickBuilder.build(), "kick", List.of());
    }

    private int kickPlayer(CommandSourceStack source, Player target, String reason) {
        CommandSender sender = source.getSender();
        if (!PlayerUtil.playerHasPermission(sender, "smcore.kick")) return 0;

        final String senderName = PlayerUtil.getPlayerNameWithRank(sender);
        Component kickMessage = MM."<red>You have been kicked from the server by \{senderName}";
        Component broadcastMessage = MM."<bold><dark_gray>[<red>!<dark_gray>]</bold><gray> \{target.getName()} <red>has been kicked by \{senderName}";

        if (reason != null) {
            kickMessage = kickMessage.appendNewline().append(MM."<red><bold>Reason: <white>\{reason}");
            broadcastMessage = broadcastMessage.append(MM." <dark_gray>|<reset><bold> Reason:</bold> <white>\{reason}");
        }

        Component finalBroadcastMessage = broadcastMessage;
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(finalBroadcastMessage));
        target.kick(kickMessage);
        return Command.SINGLE_SUCCESS;
    }
}
