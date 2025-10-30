package nusemp.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CommandResultTest {

    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback", "heading", true);

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback", "heading", true)));
        assertTrue(commandResult.equals(new CommandResult("feedback", "heading", true, false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different", "heading", true)));

        // different heading value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", "different", true)));

        // different showEventList value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", "heading", null)));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", "heading", true, true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", "heading", true, false, true)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback", "heading", true);

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback", "heading", true).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback", "heading", true);
        String expected = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=" + commandResult.getFeedbackToUser()
                + ", displayedListHeading=" + commandResult.getDisplayedListHeading()
                + ", showEventList=" + commandResult.isShowEventList()
                + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + "}";
        assertEquals(expected, commandResult.toString());
    }
}
