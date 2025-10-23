package nusemp.logic.commands.contact;

import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.logic.commands.CommandTestUtil.showContactAtIndex;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithoutEvent;
import static nusemp.testutil.TypicalEvents.MEETING_EMPTY;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_CONTACT;
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
import nusemp.model.event.Event;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code ContactDeleteCommand}.
 */
public class ContactDeleteCommandTest {

    private Model model = new ModelManager(getTypicalAppDataWithoutEvent(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Contact contactToDelete = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        ContactDeleteCommand contactDeleteCommand = new ContactDeleteCommand(INDEX_FIRST_CONTACT);

        String expectedMessage = String.format(ContactDeleteCommand.MESSAGE_DELETE_CONTACT_SUCCESS,
                Messages.format(contactToDelete));

        ModelManager expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        expectedModel.deleteContact(contactToDelete);

        assertCommandSuccess(contactDeleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_contactLinkedToEvent_removesFromEvent() {
        Model modelWithEvent = new ModelManager(getTypicalAppDataWithoutEvent(), new UserPrefs());
        Contact personToDelete = modelWithEvent.getContactByIndex(INDEX_FIRST_CONTACT);
        Event meetingWithPerson = MEETING_EMPTY.withParticipant(personToDelete);
        modelWithEvent.addEvent(meetingWithPerson);

        ContactDeleteCommand contactDeleteCommand = new ContactDeleteCommand(INDEX_FIRST_CONTACT);

        ModelManager expectedModel = new ModelManager(modelWithEvent.getAppData(), new UserPrefs());
        expectedModel.getEventByIndex(Index.fromOneBased(1)).withoutParticipant(personToDelete);
        expectedModel.deleteContact(personToDelete);

        String expectedMessage = String.format(ContactDeleteCommand.MESSAGE_DELETE_CONTACT_SUCCESS,
                Messages.format(personToDelete));

        assertCommandSuccess(contactDeleteCommand, modelWithEvent, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredContactList().size() + 1);
        ContactDeleteCommand contactDeleteCommand = new ContactDeleteCommand(outOfBoundIndex);

        assertCommandFailure(contactDeleteCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);

        Contact contactToDelete = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        ContactDeleteCommand contactDeleteCommand = new ContactDeleteCommand(INDEX_FIRST_CONTACT);

        String expectedMessage = String.format(ContactDeleteCommand.MESSAGE_DELETE_CONTACT_SUCCESS,
                Messages.format(contactToDelete));

        Model expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        expectedModel.deleteContact(contactToDelete);
        showNoContact(expectedModel);

        assertCommandSuccess(contactDeleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);

        Index outOfBoundIndex = INDEX_SECOND_CONTACT;
        // ensures that outOfBoundIndex is still in bounds of contact list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAppData().getContactList().size());

        ContactDeleteCommand contactDeleteCommand = new ContactDeleteCommand(outOfBoundIndex);

        assertCommandFailure(contactDeleteCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ContactDeleteCommand deleteFirstCommand = new ContactDeleteCommand(INDEX_FIRST_CONTACT);
        ContactDeleteCommand deleteSecondCommand = new ContactDeleteCommand(INDEX_SECOND_CONTACT);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        ContactDeleteCommand deleteFirstCommandCopy = new ContactDeleteCommand(INDEX_FIRST_CONTACT);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different contact -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        ContactDeleteCommand contactDeleteCommand = new ContactDeleteCommand(targetIndex);
        String expected = ContactDeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, contactDeleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoContact(Model model) {
        model.updateFilteredContactList(p -> false);

        assertTrue(model.getFilteredContactList().isEmpty());
    }
}
