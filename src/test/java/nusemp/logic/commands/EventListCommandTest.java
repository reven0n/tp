package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalEvents.getTypicalAddressBookWithEvents;
import static nusemp.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;

class EventListCommandTest {
    private Model model = new ModelManager(getTypicalAddressBookWithEvents(), new UserPrefs());

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            new EventListCommand().execute(null);
        });
    }

    @Test
    public void execute_validModel_success() {
        assertCommandSuccess(new EventListCommand(), model, "4 events listed!", model);

        Model anotherModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertCommandSuccess(new EventListCommand(), anotherModel,
                "0 events listed!", anotherModel);
    }
}
