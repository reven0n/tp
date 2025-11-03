package nusemp.logic.parser.contact;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.Messages.MESSAGE_INVALID_INDEX_FORMAT;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.contact.ContactShowCommand;

/**
 * Contains unit tests for ContactShowCommandParser.
 */
public class ContactShowCommandParserTest {

    private final ContactShowCommandParser parser = new ContactShowCommandParser();

    @Test
    public void parse_validArgs_returnsContactShowCommand() {
        assertParseSuccess(parser, "1", new ContactShowCommand(INDEX_FIRST_CONTACT));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", MESSAGE_INVALID_INDEX_FORMAT);
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ContactShowCommand.MESSAGE_USAGE));
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
                ContactShowCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_multipleArgs_throwsParseException() {
        assertParseFailure(parser, "1 2", MESSAGE_INVALID_INDEX_FORMAT);
    }

    @Test
    public void parse_validArgsWithLeadingTrailingSpaces_returnsContactShowCommand() {
        assertParseSuccess(parser, "  1  ", new ContactShowCommand(INDEX_FIRST_CONTACT));
    }
}
