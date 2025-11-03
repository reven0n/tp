package nusemp.logic.parser.event;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;
import static nusemp.logic.parser.CliSyntax.PREFIX_STATUS;

import nusemp.commons.core.index.Index;
import nusemp.logic.commands.event.EventRsvpCommand;
import nusemp.logic.parser.ArgumentMultimap;
import nusemp.logic.parser.ArgumentTokenizer;
import nusemp.logic.parser.Parser;
import nusemp.logic.parser.ParserUtil;
import nusemp.logic.parser.exceptions.ParseException;
import nusemp.model.participant.ParticipantStatus;

/**
 * Parses the given {@code String} of arguments in the context of the EventRsvpCommand
 * and returns an EventRsvpCommand object for execution.
 */
public class EventRsvpCommandParser implements Parser<EventRsvpCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EventRsvpCommand
     * and returns an EventRsvpCommand object for execution.
     */
    @Override
    public EventRsvpCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_EVENT,
                PREFIX_CONTACT, PREFIX_STATUS);

        // check if all prefixes are present
        if (!argMultimap.arePrefixesPresent(PREFIX_EVENT, PREFIX_CONTACT, PREFIX_STATUS)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EventRsvpCommand.MESSAGE_USAGE));
        }

        // check for duplicate prefixes
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_EVENT, PREFIX_CONTACT, PREFIX_STATUS);

        Index eventIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_EVENT).get());
        Index contactIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_CONTACT).get());
        ParticipantStatus participantStatus = ParserUtil.parseStatus(argMultimap.getValue(PREFIX_STATUS).get());

        return new EventRsvpCommand(eventIndex, contactIndex, participantStatus);
    }
}
