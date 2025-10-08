package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import nusemp.model.AddressBook;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;

public class ContactClearCommandTest {

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ContactClearCommand(), model, ContactClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ContactClearCommand(), model, ContactClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
