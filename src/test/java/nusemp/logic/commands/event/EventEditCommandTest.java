package nusemp.logic.commands.event;

/**
 * Contains tests for EventEditCommand.
 * Original tests (execute_allFieldsSpecifiedUnfilteredList_success through toStringMethod)
 * authored by @reven0n (PR #179).
 * EventStatus-related tests (execute_statusFieldSpecified_success, execute_statusWithOtherFields_success)
 * added subsequently for additional coverage.
 */

import static nusemp.logic.commands.CommandTestUtil.DESC_EVENT_CONFERENCE;
import static nusemp.logic.commands.CommandTestUtil.DESC_EVENT_MEETING;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_NAME_CONFERENCE;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_STATUS_CLOSED;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_STATUS_ONGOING;
import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.logic.commands.CommandTestUtil.showEventAtIndex;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithEvents;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.event.EventEditCommand.EditEventDescriptor;
import nusemp.model.AppData;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.event.Event;
import nusemp.testutil.EditEventDescriptorBuilder;
import nusemp.testutil.EventBuilder;

public class EventEditCommandTest {

    private Model model = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Event editedEvent = new EventBuilder().build();
        EditEventDescriptor descriptor = new EditEventDescriptorBuilder(editedEvent).build();
        EventEditCommand eventEditCommand = new EventEditCommand(INDEX_FIRST_EVENT, descriptor);

        String expectedMessage = String.format(EventEditCommand.MESSAGE_EDIT_EVENT_SUCCESS,
                Messages.format(editedEvent));

        Model expectedModel = new ModelManager(new AppData(model.getAppData()), new UserPrefs());
        expectedModel.setEvent(model.getFilteredEventList().get(0), editedEvent);

        assertCommandSuccess(eventEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EventEditCommand eventEditCommand = new EventEditCommand(INDEX_FIRST_EVENT, new EditEventDescriptor());
        Event editedEvent = model.getFilteredEventList().get(INDEX_FIRST_EVENT.getZeroBased());

        String expectedMessage = String.format(EventEditCommand.MESSAGE_EDIT_EVENT_SUCCESS,
                Messages.format(editedEvent));

        Model expectedModel = new ModelManager(new AppData(model.getAppData()), new UserPrefs());

        assertCommandSuccess(eventEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateEventUnfilteredList_failure() {
        Event firstEvent = model.getFilteredEventList().get(INDEX_FIRST_EVENT.getZeroBased());
        EditEventDescriptor descriptor = new EditEventDescriptorBuilder(firstEvent).build();
        EventEditCommand eventEditCommand = new EventEditCommand(INDEX_SECOND_EVENT, descriptor);

        assertCommandFailure(eventEditCommand, model,
                String.format(EventEditCommand.MESSAGE_DUPLICATE_EVENT, firstEvent.getName()));
    }

    @Test
    public void execute_duplicateEventFilteredList_failure() {
        showEventAtIndex(model, INDEX_FIRST_EVENT);

        Event eventInList = model.getAppData().getEventList().get(INDEX_SECOND_EVENT.getZeroBased());
        EventEditCommand eventEditCommand = new EventEditCommand(INDEX_FIRST_EVENT,
                new EditEventDescriptorBuilder(eventInList).build());

        assertCommandFailure(eventEditCommand, model,
                String.format(EventEditCommand.MESSAGE_DUPLICATE_EVENT, eventInList.getName()));
    }

    @Test
    public void execute_invalidEventIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredEventList().size() + 1);
        EditEventDescriptor descriptor = new EditEventDescriptorBuilder()
                .withName(VALID_EVENT_NAME_CONFERENCE).build();
        EventEditCommand eventEditCommand = new EventEditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(eventEditCommand, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidEventIndexFilteredList_failure() {
        showEventAtIndex(model, INDEX_FIRST_EVENT);
        Index outOfBoundIndex = INDEX_SECOND_EVENT;
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAppData().getEventList().size());

        EventEditCommand eventEditCommand = new EventEditCommand(outOfBoundIndex,
                new EditEventDescriptorBuilder().withName(VALID_EVENT_NAME_CONFERENCE).build());

        assertCommandFailure(eventEditCommand, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EventEditCommand standardCommand = new EventEditCommand(INDEX_FIRST_EVENT, DESC_EVENT_MEETING);

        // same values -> returns true
        EditEventDescriptor copyDescriptor = new EditEventDescriptor(DESC_EVENT_MEETING);
        EventEditCommand commandWithSameValues = new EventEditCommand(INDEX_FIRST_EVENT, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(1));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EventEditCommand(INDEX_SECOND_EVENT, DESC_EVENT_MEETING)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EventEditCommand(INDEX_FIRST_EVENT, DESC_EVENT_CONFERENCE)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditEventDescriptor editEventDescriptor = new EditEventDescriptor();
        EventEditCommand eventEditCommand = new EventEditCommand(index, editEventDescriptor);
        String expected = EventEditCommand.class.getCanonicalName() + "{index=" + index
                + ", editEventDescriptor=" + editEventDescriptor + "}";
        assertEquals(expected, eventEditCommand.toString());
    }

    @Test
    public void execute_statusFieldSpecified_success() {
        Event eventToEdit = model.getFilteredEventList().get(INDEX_FIRST_EVENT.getZeroBased());
        EditEventDescriptor descriptor = new EditEventDescriptorBuilder().withStatus(VALID_EVENT_STATUS_CLOSED).build();
        EventEditCommand eventEditCommand = new EventEditCommand(INDEX_FIRST_EVENT, descriptor);

        Event editedEvent = new EventBuilder(eventToEdit).withStatus(VALID_EVENT_STATUS_CLOSED).build();

        String expectedMessage = String.format(EventEditCommand.MESSAGE_EDIT_EVENT_SUCCESS,
                Messages.format(editedEvent));

        Model expectedModel = new ModelManager(new AppData(model.getAppData()), new UserPrefs());
        expectedModel.setEvent(eventToEdit, editedEvent);

        assertCommandSuccess(eventEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_statusWithOtherFields_success() {
        Event eventToEdit = model.getFilteredEventList().get(INDEX_FIRST_EVENT.getZeroBased());
        EditEventDescriptor descriptor = new EditEventDescriptorBuilder()
                .withAddress("New Conference Hall")
                .withStatus(VALID_EVENT_STATUS_ONGOING)
                .build();
        EventEditCommand eventEditCommand = new EventEditCommand(INDEX_FIRST_EVENT, descriptor);

        Event editedEvent = new EventBuilder(eventToEdit)
                .withAddress("New Conference Hall")
                .withStatus(VALID_EVENT_STATUS_ONGOING)
                .build();

        String expectedMessage = String.format(EventEditCommand.MESSAGE_EDIT_EVENT_SUCCESS,
                Messages.format(editedEvent));

        Model expectedModel = new ModelManager(new AppData(model.getAppData()), new UserPrefs());
        expectedModel.setEvent(eventToEdit, editedEvent);

        assertCommandSuccess(eventEditCommand, model, expectedMessage, expectedModel);
    }
}
