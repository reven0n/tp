package nusemp.logic.commands;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.event.EventExportCommand;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;

import org.junit.jupiter.api.Test;

import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithEvents;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_EVENT;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code EventExportCommand}.
 */
public class EventExportCommandTest {

    private Model model = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());


    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredEventList().size() + 1);
        EventExportCommand exportCommand = new EventExportCommand(outOfBoundIndex);

        assertCommandFailure(exportCommand, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredEventList().size() + 1);
        EventExportCommand exportCommand = new EventExportCommand(outOfBoundIndex);

        assertCommandFailure(exportCommand, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        EventExportCommand exportFirstCommand = new EventExportCommand(INDEX_FIRST_EVENT);
        EventExportCommand exportSecondCommand = new EventExportCommand(INDEX_SECOND_EVENT);

        // same object -> returns true
        assertTrue(exportFirstCommand.equals(exportFirstCommand));

        // same values -> returns true
        EventExportCommand exportFirstCommandCopy = new EventExportCommand(INDEX_FIRST_EVENT);
        assertTrue(exportFirstCommand.equals(exportFirstCommandCopy));

        // different types -> returns false
        assertFalse(exportFirstCommand.equals(1));

        // null -> returns false
        assertFalse(exportFirstCommand.equals(null));

        // different event -> returns false
        assertFalse(exportFirstCommand.equals(exportSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        EventExportCommand exportCommand = new EventExportCommand(targetIndex);
        String expected = EventExportCommand.class.getCanonicalName() + "{eventIndex=" + targetIndex + "}";
        assertEquals(expected, exportCommand.toString());
    }
}
