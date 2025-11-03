package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.Messages.MESSAGE_INVALID_INDEX_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_STATUS;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;

import org.junit.jupiter.api.Test;

import nusemp.logic.Messages;
import nusemp.logic.commands.event.EventExportCommand;
import nusemp.logic.parser.event.EventExportCommandParser;
import nusemp.model.participant.ParticipantStatus;


/**
 * Contains unit tests for EventExportCommandParser.
 */
public class EventExportCommandParserTest {
    public static final String INVALID_COMMAND_MESSAGE = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            EventExportCommand.MESSAGE_USAGE);
    private static final String AVAILABLE_STATUS = " available";
    private EventExportCommandParser parser = new EventExportCommandParser();

    @Test
    public void parse_validArgs_returnsEventExportCommand() {
        assertParseSuccess(parser, "1", new EventExportCommand(INDEX_FIRST_EVENT));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // non-numeric argument
        assertParseFailure(parser, "a", MESSAGE_INVALID_INDEX_FORMAT);

        // negative index
        assertParseFailure(parser, "-1", MESSAGE_INVALID_INDEX_FORMAT);

        // zero index
        assertParseFailure(parser, "0", MESSAGE_INVALID_INDEX_FORMAT);

        // empty string
        assertParseFailure(parser, "", INVALID_COMMAND_MESSAGE);

        // whitespace only
        assertParseFailure(parser, "   ", INVALID_COMMAND_MESSAGE);

        // invalid status
        assertParseFailure(parser, "1 --status going", INVALID_COMMAND_MESSAGE);
    }

    @Test
    public void parse_multipleArgs_throwsParseException() {
        // multiple indices
        assertParseFailure(parser, "1 2 " + PREFIX_STATUS + AVAILABLE_STATUS, MESSAGE_INVALID_INDEX_FORMAT);
    }

    @Test
    public void parse_validArgsWithStatus_returnsEventExportCommand() {
        assertParseSuccess(parser, "1 " + PREFIX_STATUS + AVAILABLE_STATUS,
                new EventExportCommand(INDEX_FIRST_EVENT, ParticipantStatus.AVAILABLE));

        assertParseSuccess(parser, "1 " + PREFIX_STATUS + " unavaiLable       ",
                new EventExportCommand(INDEX_FIRST_EVENT, ParticipantStatus.UNAVAILABLE));

        assertParseSuccess(parser, "1 " + PREFIX_STATUS + " unknown",
                new EventExportCommand(INDEX_FIRST_EVENT, ParticipantStatus.UNKNOWN));
    }

    @Test
    public void parse_duplicateStatusPrefix_throwsParseException() {
        // duplicate status prefix
        assertParseFailure(parser, "1 " + PREFIX_STATUS + AVAILABLE_STATUS + " "
                        + PREFIX_STATUS + " unavailable", Messages.getErrorMessageForDuplicatePrefixes(PREFIX_STATUS));
    }
}
