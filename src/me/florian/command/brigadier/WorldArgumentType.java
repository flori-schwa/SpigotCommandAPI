package me.florian.command.brigadier;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public class WorldArgumentType implements ArgumentType<World> {

    public static WorldArgumentType world() {
        return new WorldArgumentType();
    }

    @Override
    public World parse(StringReader stringReader) throws CommandSyntaxException {
        final World world = Bukkit.getWorld(stringReader.readString());

        if (world == null) {
            throw new SimpleCommandExceptionType(new LiteralMessage("Could not find a world with that name!")).create();
        }

        return world;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Bukkit.getWorlds().stream().map(World::getName).forEach(builder::suggest);

        return builder.buildFuture();
    }
}
