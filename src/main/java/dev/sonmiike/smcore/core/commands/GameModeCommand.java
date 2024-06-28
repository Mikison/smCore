package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.sonmiike.smcore.core.commands.customArguments.GameModeArgument;
import dev.sonmiike.smcore.core.commands.customArguments.GameModeType;
import dev.sonmiike.smcore.core.util.MiniFormatter;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;


public class GameModeCommand {

    public GameModeCommand(JavaPlugin plugin, Commands commands) {
        register(plugin, commands);
    }

    private void register(JavaPlugin plugin, Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> gamemodeBuilder = Commands.literal("gamemode")
            .then(gamemodeArgument()
                .then(playerArgument()));

        commands.register(plugin.getPluginMeta(), gamemodeBuilder.build(), "gamemode", List.of("gm"));
    }

    private RequiredArgumentBuilder<CommandSourceStack, GameModeType> gamemodeArgument() {
        return Commands.argument("gamemode", new GameModeArgument())
            .executes((source) -> {
                CommandSourceStack sourceStack = source.getSource();
                GameModeType gameModeString = source.getArgument("gamemode", GameModeType.class);
                GameMode gameMode = GameMode.valueOf(gameModeString.name());

                if (!(sourceStack.getSender() instanceof Player player)) {
                    sourceStack.getSender().sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>You must be a player to use this command");
                    return 0;
                }

                player.setGameMode(gameMode);
                player.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Your gamemode has been set to <blue>\{gameMode.name()}");
                return Command.SINGLE_SUCCESS;
            });
    }

    private RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver> playerArgument() {
        return Commands.argument("player", ArgumentTypes.player())
            .executes((source) -> {
                CommandSourceStack sourceStack = source.getSource();
                CommandSender commandSender = sourceStack.getSender();
                GameModeType gameModeString = source.getArgument("gamemode", GameModeType.class);
                GameMode gameMode = GameMode.valueOf(gameModeString.name());
                Player resolved = source.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(sourceStack).get(0);
                resolved.setGameMode(gameMode);
                resolved.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Your gamemode has been set to <blue>\{gameMode.name()} <gray>by \{commandSender instanceof Player player ? PlayerUtil.getPlayerNameWithRank(player) : "<bold><red>CONSOLE"}");
                commandSender.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Set <blue>\{PlayerUtil.getPlayerNameWithRank(resolved)}'s <gray>gamemode to <blue>\{gameMode.name()}");

                return Command.SINGLE_SUCCESS;
            });
    }
}
