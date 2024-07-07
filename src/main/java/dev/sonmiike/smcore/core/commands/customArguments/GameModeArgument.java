package dev.sonmiike.smcore.core.commands.customArguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GameModeArgument implements CustomArgumentType.Converted<GameModeType, String>
{
    @Override
    public @NotNull GameModeType convert(@NotNull String s) throws CommandSyntaxException
    {
        try
        {
            int id = Integer.parseInt(s);
            for (GameModeType type : GameModeType.values())
            {
                if (type.getId() == id)
                {
                    return type;
                }
            }
        }
        catch (NumberFormatException e)
        {
            try
            {
                return GameModeType.valueOf(s.toUpperCase());
            }
            catch (IllegalArgumentException ex)
            {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
            }
        }

        // If no match found for ID, throw an exception
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType()
    {
        return StringArgumentType.word();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        for (GameModeType gameMode : GameModeType.values())
        {
            builder.suggest(gameMode.name());
            builder.suggest(gameMode.getId());
        }

        return CompletableFuture.completedFuture(builder.build());
    }
}
