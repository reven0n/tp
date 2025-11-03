package nusemp.logic.parser.event;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.Messages.MESSAGE_INVALID_INDEX_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.event.EventLinkCommand;

class EventLinkCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT = String.format(
            MESSAGE_INVALID_COMMAND_FORMAT, EventLinkCommand.MESSAGE_USAGE);

    private EventLinkCommandParser parser = new EventLinkCommandParser();

    @Test
    public void parse_missingCompulsoryFields_failure() {
        // no prefixes
        assertParseFailure(parser, " 1 2", MESSAGE_INVALID_FORMAT);
        // no event prefix
        assertParseFailure(parser, " " + PREFIX_CONTACT + " 1", MESSAGE_INVALID_FORMAT);
        // no contact prefix
        assertParseFailure(parser, " " + PREFIX_EVENT + " 2", MESSAGE_INVALID_FORMAT);
        // no arguments
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_duplicatePrefixes_failure() {
        // duplicate event prefix
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 " + PREFIX_EVENT + " 2 "
                + PREFIX_CONTACT + " 1", Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EVENT));
        // duplicate contact prefix
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 " + PREFIX_CONTACT + " 1 "
                + PREFIX_CONTACT + " 2", Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CONTACT));
        // duplicate both prefixes
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 " + PREFIX_EVENT + " 2 "
                + PREFIX_CONTACT + " 1 " + PREFIX_CONTACT + " 2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EVENT, PREFIX_CONTACT));
    }

    @Test
    public void parse_invalidIndex_failure() {
        // non-numeric event index
        assertParseFailure(parser, " " + PREFIX_EVENT + " a " + PREFIX_CONTACT + " 1", MESSAGE_INVALID_INDEX_FORMAT);
        // non-numeric contact index
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 " + PREFIX_CONTACT + " b", MESSAGE_INVALID_INDEX_FORMAT);
        // zero event index
        assertParseFailure(parser, " " + PREFIX_EVENT + " 0 " + PREFIX_CONTACT + " 1", MESSAGE_INVALID_INDEX_FORMAT);
        // zero contact index
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 " + PREFIX_CONTACT + " 0", MESSAGE_INVALID_INDEX_FORMAT);
        // negative event index
        assertParseFailure(parser, " " + PREFIX_EVENT + " -1 " + PREFIX_CONTACT + " 1", MESSAGE_INVALID_INDEX_FORMAT);
        // negative contact index
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 " + PREFIX_CONTACT + " -1", MESSAGE_INVALID_INDEX_FORMAT);
    }

    @Test
    public void parse_validValue_success() throws Exception {
        // valid indices
        assertParseSuccess(parser, " " + PREFIX_EVENT + " 1 " + PREFIX_CONTACT + " 2 ",
                new EventLinkCommand(Index.fromOneBased(1), Index.fromOneBased(2)));

        // valid indices with extra spaces
        assertParseSuccess(parser, "   " + PREFIX_EVENT + " 3   " + PREFIX_CONTACT + " 4   ",
                new EventLinkCommand(Index.fromOneBased(3), Index.fromOneBased(4)));
    }
}
