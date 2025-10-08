package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.logic.commands.contact.HelpCommand.SHOWING_HELP_MESSAGE;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.contact.CommandResult;
import nusemp.logic.commands.contact.HelpCommand;
import nusemp.model.Model;
import nusemp.model.ModelManager;

public class HelpCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_help_success() {
        CommandResult expectedCommandResult = new CommandResult(SHOWING_HELP_MESSAGE, true, false);
        assertCommandSuccess(new HelpCommand(), model, expectedCommandResult, expectedModel);
    }
}
