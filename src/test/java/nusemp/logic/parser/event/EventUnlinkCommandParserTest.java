package nusemp.logic.parser.event;

import static nusemp.logic.Messages.MESSAGE_DUPLICATE_FIELDS;
import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.Messages.MESSAGE_INVALID_INDEX_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.event.EventUnlinkCommand;

public class EventUnlinkCommandParserTest {
    private final EventUnlinkCommandParser parser = new EventUnlinkCommandParser();

    @Test
    public void parse_validArgs_returnsUnlinkCommand() {
        assertParseSuccess(parser, " " + PREFIX_EVENT + "1 " + PREFIX_CONTACT + "1",
                new EventUnlinkCommand(INDEX_FIRST_EVENT, INDEX_FIRST_CONTACT));
    }

    @Test
    public void parse_missingEventPrefix_throwsParseException() {
        assertParseFailure(parser, " " + PREFIX_CONTACT + "1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EventUnlinkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingContactPrefix_throwsParseException() {
        assertParseFailure(parser, " " + PREFIX_EVENT + "1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EventUnlinkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidEventIndex_throwsParseException() {
        assertParseFailure(parser, " " + PREFIX_EVENT + "a " + PREFIX_CONTACT + "1",
                MESSAGE_INVALID_INDEX_FORMAT);
    }

    @Test
    public void parse_invalidContactIndex_throwsParseException() {
        assertParseFailure(parser, " " + PREFIX_EVENT + "1 " + PREFIX_CONTACT + "a",
                MESSAGE_INVALID_INDEX_FORMAT);
    }

    @Test
    public void parse_duplicateEventPrefix_throwsParseException() {
        assertParseFailure(parser, " " + PREFIX_EVENT + "1 " + PREFIX_EVENT + "2 " + PREFIX_CONTACT + "1",
                String.format(MESSAGE_DUPLICATE_FIELDS + PREFIX_EVENT, EventUnlinkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateContactPrefix_throwsParseException() {
        assertParseFailure(parser, " " + PREFIX_EVENT + "1 " + PREFIX_CONTACT + "1 " + PREFIX_CONTACT + "2",
                String.format(MESSAGE_DUPLICATE_FIELDS + PREFIX_CONTACT, EventUnlinkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_nonEmptyPreamble_throwsParseException() {
        assertParseFailure(parser, "some preamble " + PREFIX_EVENT + "1 " + PREFIX_CONTACT + "1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EventUnlinkCommand.MESSAGE_USAGE));
    }
}
