package nusemp.testutil;

import static nusemp.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static nusemp.logic.parser.CliSyntax.PREFIX_DATE;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;

import nusemp.logic.commands.CommandType;
import nusemp.logic.commands.event.EventAddCommand;
import nusemp.model.event.Event;

/**
 * A utility class for Event.
 */
public class EventUtil {

    /**
     * Returns an add command string for adding the {@code event}.
     */
    public static String getAddCommand(Event event) {
        return CommandType.EVENT + " " + EventAddCommand.COMMAND_WORD + " " + getEventDetails(event);
    }

    /**
     * Returns the part of command string for the given {@code event}'s details.
     */
    public static String getEventDetails(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME).append(event.getName().value).append(" ");
        sb.append(PREFIX_DATE).append(event.getDate().value).append(" ");
        if (event.hasAddress()) {
            sb.append(PREFIX_ADDRESS).append(event.getAddress().value).append(" ");
        }
        return sb.toString();
    }
}
