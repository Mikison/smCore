package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.sonmiike.smcore.core.util.MiniFormatter;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class GameModeCommand {

    public static void register(JavaPlugin plugin, Commands commands) {
        final LiteralArgumentBuilder<CommandSourceStack> gamemodeBuilder = Commands.literal("gamemode")
            .then(gamemodeArgument()
                .then(playerArgument()));

        commands.register(plugin.getPluginMeta(), gamemodeBuilder.build(), "gamemode", List.of("gm"));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, GameMode> gamemodeArgument() {
        return Commands.argument("gamemode", ArgumentTypes.gameMode())
            .executes((source) -> {
                CommandSourceStack sourceStack = source.getSource();
                GameMode gameMode = source.getArgument("gamemode", GameMode.class);
                if (!(sourceStack.getSender() instanceof Player player)) return 0;
                player.setGameMode(gameMode);
                player.sendMessage(MiniFormatter.deserialize("Ustawiłem kurwa gamemode jak coś okok"));
                return 1;
            });
    }

    private static RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver> playerArgument() {
        return Commands.argument("player", ArgumentTypes.player())
            .executes((source) -> {
                CommandSourceStack sourceStack = source.getSource();
                GameMode gameMode = source.getArgument("gamemode", GameMode.class);
                Player resolved = source.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(sourceStack).get(0);
                resolved.setGameMode(gameMode);
                resolved.sendMessage(MiniFormatter.deserialize("Ustawiłem kurwa gamemode jak coś dla " + resolved.getName() + " okok"));
                return 1;
            });
    }
}
