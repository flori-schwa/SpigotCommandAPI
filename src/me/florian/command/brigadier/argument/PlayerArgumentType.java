package me.florian.command.brigadier.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class PlayerArgumentType implements ArgumentType<Player> {

    public static PlayerArgumentType player() {
        return new PlayerArgumentType();
    }

    @Override
    public Player parse(StringReader stringReader) throws CommandSyntaxException {
        Player player = Bukkit.getPlayer(stringReader.readString());

        if (player == null) {
            throw new SimpleCommandExceptionType(new LiteralMessage("Could not find a player with that name!")).create();
        }

        return player;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(builder::suggest);

        return builder.buildFuture();
    }
}
