package me.florian.command.result;

import me.florian.command.AbstractCommand;
import org.bukkit.command.CommandSender;

/**
 * Represents an action that is performed after a command finishes Execution normally.
 */
public abstract class CommandResult {

    protected final AbstractCommand command;

    public CommandResult(AbstractCommand command) {
        this.command = command;
    }

    public final AbstractCommand getCommand() {
        return command;
    }

    public abstract void finish(CommandSender sender);

    // region Util methods

    protected void sendPrefixedMessage(CommandSender to, String message) {
        to.sendMessage(getPrefixedMessage(message));
    }

    protected String getPrefixedMessage(String message) {
        return String.format("[%s] %s", command.getPlugin().getName(), message);
    }

    // endregion

}
