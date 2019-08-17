package me.florian.command.brigadier;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class EnumArgumentType<E extends Enum<E>> implements ArgumentType<E> {

    private final Class<E> clazz;

    public EnumArgumentType(Class<E> clazz) {
        this.clazz = clazz;
    }

    public static <T extends Enum<T>> EnumArgumentType<T> enumArgument(Class<T> clazz) {
        return new EnumArgumentType<>(clazz);
    }

    @Override
    public E parse(StringReader stringReader) throws CommandSyntaxException {
        try {
            return Enum.valueOf(clazz, stringReader.readString().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SimpleCommandExceptionType(new LiteralMessage(e.getMessage())).create();
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        try {
            Arrays.stream(getEnumConstants()).map(E::name).map(String::toLowerCase).forEach(builder::suggest);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return builder.buildFuture();
    }

    private E[] getEnumConstants() throws NoSuchFieldException, IllegalAccessException {
        Field valuesField = clazz.getDeclaredField("$VALUES");
        valuesField.setAccessible(true);

        return (E[]) valuesField.get(null);
    }
}
