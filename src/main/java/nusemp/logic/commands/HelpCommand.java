package nusemp.logic.commands;

import nusemp.model.Model;

/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = "Type \"help\" to open the user guide.";

    public static final String SHOWING_HELP_MESSAGE = "Opened user guide in browser.";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(SHOWING_HELP_MESSAGE, true, false);
    }
}
