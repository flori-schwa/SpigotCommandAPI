package me.shawlaf.command.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import me.shawlaf.command.AbstractCommand;
import me.shawlaf.command.ArgumentIterator;
import me.shawlaf.command.CommandSuggestions;
import me.shawlaf.command.exception.CommandException;
import me.shawlaf.command.result.CommandResult;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public abstract class BrigadierCommand<C extends CommandSender, P extends JavaPlugin> extends AbstractCommand<P> {

    protected final CommandDispatcher<C> commandDispatcher;
    private final Class<C> cClass;
    private boolean built = false;

    public BrigadierCommand(P plugin, String name, Class<C> cClass, boolean lateBuild) {
        super(plugin, name);

        this.cClass = cClass;
        this.commandDispatcher = new CommandDispatcher<>();

        if (!lateBuild) {
            build();
        }
    }

    public BrigadierCommand(P plugin, String name, Class<C> cClass) {
        this(plugin, name, cClass, false);
    }

    public static CommandSyntaxException literal(String message) {
        return new SimpleCommandExceptionType(new LiteralMessage(message)).create();
    }

    public synchronized void build() {
        if (!built) {
            this.commandDispatcher.register(buildCommand(buildRootNode(getName())));
            built = true;
        }
    }

    private String getFullInput(String[] args) {
        StringBuilder builder = new StringBuilder(getName());

        for (String arg : args) {
            builder.append(" ").append(arg);
        }

        return builder.toString();
    }

    protected abstract LiteralArgumentBuilder<C> buildCommand(LiteralArgumentBuilder<C> baseNode);

    protected LiteralArgumentBuilder<C> buildRootNode(String name) {
        return LiteralArgumentBuilder.<C>literal(name).requires(c -> {
            String required = getRequiredPermission();

            if (required == null || required.isEmpty()) {
                return true;
            }

            return c.hasPermission(required);
        });
    }

    protected final String getFullInput(ArgumentIterator args) {
        return getFullInput(args.getArguments());
    }

    @NotNull
    @Override
    public final String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandResult execute(CommandSender sender, ArgumentIterator args) {
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
    public void tabComplete(CommandSuggestions suggestions) {
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
