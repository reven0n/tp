package nusemp.logic.commands.contact;

import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithEvents;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_CONTACT;
import static nusemp.testutil.TypicalIndexes.INDEX_THIRD_CONTACT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.contact.Contact;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code ContactShowCommand}.
 */
public class ContactShowCommandTest {

    private final Model model = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Contact contactToShow = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        ContactShowCommand contactShowCommand = new ContactShowCommand(INDEX_FIRST_CONTACT);

        String expectedMessage = String.format(ContactShowCommand.MESSAGE_CONTACT_SHOW_SUCCESS,
                contactToShow.getEvents().size(), Messages.format(contactToShow));

        ModelManager expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        expectedModel.updateFilteredEventList(contactToShow::hasEvent);

        assertCommandSuccess(contactShowCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredContactList().size() + 1);
        ContactShowCommand contactShowCommand = new ContactShowCommand(outOfBoundIndex);

        assertCommandFailure(contactShowCommand, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showContactAtIndex(model, INDEX_THIRD_CONTACT);

        Contact contactToShow = model.getContactByIndex(INDEX_FIRST_CONTACT);

        ContactShowCommand contactShowCommand = new ContactShowCommand(INDEX_FIRST_CONTACT);

        String expectedMessage = String.format(ContactShowCommand.MESSAGE_CONTACT_SHOW_SUCCESS,
                contactToShow.getEvents().size(), Messages.format(contactToShow));

        Model expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        expectedModel.updateFilteredEventList(contactToShow::hasEvent);
        showContactAtIndex(expectedModel, INDEX_THIRD_CONTACT);

        assertCommandSuccess(contactShowCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);

        Index outOfBoundIndex = INDEX_SECOND_CONTACT;
        // ensures that outOfBoundIndex is still in bounds of contact list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAppData().getContactList().size());

        ContactShowCommand contactShowCommand = new ContactShowCommand(outOfBoundIndex);

        assertCommandFailure(contactShowCommand, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ContactShowCommand contactShowCommand1 = new ContactShowCommand(INDEX_FIRST_CONTACT);
        ContactShowCommand contactShowCommand2 = new ContactShowCommand(INDEX_SECOND_CONTACT);

        // same object -> returns true
        assertTrue(contactShowCommand1.equals(contactShowCommand1));

        // same values -> returns true
        ContactShowCommand contactShowCommandCopy = new ContactShowCommand(INDEX_FIRST_CONTACT);
        assertTrue(contactShowCommand1.equals(contactShowCommandCopy));

        // different types -> returns false
        assertFalse(contactShowCommand1.equals(1));

        // null -> returns false
        assertFalse(contactShowCommand1.equals(null));

        // different event -> returns false
        assertFalse(contactShowCommand1.equals(contactShowCommand2));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        ContactShowCommand contactShowCommand = new ContactShowCommand(targetIndex);
        String expected = ContactShowCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, contactShowCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show only the contact at the given {@code targetIndex} in the
     * {@code model}'s contact list.
     */
    private void showContactAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredContactList().size());

        Contact contact = model.getFilteredContactList().get(targetIndex.getZeroBased());
        model.updateFilteredContactList(c -> c.equals(contact));

        assertEquals(1, model.getFilteredContactList().size());
    }
}
