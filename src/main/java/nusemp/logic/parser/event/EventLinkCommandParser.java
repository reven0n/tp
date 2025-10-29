package nusemp.logic.parser.event;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;

import nusemp.commons.core.index.Index;
import nusemp.logic.commands.event.EventLinkCommand;
import nusemp.logic.parser.ArgumentMultimap;
import nusemp.logic.parser.ArgumentTokenizer;
import nusemp.logic.parser.Parser;
import nusemp.logic.parser.ParserUtil;
import nusemp.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EventLinkCommand object.
 */
public class EventLinkCommandParser implements Parser<EventLinkCommand> {
    private static final String LINK_ALL_KEYWORD = "all";

    /**
     * Parses the given {@code String} of arguments in the context of the EventLinkCommand
     * and returns an EventLinkCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public EventLinkCommand parse(String args) throws ParseException {
        // tokenize the arguments with their respective prefixes
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_EVENT, PREFIX_CONTACT);

        // check if both prefixes are present
        if (!argMultimap.arePrefixesPresent(PREFIX_EVENT, PREFIX_CONTACT)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EventLinkCommand.MESSAGE_USAGE));
        }

        // check for duplicate prefixes
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_EVENT, PREFIX_CONTACT);

        Index eventIndex;
        try {
            eventIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_EVENT).get());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EventLinkCommand.MESSAGE_USAGE), pe);
        }

        String contactValue = argMultimap.getValue(PREFIX_CONTACT).get().trim();

        if (LINK_ALL_KEYWORD.equalsIgnoreCase(contactValue)) {
            return new EventLinkCommand(eventIndex);
        }

        Index contactIndex;
        try {
            contactIndex = ParserUtil.parseIndex(contactValue);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EventLinkCommand.MESSAGE_USAGE), pe);
        }

        return new EventLinkCommand(eventIndex, contactIndex);

//        Index contactIndex;
//
//        // parse the indices
//        try {
//            eventIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_EVENT).get());
//            contactIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_CONTACT).get());
//        } catch (ParseException pe) {
//            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
//                    EventLinkCommand.MESSAGE_USAGE), pe);
//        }
//
//        return new EventLinkCommand(eventIndex, contactIndex);
    }
}
