package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.logic.commands.CommandTestUtil.showContactAtIndex;
import static nusemp.testutil.TypicalContacts.getTypicalAppData;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nusemp.logic.commands.contact.ContactListCommand;
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
        model = new ModelManager(getTypicalAppData(), new UserPrefs());
        expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ContactListCommand(), model, String.format(ContactListCommand.MESSAGE_SUCCESS,
                expectedModel.getFilteredContactList().size()), expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);
        assertCommandSuccess(new ContactListCommand(), model, String.format(ContactListCommand.MESSAGE_SUCCESS,
                        expectedModel.getFilteredContactList().size()), expectedModel);
    }
}
