package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithEvents;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.event.EventExportCommand;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.event.Event;


/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code EventExportCommand}.
 */
public class EventExportCommandTest {

    private Model model = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());

    @BeforeAll
    public static void setupHeadless() {
        // Set headless properties before initializing JavaFX
        System.setProperty("java.awt.headless", "true");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");

        // Initialize JavaFX toolkit
        try {
            new JFXPanel();
        } catch (UnsupportedOperationException e) {
            // JavaFX already initialized or not available in headless mode
            // Tests will still pass as clipboard operations will be mocked
        }
    }

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        Event eventToExport = model.getFilteredEventList().get(INDEX_FIRST_EVENT.getZeroBased());
        EventExportCommand exportCommand = new EventExportCommand(INDEX_FIRST_EVENT);

        String expectedMessage = EventExportCommand.MESSAGE_SUCCESS;

        // Execute on JavaFX thread with CountDownLatch for synchronization
        final CommandResult[] result = new CommandResult[1];
        final Exception[] exception = new Exception[1];
        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                result[0] = exportCommand.execute(model);
            } catch (CommandException e) {
                exception[0] = e;
            } finally {
                latch.countDown();
            }
        });

        // Wait for JavaFX thread to complete with timeout
        latch.await(2, TimeUnit.SECONDS);

        if (exception[0] != null) {
            throw exception[0];
        }

        assertNotNull(result[0]);
        assertEquals(expectedMessage, result[0].getFeedbackToUser());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredEventList().size() + 1);
        EventExportCommand exportCommand = new EventExportCommand(outOfBoundIndex);

        assertCommandFailure(exportCommand, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() throws Exception {
        // Get the event first before filtering
        Event targetEvent = model.getFilteredEventList().get(INDEX_FIRST_EVENT.getZeroBased());

        // Filter the list to show only the target event
        model.updateFilteredEventList(event -> event.equals(targetEvent));

        EventExportCommand exportCommand = new EventExportCommand(INDEX_FIRST_EVENT);

        String expectedMessage = EventExportCommand.MESSAGE_SUCCESS;

        // Execute on JavaFX thread with CountDownLatch for synchronization
        final CommandResult[] result = new CommandResult[1];
        final Exception[] exception = new Exception[1];
        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                result[0] = exportCommand.execute(model);
            } catch (CommandException e) {
                exception[0] = e;
            } finally {
                latch.countDown();
            }
        });

        // Wait for JavaFX thread to complete with timeout
        latch.await(2, TimeUnit.SECONDS);

        if (exception[0] != null) {
            throw exception[0];
        }

        assertNotNull(result[0]);
        assertEquals(expectedMessage, result[0].getFeedbackToUser());
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
