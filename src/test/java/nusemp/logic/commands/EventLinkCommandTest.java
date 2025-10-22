package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithEvents;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.event.EventLinkCommand;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;


class EventLinkCommandTest {
    private Model model = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());

    @Test
    public void constructor_nullParameters_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EventLinkCommand(null, Index.fromOneBased(1)));
        assertThrows(NullPointerException.class, () -> new EventLinkCommand(Index.fromOneBased(1), null));
    }

    @Test
    public void execute_invalidIndex_failure() {
        Index outOfBoundEventIndex = Index.fromOneBased(model.getFilteredEventList().size() + 1);
        Index validContactIndex = Index.fromOneBased(1);
        EventLinkCommand eventLinkCommand1 = new EventLinkCommand(outOfBoundEventIndex, validContactIndex);
        assertCommandFailure(eventLinkCommand1, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);

        Index validEventIndex = Index.fromOneBased(1);
        Index outOfBoundContactIndex = Index.fromOneBased(model.getFilteredContactList().size() + 1);
        EventLinkCommand eventLinkCommand2 = new EventLinkCommand(validEventIndex, outOfBoundContactIndex);
        assertCommandFailure(eventLinkCommand2, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_duplicateParticipant_throwsCommandException() {
        Index validEventIndex = Index.fromOneBased(3); // Event already has all particpants linked
        Index validContactIndex = Index.fromOneBased(1);
        EventLinkCommand eventLinkCommand = new EventLinkCommand(validEventIndex, validContactIndex);
        assertCommandFailure(eventLinkCommand, model, String.format(EventLinkCommand.MESSAGE_DUPLICATE_PARTICIPANT,
                model.getContactByIndex(validContactIndex).getEmail()));
    }

    @Test
    public void execute_validIndices_success() throws Exception {
        Index validEventIndex = Index.fromOneBased(1);
        Index validContactIndex = Index.fromOneBased(1);

        Event eventToUpdate = model.getEventByIndex(validEventIndex);
        Contact contactToLink = model.getContactByIndex(validContactIndex);

        // Create both updated objects for bidirectional linking
        Event updatedEvent = eventToUpdate.withParticipantStatus(contactToLink);
        Contact updatedContact = contactToLink.addEvent(updatedEvent);

        EventLinkCommand eventLinkCommand = new EventLinkCommand(validEventIndex, validContactIndex);

        // Set up expected model with both sides of the link
        Model expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        expectedModel.setEvent(eventToUpdate, updatedEvent);
        expectedModel.setContact(contactToLink, updatedContact);

        assertCommandSuccess(eventLinkCommand, model, EventLinkCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
