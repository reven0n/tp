package nusemp.logic.parser.event;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_STATUS;

import java.util.Optional;

import nusemp.commons.core.index.Index;
import nusemp.logic.commands.event.EventExportCommand;
import nusemp.logic.parser.ArgumentMultimap;
import nusemp.logic.parser.ArgumentTokenizer;
import nusemp.logic.parser.Parser;
import nusemp.logic.parser.ParserUtil;
import nusemp.logic.parser.exceptions.ParseException;
import nusemp.model.participant.ParticipantStatus;

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
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_STATUS);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_STATUS);

        String preamble = argMultimap.getPreamble().trim();
        if (preamble.isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, EventExportCommand.MESSAGE_USAGE));
        }

        Index index = ParserUtil.parseIndex(preamble);
        Optional<String> statusValue = argMultimap.getValue(PREFIX_STATUS);
        if (statusValue.isPresent()) {
            try {
                ParticipantStatus status = ParserUtil.parseStatus(statusValue.get());
                return new EventExportCommand(index, status);
            } catch (ParseException pe) {
                throw new ParseException(String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT, EventExportCommand.MESSAGE_USAGE), pe);
            }
        } else {
            return new EventExportCommand(index);
        }
    }
}
