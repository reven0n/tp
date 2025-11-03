package nusemp.logic.parser.event;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.Messages.MESSAGE_INVALID_INDEX_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;
import static nusemp.logic.parser.CliSyntax.PREFIX_STATUS;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.event.EventRsvpCommand;
import nusemp.model.participant.ParticipantStatus;

class EventRsvpCommandParserTest {
    private static final String AVAILABLE = " " + ParticipantStatus.AVAILABLE.toString();
    private static final String UNAVAILABLE = " " + ParticipantStatus.UNAVAILABLE.toString();
    private static final String MESSAGE_INVALID_FORMAT = String.format(
            MESSAGE_INVALID_COMMAND_FORMAT, EventRsvpCommand.MESSAGE_USAGE);

    private EventRsvpCommandParser parser = new EventRsvpCommandParser();

    @Test
    public void parse_missingCompulsoryFields_failure() {
        // no arguments
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        // no prefixes
        assertParseFailure(parser, " 1 2 3", MESSAGE_INVALID_FORMAT);
        // no event prefix
        assertParseFailure(parser, " 1 " + PREFIX_CONTACT + " 2 " + PREFIX_STATUS + AVAILABLE,
                MESSAGE_INVALID_FORMAT);
        // no contact prefix
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 2 " + PREFIX_STATUS + AVAILABLE,
                MESSAGE_INVALID_FORMAT);
        // no status prefix
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 " + PREFIX_CONTACT + " 2 " + AVAILABLE,
                MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_duplicatePrefixes_failure() {
        // duplicate event prefix
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 " + PREFIX_EVENT + " 2 "
                + PREFIX_CONTACT + " 1 " + PREFIX_STATUS + AVAILABLE,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EVENT));
        // duplicate contact prefix
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 " + PREFIX_CONTACT + " 1 "
                + PREFIX_CONTACT + " 2 " + PREFIX_STATUS + AVAILABLE,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CONTACT));
        // duplicate status prefix
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 " + PREFIX_CONTACT + " 1 "
                + PREFIX_STATUS + AVAILABLE + " " + PREFIX_STATUS + " unavailable",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_STATUS));
        // duplicate all prefixes
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 " + PREFIX_EVENT + " 2 "
                + PREFIX_CONTACT + " 1 " + PREFIX_CONTACT + " 2 "
                + PREFIX_STATUS + AVAILABLE + " " + PREFIX_STATUS + " unavailable",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EVENT, PREFIX_CONTACT, PREFIX_STATUS));
    }

    @Test
    public void parse_invalidIndex_failure() {
        // non-numeric event index
        assertParseFailure(parser, " " + PREFIX_EVENT + " a "
                + PREFIX_CONTACT + " 1 " + PREFIX_STATUS + AVAILABLE, MESSAGE_INVALID_INDEX_FORMAT);
        // non-numeric contact index
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 "
                + PREFIX_CONTACT + " b " + PREFIX_STATUS + AVAILABLE, MESSAGE_INVALID_INDEX_FORMAT);
        // zero event index
        assertParseFailure(parser, " " + PREFIX_EVENT + " 0 "
                + PREFIX_CONTACT + " 1 " + PREFIX_STATUS + AVAILABLE, MESSAGE_INVALID_INDEX_FORMAT);
        // zero contact index
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 "
                + PREFIX_CONTACT + " 0 " + PREFIX_STATUS + AVAILABLE, MESSAGE_INVALID_INDEX_FORMAT);
        // negative event index
        assertParseFailure(parser, " " + PREFIX_EVENT + " -1 "
                + PREFIX_CONTACT + " 1 " + PREFIX_STATUS + AVAILABLE, MESSAGE_INVALID_INDEX_FORMAT);
        // negative contact index
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 "
                + PREFIX_CONTACT + " -1 " + PREFIX_STATUS + AVAILABLE, MESSAGE_INVALID_INDEX_FORMAT);
    }

    @Test
    public void parse_invalidStatus_failure() {
        // invalid status value
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 "
                + PREFIX_CONTACT + " 1 " + PREFIX_STATUS + " going", ParticipantStatus.MESSAGE_CONSTRAINTS);
        // empty status value
        assertParseFailure(parser, " " + PREFIX_EVENT + " 1 "
                + PREFIX_CONTACT + " 1 " + PREFIX_STATUS + " ", ParticipantStatus.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_validArgs_success() {
        // normal case
        assertParseSuccess(parser, " " + PREFIX_EVENT + " 1 "
                + PREFIX_CONTACT + " 2 " + PREFIX_STATUS + AVAILABLE,
                new EventRsvpCommand(Index.fromOneBased(1), Index.fromOneBased(2),
                        ParticipantStatus.AVAILABLE));

        // leading and trailing spaces
        assertParseSuccess(parser, "   " + PREFIX_EVENT + " 3 "
                + PREFIX_CONTACT + " 4 " + PREFIX_STATUS + " UNavailable   ",
                new EventRsvpCommand(Index.fromOneBased(3), Index.fromOneBased(4),
                        ParticipantStatus.UNAVAILABLE));
    }
}
