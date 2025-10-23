package nusemp.logic.parser.event;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import nusemp.commons.core.index.Index;
import nusemp.logic.commands.event.EventExportCommand;
import nusemp.logic.parser.Parser;
import nusemp.logic.parser.ParserUtil;
import nusemp.logic.parser.exceptions.ParseException;
/**
 * Parses input arguments and creates a new EventExportCommand object
 */
public class EventExportCommandParser implements Parser<EventExportCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the EventExportCommand
     * and returns a EventExportCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EventExportCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new EventExportCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, EventExportCommand.MESSAGE_USAGE), pe);
        }
    }
}
