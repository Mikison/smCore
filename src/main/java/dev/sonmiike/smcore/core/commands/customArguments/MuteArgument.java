package dev.sonmiike.smcore.core.commands.customArguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MuteArgument implements CustomArgumentType.Converted<MuteArgument.DurationPair, String>
{

    @Override
    public @NotNull DurationPair convert(@NotNull String s) throws CommandSyntaxException
    {
        // Handle special case for "0" which means permanent mute
        if (s.equals("0"))
        {
            return new DurationPair(0, "PERMANENT");
        }

        // Get the unit (last character) and the numeric value (rest of the string)
        String unit = s.substring(s.length() - 1).toLowerCase();
        int value;

        try
        {
            value = Integer.parseInt(s.substring(0, s.length() - 1));
        }
        catch (NumberFormatException e)
        {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt()
                    .createWithContext(new StringReader(s), s);
        }

        if (value == 0)
        {
            return new DurationPair(0, "PERMANENT");
        }

        int durationInSeconds;
        String humanReadable;

        switch (unit)
        {
        case "s" ->
        {
            durationInSeconds = value;
            humanReadable = STR."\{value} seconds";
        }
        case "m" ->
        {
            durationInSeconds = value * 60;
            humanReadable = STR."\{value} minutes";
        }
        case "h" ->
        {
            durationInSeconds = value * 3600;
            humanReadable = STR."\{value} hours";
        }
        case "d" ->
        {
            durationInSeconds = value * 86400;
            humanReadable = STR."\{value} days";
        }
        default -> throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        }

        return new DurationPair(durationInSeconds, humanReadable);
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType()
    {
        return StringArgumentType.word();
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context,
            @NotNull SuggestionsBuilder builder)
    {
        String input = builder.getRemaining();
        if (input.isEmpty())
        {
            builder.suggest("0");
            builder.suggest("0s");
            builder.suggest("0m");
            builder.suggest("0h");
            builder.suggest("0d");
            return CompletableFuture.completedFuture(builder.build());
        }
        if (input.matches("\\d*"))
        {
            builder.suggest(input + "s");
            builder.suggest(input + "m");
            builder.suggest(input + "h");
            builder.suggest(input + "d");
        }

        return CompletableFuture.completedFuture(builder.build());
    }

    public static class DurationPair
    {
        public final int durationInSeconds;
        public final String humanReadable;

        public DurationPair(int durationInSeconds, String humanReadable)
        {
            this.durationInSeconds = durationInSeconds;
            this.humanReadable = humanReadable;
        }
    }
}
