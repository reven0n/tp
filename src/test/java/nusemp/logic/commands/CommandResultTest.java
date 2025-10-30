package nusemp.logic.commands;

import static nusemp.logic.commands.CommandResult.UiBehavior.NONE;
import static nusemp.logic.commands.CommandResult.UiBehavior.SHOW_CONTACTS;
import static nusemp.logic.commands.CommandResult.UiBehavior.SHOW_EVENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CommandResultTest {
    public static final String DEFAULT_HEADING = "";
    public static final String FEEDBACK = "feedback";

    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult(FEEDBACK);

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult(FEEDBACK)));
        assertTrue(commandResult.equals(new CommandResult(FEEDBACK, NONE, DEFAULT_HEADING)));
        assertTrue(commandResult.equals(new CommandResult(FEEDBACK, NONE, DEFAULT_HEADING, false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different", NONE, DEFAULT_HEADING)));

        // different behavior value -> returns false
        assertFalse(commandResult.equals(new CommandResult(FEEDBACK, SHOW_CONTACTS, DEFAULT_HEADING)));

        // different heading value -> returns false
        assertFalse(commandResult.equals(new CommandResult(FEEDBACK, NONE, "different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult(FEEDBACK, NONE, DEFAULT_HEADING, true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult(FEEDBACK, NONE, DEFAULT_HEADING, false, true)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult(FEEDBACK, SHOW_EVENTS, "heading");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult(FEEDBACK, SHOW_EVENTS, "heading").hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult(FEEDBACK, SHOW_CONTACTS, "heading");
        String expected = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=" + commandResult.getFeedbackToUser()
                + ", behavior=" + commandResult.getUiBehavior()
                + ", heading=" + commandResult.getHeading()
                + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + "}";
        assertEquals(expected, commandResult.toString());
    }
}
