package me.florian.command.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import me.florian.command.AbstractCommand;
import me.florian.command.ArgumentIterator;
import me.florian.command.CommandSuggestions;
import me.florian.command.exception.CommandException;
import me.florian.command.result.CommandResult;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class BrigadierCommand<C extends CommandSender> extends AbstractCommand {

    private final CommandDispatcher<C> commandDispatcher;
    private final Class<C> cClass;

    public BrigadierCommand(Plugin plugin, LiteralArgumentBuilder<C> literalArgumentBuilder, Class<C> cClass) {
        super(plugin, literalArgumentBuilder.getLiteral());

        this.cClass = cClass;

        this.commandDispatcher = new CommandDispatcher<>();
        this.commandDispatcher.register(literalArgumentBuilder);
    }

    public static CommandSyntaxException literal(String message) {
        return new SimpleCommandExceptionType(new LiteralMessage(message)).create();
    }

    private String getFullInput(String[] args) {
        StringBuilder builder = new StringBuilder(getName());

        for (String arg : args) {
            builder.append(" ").append(arg);
        }

        return builder.toString();
    }

    private String getFullInput(ArgumentIterator args) {
        return getFullInput(args.getArguments());
    }

    @Override
    public final String[] getAliases() {
        return new String[0];
    }

    @Override
    public final CommandResult execute(CommandSender sender, ArgumentIterator args) {
        C source = assertSenderInstanceOf(sender, cClass);

        ParseResults<C> parseResults = commandDispatcher.parse(getFullInput(args), source);

        try {
            commandDispatcher.execute(parseResults);
        } catch (CommandSyntaxException e) {
            throw CommandException.mildException(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public final void tabComplete(CommandSuggestions suggestions) {
        try {
            C source = assertSenderInstanceOf(suggestions.getCommandSender(), cClass);
            Suggestions result = commandDispatcher.getCompletionSuggestions(commandDispatcher.parse(getFullInput(suggestions.getArguments()), source)).get();

            suggestions.add(result.getList().stream().map(Suggestion::getText).collect(Collectors.toSet()));
        } catch (CommandException e) {
            // Ignore
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
