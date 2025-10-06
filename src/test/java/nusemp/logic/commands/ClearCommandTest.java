package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import nusemp.model.AddressBook;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
