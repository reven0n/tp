package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.logic.commands.CommandTestUtil.showPersonAtIndex;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static nusemp.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ContactListCommand.
 */
public class ContactListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ContactListCommand(), model, ContactListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ContactListCommand(), model, ContactListCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
