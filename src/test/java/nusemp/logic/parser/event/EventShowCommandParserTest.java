package nusemp.logic.parser.event;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.Messages.MESSAGE_INVALID_INDEX_FORMAT;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.event.EventShowCommand;

/**
 * Contains unit tests for EventShowCommandParser.
 */
public class EventShowCommandParserTest {

    private final EventShowCommandParser parser = new EventShowCommandParser();

    @Test
    public void parse_validArgs_returnsEventShowCommand() {
        assertParseSuccess(parser, "1", new EventShowCommand(INDEX_FIRST_EVENT));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", MESSAGE_INVALID_INDEX_FORMAT);
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EventShowCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        // negative index
        assertParseFailure(parser, "-1", MESSAGE_INVALID_INDEX_FORMAT);

        // zero index
        assertParseFailure(parser, "0", MESSAGE_INVALID_INDEX_FORMAT);

        // index too large
        assertParseFailure(parser, "999999999999999999", MESSAGE_INVALID_INDEX_FORMAT);
    }

    @Test
    public void parse_whitespaceArgs_throwsParseException() {
        assertParseFailure(parser, "   ", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EventShowCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_multipleArgs_throwsParseException() {
        assertParseFailure(parser, "1 2", MESSAGE_INVALID_INDEX_FORMAT);
    }

    @Test
    public void parse_validArgsWithLeadingTrailingSpaces_returnsEventShowCommand() {
        assertParseSuccess(parser, "  1  ", new EventShowCommand(INDEX_FIRST_EVENT));
    }
}
