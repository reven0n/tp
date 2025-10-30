package nusemp.logic.commands;

import nusemp.logic.Messages;
import nusemp.model.Model;

/**
 * Terminates the program.
 */
public class ExitCommand extends Command {

    public static final String COMMAND_WORD = "exit";

    public static final String MESSAGE_EXIT_ACKNOWLEDGEMENT = "Exiting NUS EMP as requested ...";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(MESSAGE_EXIT_ACKNOWLEDGEMENT, Messages.HEADING_PREVIOUS, null, false, true);
    }

}
