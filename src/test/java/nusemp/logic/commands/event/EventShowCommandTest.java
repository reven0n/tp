package nusemp.logic.commands.event;

import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithEvents;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_EVENT;
import static nusemp.testutil.TypicalIndexes.INDEX_THIRD_EVENT;
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

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code EventShowCommand}.
 */
public class EventShowCommandTest {

    private final Model model = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Event eventToShow = model.getFilteredEventList().get(INDEX_FIRST_EVENT.getZeroBased());
        EventShowCommand eventShowCommand = new EventShowCommand(INDEX_FIRST_EVENT);

        ModelManager expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        expectedModel.updateFilteredContactList(contact -> model.hasParticipant(contact, eventToShow));
        String expectedMessage = String.format(EventShowCommand.MESSAGE_EVENT_SHOW_SUCCESS,
                expectedModel.getFilteredContactList().size(), Messages.format(eventToShow));

        assertCommandSuccess(eventShowCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredEventList().size() + 1);
        EventShowCommand eventShowCommand = new EventShowCommand(outOfBoundIndex);

        assertCommandFailure(eventShowCommand, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showEventAtIndex(model, INDEX_THIRD_EVENT);

        Event eventToShow = model.getEventByIndex(INDEX_FIRST_EVENT);

        EventShowCommand eventShowCommand = new EventShowCommand(INDEX_FIRST_EVENT);

        ModelManager expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        expectedModel.updateFilteredContactList(contact -> model.hasParticipant(contact, eventToShow));
        String expectedMessage = String.format(EventShowCommand.MESSAGE_EVENT_SHOW_SUCCESS,
                expectedModel.getFilteredContactList().size(), Messages.format(eventToShow));
        showEventAtIndex(expectedModel, INDEX_THIRD_EVENT);

        assertCommandSuccess(eventShowCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showEventAtIndex(model, INDEX_FIRST_EVENT);

        Index outOfBoundIndex = INDEX_SECOND_EVENT;
        // ensures that outOfBoundIndex is still in bounds of event list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAppData().getEventList().size());

        EventShowCommand eventShowCommand = new EventShowCommand(outOfBoundIndex);

        assertCommandFailure(eventShowCommand, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        EventShowCommand eventShowCommand1 = new EventShowCommand(INDEX_FIRST_EVENT);
        EventShowCommand eventShowCommand2 = new EventShowCommand(INDEX_SECOND_EVENT);

        // same object -> returns true
        assertTrue(eventShowCommand1.equals(eventShowCommand1));

        // same values -> returns true
        EventShowCommand eventShowCommandCopy = new EventShowCommand(INDEX_FIRST_EVENT);
        assertTrue(eventShowCommand1.equals(eventShowCommandCopy));

        // different types -> returns false
        assertFalse(eventShowCommand1.equals(1));

        // null -> returns false
        assertFalse(eventShowCommand1.equals(null));

        // different event -> returns false
        assertFalse(eventShowCommand1.equals(eventShowCommand2));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        EventShowCommand eventShowCommand = new EventShowCommand(targetIndex);
        String expected = EventShowCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, eventShowCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show only the event at the given {@code targetIndex} in the
     * {@code model}'s event list.
     */
    private void showEventAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredEventList().size());

        Event event = model.getFilteredEventList().get(targetIndex.getZeroBased());
        final String eventName = event.getName().value;
        model.updateFilteredEventList(e -> e.getName().value.equals(eventName));

        assertEquals(1, model.getFilteredEventList().size());
    }
}
