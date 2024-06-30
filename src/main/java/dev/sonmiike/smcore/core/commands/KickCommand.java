package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class KickCommand {


    public KickCommand(JavaPlugin plugin, Commands commands) {
        register(plugin, commands);
    }

    private void register(JavaPlugin plugin, Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> kickBuilder = Commands.literal("kick")
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes(context -> {
                    final CommandSourceStack source = context.getSource();
                    final Player target = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(source).getFirst();
                    final CommandSender sender = source.getSender();
                    if (!PlayerUtil.playerHasPermission(sender, "smcore.kick")) return 0;
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>\{target.getName()} <red>has been kicked by \{sender instanceof Player kicker ? PlayerUtil.getPlayerNameWithRank(kicker) : "<bold><red>CONSOLE"}"));
                    target.kick(MM."<red>You have been kicked from the server by \{sender instanceof Player player ? PlayerUtil.getPlayerNameWithRank(player) : "<bold><red>CONSOLE"}");
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("reason", StringArgumentType.string())
                    .executes(context -> {
                        final CommandSourceStack source = context.getSource();
                        final Player target = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(source).getFirst();
                        final String reason = context.getArgument("reason", String.class);
                        final CommandSender sender = source.getSender();
                        if (!PlayerUtil.playerHasPermission(sender, "smcore.kick")) return 0;
                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>\{target.getName()} <red>has been kicked by \{sender instanceof Player kicker ? PlayerUtil.getPlayerNameWithRank(kicker) : "<bold><red>CONSOLE"}"
                            .appendNewline()
                            .append(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold><red><bold> Reason: <white>\{reason}")));
                        target.kick(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold><red>You have been kicked from the server by \{sender instanceof Player player ? PlayerUtil.getPlayerNameWithRank(player) : "<bold><red>CONSOLE"}"
                            .appendNewline()
                            .appendNewline()
                            .append(MM."<reset><red>Reason: <white>\{reason}"));
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );
        commands.register(plugin.getPluginMeta(), kickBuilder.build(), "kick", List.of());
    }
}
