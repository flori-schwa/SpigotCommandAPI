package me.florian.command.brigadier.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.florian.command.CommandSuggestions;
import me.florian.command.brigadier.datatypes.CaretCoordinates;
import me.florian.command.brigadier.datatypes.Coordinates;
import me.florian.command.brigadier.datatypes.WorldCoordinates;
import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;

public class PositionArgumentType implements ArgumentType<Coordinates> {

    public static PositionArgumentType position() {
        return new PositionArgumentType();
    }

    @Override
    public Coordinates parse(StringReader stringReader) throws CommandSyntaxException {
        // Todo implement ~n and ^n
        if (stringReader.peek() == '^') {
            System.out.println("Returning Caret Coordinates");
            return new CaretCoordinates(stringReader);
        } else {
            System.out.println("Returning World Coordinates");
            return new WorldCoordinates(stringReader);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        final String[] remaining = builder.getRemaining().split(" ", -1);
        final int completedCoords = remaining.length - 1;

        if (context.getSource() instanceof Entity && (remaining.length >= 1 && remaining.length <= 3)) {
            Entity entity = (Entity) context.getSource();

            CommandSuggestions commandSuggestions = new CommandSuggestions(entity, context.getInput());
            commandSuggestions.suggestBlockPosition(completedCoords, false);

            for (int i = 0; i < (3 - completedCoords); i++) {
                StringBuilder suggestionBuilder = new StringBuilder();

                for (int j = 0; j <= i; j++) {
                    suggestionBuilder.append("~ ");
                }

                commandSuggestions.add(suggestionBuilder.toString().trim(), false);
            }

            commandSuggestions.getSuggestions().forEach(builder::suggest);
        }

        return builder.buildFuture();
    }
}
