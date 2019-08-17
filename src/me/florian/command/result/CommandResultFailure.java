package me.florian.command.result;

import me.florian.command.AbstractCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Formats and sends the given message to the sender after completion
 */
public class CommandResultFailure extends CommandResult {

    private final String message;

    public CommandResultFailure(AbstractCommand command, String message) {
        super(command);

        this.message = message;
    }

    @Override
    public void finish(CommandSender sender) {
        sendPrefixedMessage(sender, message);
    }

    @Override
    protected String getPrefixedMessage(String message) {
        return ChatColor.RED + super.getPrefixedMessage(message);
    }
}
