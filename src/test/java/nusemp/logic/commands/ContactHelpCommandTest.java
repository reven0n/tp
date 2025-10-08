package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.logic.commands.ContactHelpCommand.SHOWING_HELP_MESSAGE;

import org.junit.jupiter.api.Test;

import nusemp.model.Model;
import nusemp.model.ModelManager;

public class ContactHelpCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_help_success() {
        CommandResult expectedCommandResult = new CommandResult(SHOWING_HELP_MESSAGE, true, false);
        assertCommandSuccess(new ContactHelpCommand(), model, expectedCommandResult, expectedModel);
    }
}
