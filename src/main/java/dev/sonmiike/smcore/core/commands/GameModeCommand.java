package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.sonmiike.smcore.core.commands.customArguments.GameModeArgument;
import dev.sonmiike.smcore.core.commands.customArguments.GameModeType;
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

public class GameModeCommand
{

    public GameModeCommand(JavaPlugin plugin, Commands commands)
    {
        register(plugin, commands);
    }

    private void register(JavaPlugin plugin, Commands commands)
    {
        final LiteralArgumentBuilder<CommandSourceStack> gamemodeBuilder = Commands.literal("gamemode")
                .then(createGamemodeArgument().then(createPlayerArgument()));

        commands.register(plugin.getPluginMeta(), gamemodeBuilder.build(), "gamemode", List.of("gm"));
    }

    private RequiredArgumentBuilder<CommandSourceStack, GameModeType> createGamemodeArgument()
    {
        return Commands.argument("gamemode", new GameModeArgument())
                .executes(context -> executeGamemodeCommand(context, null));
    }

    private RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver> createPlayerArgument()
    {
        return Commands.argument("player", ArgumentTypes.player()).executes(context -> {
            PlayerSelectorArgumentResolver resolver = context.getArgument("player",
                    PlayerSelectorArgumentResolver.class);
            return executeGamemodeCommand(context, resolver.resolve(context.getSource()).getFirst());
        });
    }

    private int executeGamemodeCommand(CommandContext<CommandSourceStack> context, Player target)
    {
        final CommandSourceStack sourceStack = context.getSource();
        final CommandSender sender = sourceStack.getSender();
        final GameModeType gameModeType = context.getArgument("gamemode", GameModeType.class);
        final GameMode gameMode = GameMode.valueOf(gameModeType.name());

        if (target == null)
        {
            if (!(sender instanceof Player player))
            {
                sender.sendMessage(
                        MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <gray>You must be a player to use this command");
                return 0;
            }
            if (!PlayerUtil.playerHasPermission(player, "smcore.gamemode"))
                return 0;

            player.setGameMode(gameMode);
            player.sendMessage(
                    MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Your gamemode has been set to <blue>\{gameMode.name()}");
            return 0;
        }
        if (!PlayerUtil.playerHasPermission(sender, "smcore.gamemode.others"))
            return 0;

        target.setGameMode(gameMode);
        target.sendMessage(
                MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Your gamemode has been set to <blue>\{gameMode.name()} <gray>by \{PlayerUtil.getPlayerNameWithRank(
                        sender)}");
        sender.sendMessage(
                MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Set \{PlayerUtil.getPlayerNameWithRank(
                        target)} <gray>gamemode to <blue>\{gameMode.name()}");

        return Command.SINGLE_SUCCESS;
    }
}
