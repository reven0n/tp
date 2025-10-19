package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalEvents.getTypicalAppDataWithEvents;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.event.EventLinkCommand;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
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
    public void execute_validIndices_success() {
        Index validEventIndex = Index.fromOneBased(1);
        Index validContactIndex = Index.fromOneBased(1);
        Event editedEvent = model.getEventByIndex(validEventIndex).withParticipant(
                model.getContactByIndex(validContactIndex)
        );
        EventLinkCommand eventLinkCommand = new EventLinkCommand(validEventIndex, validContactIndex);
        String expectedMessage = String.format(EventLinkCommand.MESSAGE_SUCCESS,
                model.getContactByIndex(validContactIndex).getName(),
                model.getEventByIndex(validEventIndex).getName()
        );

        Model expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        expectedModel.setEvent(model.getEventByIndex(validEventIndex), editedEvent);

        assertCommandSuccess(eventLinkCommand, model, EventLinkCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
