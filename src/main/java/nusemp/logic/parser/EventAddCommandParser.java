package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_DATE;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;

import nusemp.logic.commands.EventAddCommand;
import nusemp.logic.parser.exceptions.ParseException;
import nusemp.model.event.Event;
import nusemp.model.event.EventDate;
import nusemp.model.event.EventName;

/**
 * Parses input arguments and creates a new EventAddCommand object
 */
public class EventAddCommandParser implements Parser<EventAddCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the EventAddCommand
     * and returns an EventAddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public EventAddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_NAME, PREFIX_DATE);
        if (!argMultimap.arePrefixesPresent(PREFIX_NAME, PREFIX_DATE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EventAddCommand.MESSAGE_USAGE));
        }

        Event event = createEvent(argMultimap);

        return new EventAddCommand(event);
    }

    /**
     * Creates an Event object from the given ArgumentMultimap.
     *
     * @param argMultimap the ArgumentMultimap containing the parsed arguments
     * @return the created Event object
     * @throws ParseException if there is an error during parsing
     */
    private Event createEvent(ArgumentMultimap argMultimap) throws ParseException {
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_DATE);
        EventName name = ParserUtil.parseEventName(argMultimap.getValue(PREFIX_NAME).get());
        EventDate date = ParserUtil.parseEventDate(argMultimap.getValue(PREFIX_DATE).get());

        return new Event(name, date);
    }
}
