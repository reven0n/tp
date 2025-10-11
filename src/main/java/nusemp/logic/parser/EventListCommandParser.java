package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import nusemp.logic.commands.EventListCommand;
import nusemp.logic.parser.exceptions.ParseException;

/**
 * Parses the given {@code String} of arguments in the context of the EventListCommand
 * and returns an EventListCommand object for execution.
 * @throws ParseException if the user input does not conform to the expected format.
 */
public class EventListCommandParser implements Parser<EventListCommand> {
    @Override
    public EventListCommand parse(String args) throws ParseException {
        // Tokenize the arguments
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args);

        // Check if there are any extra parameters provided after the "event list" command
        if (!argMultimap.getPreamble().isEmpty() || argMultimap.hasUnexpectedArguments()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EventListCommand.MESSAGE_USAGE));
        }

        // If no extra arguments, return the EventListCommand for execution
        return new EventListCommand();
    }
}
