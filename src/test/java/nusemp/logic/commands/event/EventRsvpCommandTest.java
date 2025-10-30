package nusemp.logic.commands.event;

import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalAppData.getTypicalAppData;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;
import static nusemp.testutil.TypicalIndexes.INDEX_THIRD_EVENT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.Command;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.participant.ParticipantStatus;

class EventRsvpCommandTest {
    private Model model = new ModelManager(getTypicalAppData(), new UserPrefs());
    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EventRsvpCommand(
                Index.fromOneBased(1), null, null));
        assertThrows(NullPointerException.class, () -> new EventRsvpCommand(
                null, Index.fromOneBased(1), null));
        assertThrows(NullPointerException.class, () -> new EventRsvpCommand(
                null, null, ParticipantStatus.AVAILABLE));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        EventRsvpCommand command = new EventRsvpCommand(Index.fromOneBased(1),
                Index.fromOneBased(1), ParticipantStatus.AVAILABLE);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_contactNotParticipant_throwsCommandException() {
        Index validEventIndex = INDEX_FIRST_EVENT;
        Index validContactIndex = INDEX_FIRST_CONTACT;

        Contact contactToRsvp = model.getContactByIndex(validContactIndex);
        Event eventToUpdate = model.getEventByIndex(validEventIndex);
        ParticipantStatus validStatus = ParticipantStatus.UNAVAILABLE;

        // Confirm contact is not a participant of the event
        assertFalse(model.hasParticipant(contactToRsvp, eventToUpdate));

        Command eventRsvpCommand = new EventRsvpCommand(validEventIndex, validContactIndex, validStatus);

        assertCommandFailure(eventRsvpCommand, model, String.format(EventRsvpCommand.MESSAGE_CONTACT_NOT_PARTICIPANT,
                contactToRsvp.getName(), eventToUpdate.getName()));
    }

    @Test
    public void execute_invalidIndexes_throwsCommandException() {
        // Test invalid event index
        Index outOfBoundEventIndex = Index.fromOneBased(model.getFilteredEventList().size() + 1);
        EventRsvpCommand command1 = new EventRsvpCommand(
                outOfBoundEventIndex, INDEX_FIRST_CONTACT, ParticipantStatus.AVAILABLE);
        assertCommandFailure(command1, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);

        // Test invalid contact index
        Index outOfBoundContactIndex = Index.fromOneBased(model.getFilteredContactList().size() + 1);
        EventRsvpCommand command2 = new EventRsvpCommand(
                INDEX_THIRD_EVENT, outOfBoundContactIndex, ParticipantStatus.AVAILABLE);
        assertCommandFailure(command2, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_sameStatus_success() {
        Index validEventIndex = INDEX_THIRD_EVENT;
        Index validContactIndex = INDEX_FIRST_CONTACT;
        ParticipantStatus currentStatus = ParticipantStatus.AVAILABLE;

        Event eventToUpdate = model.getEventByIndex(validEventIndex);
        Contact contactToRsvp = model.getContactByIndex(validContactIndex);

        EventRsvpCommand command = new EventRsvpCommand(validEventIndex, validContactIndex, currentStatus);

        // Create a new model with same data for comparison
        Model expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        String expectedMessage = String.format(EventRsvpCommand.MESSAGE_SUCCESS,
                eventToUpdate.getName(), contactToRsvp.getName(), currentStatus);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validInputs_success() {
        Index validEventIndex = Index.fromOneBased(3);
        Index validContactIndex = Index.fromOneBased(1);
        ParticipantStatus newStatus = ParticipantStatus.UNAVAILABLE;

        Event eventToUpdate = model.getEventByIndex(validEventIndex);
        Contact contactToRsvp = model.getContactByIndex(validContactIndex);

        assertTrue(model.hasParticipant(contactToRsvp, eventToUpdate)); // Confirm participant exists

        EventRsvpCommand command = new EventRsvpCommand(validEventIndex, validContactIndex, newStatus);

        Model expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        expectedModel.setParticipant(contactToRsvp, eventToUpdate, newStatus);

        assertCommandSuccess(command, model,
                String.format(EventRsvpCommand.MESSAGE_SUCCESS,
                        eventToUpdate.getName(), contactToRsvp.getName(), newStatus), expectedModel);
    }
}
