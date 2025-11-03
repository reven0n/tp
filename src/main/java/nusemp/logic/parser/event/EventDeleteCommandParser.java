package nusemp.logic.parser.event;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import nusemp.commons.core.index.Index;
import nusemp.logic.commands.event.EventDeleteCommand;
import nusemp.logic.parser.Parser;
import nusemp.logic.parser.ParserUtil;
import nusemp.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EventDeleteCommand object
 */
public class EventDeleteCommandParser implements Parser<EventDeleteCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the EventDeleteCommand
     * and returns a EventDeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public EventDeleteCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, EventDeleteCommand.MESSAGE_USAGE));
        }
        Index index = ParserUtil.parseIndex(trimmedArgs);
        return new EventDeleteCommand(index);
    }
}
