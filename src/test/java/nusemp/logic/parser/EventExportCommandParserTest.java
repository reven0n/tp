package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.event.EventExportCommand;
import nusemp.logic.parser.event.EventExportCommandParser;


/**
 * Contains unit tests for EventExportCommandParser.
 */
public class EventExportCommandParserTest {

    private EventExportCommandParser parser = new EventExportCommandParser();

    @Test
    public void parse_validArgs_returnsEventExportCommand() {
        assertParseSuccess(parser, "1", new EventExportCommand(INDEX_FIRST_EVENT));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // non-numeric argument
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EventExportCommand.MESSAGE_USAGE));

        // negative index
        assertParseFailure(parser, "-1", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EventExportCommand.MESSAGE_USAGE));

        // zero index
        assertParseFailure(parser, "0", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EventExportCommand.MESSAGE_USAGE));

        // empty string
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EventExportCommand.MESSAGE_USAGE));

        // whitespace only
        assertParseFailure(parser, "   ", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EventExportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_multipleArgs_throwsParseException() {
        // multiple indices
        assertParseFailure(parser, "1 2", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EventExportCommand.MESSAGE_USAGE));
    }
}
