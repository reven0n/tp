package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.logic.commands.CommandTestUtil.showPersonAtIndex;
import static nusemp.testutil.TypicalEvents.MEETING_EMPTY;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static nusemp.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.event.Event;
import nusemp.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code ContactDeleteCommand}.
 */
public class ContactDeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ContactDeleteCommand contactDeleteCommand = new ContactDeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(ContactDeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(contactDeleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_contactLinkedToEvent_removesFromEvent() {
        Model modelWithEvent = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person personToDelete = modelWithEvent.getPersonByIndex(INDEX_FIRST_PERSON);
        Event meetingWithPerson = MEETING_EMPTY.withParticipant(personToDelete);
        modelWithEvent.addEvent(meetingWithPerson);

        ContactDeleteCommand contactDeleteCommand = new ContactDeleteCommand(INDEX_FIRST_PERSON);

        ModelManager expectedModel = new ModelManager(modelWithEvent.getAddressBook(), new UserPrefs());
        expectedModel.getEventByIndex(Index.fromOneBased(1)).withoutParticipant(personToDelete);
        expectedModel.deletePerson(personToDelete);

        String expectedMessage = String.format(ContactDeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        assertCommandSuccess(contactDeleteCommand, modelWithEvent, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        ContactDeleteCommand contactDeleteCommand = new ContactDeleteCommand(outOfBoundIndex);

        assertCommandFailure(contactDeleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ContactDeleteCommand contactDeleteCommand = new ContactDeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(ContactDeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(contactDeleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        ContactDeleteCommand contactDeleteCommand = new ContactDeleteCommand(outOfBoundIndex);

        assertCommandFailure(contactDeleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ContactDeleteCommand deleteFirstCommand = new ContactDeleteCommand(INDEX_FIRST_PERSON);
        ContactDeleteCommand deleteSecondCommand = new ContactDeleteCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        ContactDeleteCommand deleteFirstCommandCopy = new ContactDeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
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
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
