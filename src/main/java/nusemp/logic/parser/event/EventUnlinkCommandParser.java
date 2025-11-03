package nusemp.logic.parser.event;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CliSyntax.LINK_ALL_KEYWORD;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;

import nusemp.commons.core.index.Index;
import nusemp.logic.commands.event.EventUnlinkCommand;
import nusemp.logic.parser.ArgumentMultimap;
import nusemp.logic.parser.ArgumentTokenizer;
import nusemp.logic.parser.Parser;
import nusemp.logic.parser.ParserUtil;
import nusemp.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EventUnlinkCommand object.
 */
public class EventUnlinkCommandParser implements Parser<EventUnlinkCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the EventUnlinkCommand
     * and returns an EventUnlinkCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EventUnlinkCommand parse(String args) throws ParseException {
        // tokenize the arguments with their respective prefixes
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_EVENT, PREFIX_CONTACT);

        // check if both prefixes are present
        if (!argMultimap.arePrefixesPresent(PREFIX_EVENT, PREFIX_CONTACT)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EventUnlinkCommand.MESSAGE_USAGE));
        }

        // check for duplicate prefixes
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_EVENT, PREFIX_CONTACT);

        Index eventIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_EVENT).get());

        String contactValue = argMultimap.getValue(PREFIX_CONTACT).get().trim();

        if (LINK_ALL_KEYWORD.equalsIgnoreCase(contactValue)) {
            return new EventUnlinkCommand(eventIndex);
        }

        Index contactIndex = ParserUtil.parseIndex(contactValue);

        return new EventUnlinkCommand(eventIndex, contactIndex);
    }
}
