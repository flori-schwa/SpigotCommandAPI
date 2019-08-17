package me.florian.command.brigadier;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.florian.command.CommandSuggestions;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;

public class PositionArgumentType implements ArgumentType<Location> {

    public static PositionArgumentType position() {
        return new PositionArgumentType();
    }

    @Override
    public Location parse(StringReader stringReader) throws CommandSyntaxException {
        // Todo implement ~n and ^n
        final Location location = new Location(null, Double.NaN, Double.NaN, Double.NaN);

        location.setX(stringReader.readDouble());
        stringReader.skipWhitespace();
        location.setY(stringReader.readDouble());
        stringReader.skipWhitespace();
        location.setZ(stringReader.readDouble());

        return location;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        final String[] remaining = builder.getRemaining().split(" ", -1);

        if (context.getSource() instanceof Entity && (remaining.length >= 1 && remaining.length <= 3)) {
            Entity entity = (Entity) context.getSource();

            CommandSuggestions commandSuggestions = new CommandSuggestions(entity, context.getInput());

            commandSuggestions.suggestBlockPosition(remaining.length - 1, false);
            commandSuggestions.getSuggestions().forEach(builder::suggest);
        }

        return builder.buildFuture();
    }
}
