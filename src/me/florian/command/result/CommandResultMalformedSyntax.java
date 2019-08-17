package me.florian.command.result;

import me.florian.command.AbstractCommand;
import org.bukkit.command.CommandSender;

public class CommandResultMalformedSyntax extends CommandResult {

    public CommandResultMalformedSyntax(AbstractCommand command) {
        super(command);
    }

    @Override
    public void finish(CommandSender sender) {
        sendPrefixedMessage(sender, command.getUsageString());
    }
}
