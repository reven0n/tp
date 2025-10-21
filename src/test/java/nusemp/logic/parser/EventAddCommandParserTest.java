package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.commands.CommandTestUtil.EVENT_ADDRESS_DESC_CONFERENCE;
import static nusemp.logic.commands.CommandTestUtil.EVENT_ADDRESS_DESC_MEETING;
import static nusemp.logic.commands.CommandTestUtil.EVENT_DATE_DESC_CONFERENCE;
import static nusemp.logic.commands.CommandTestUtil.EVENT_DATE_DESC_MEETING;
import static nusemp.logic.commands.CommandTestUtil.EVENT_NAME_DESC_CONFERENCE;
import static nusemp.logic.commands.CommandTestUtil.EVENT_NAME_DESC_MEETING;
import static nusemp.logic.commands.CommandTestUtil.INVALID_EVENT_DATE_DESC1;
import static nusemp.logic.commands.CommandTestUtil.INVALID_EVENT_DATE_DESC2;
import static nusemp.logic.commands.CommandTestUtil.INVALID_EVENT_DATE_DESC3;
import static nusemp.logic.commands.CommandTestUtil.INVALID_EVENT_DATE_DESC4;
import static nusemp.logic.commands.CommandTestUtil.INVALID_EVENT_NAME_DESC;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_DATE_MEETING;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_NAME_MEETING;
import static nusemp.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static nusemp.logic.parser.CliSyntax.PREFIX_DATE;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static nusemp.testutil.TypicalEvents.CONFERENCE_EMPTY;
import static nusemp.testutil.TypicalEvents.MEETING_EMPTY;

import org.junit.jupiter.api.Test;

import nusemp.logic.Messages;
import nusemp.logic.commands.event.EventAddCommand;
import nusemp.logic.parser.event.EventAddCommandParser;
import nusemp.model.fields.Date;
import nusemp.model.fields.Name;

class EventAddCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT = String.format(
            MESSAGE_INVALID_COMMAND_FORMAT, EventAddCommand.MESSAGE_USAGE);

    private final EventAddCommandParser parser = new EventAddCommandParser();

    @Test
    public void parse_missingOptionalFields_success() {
        // no address
        assertParseSuccess(parser, EVENT_NAME_DESC_MEETING + EVENT_DATE_DESC_MEETING,
                new EventAddCommand(MEETING_EMPTY));
    }

    @Test
    public void parse_missingCompulsoryFields_failure() {
        // no name prefix
        assertParseFailure(parser, EVENT_DATE_DESC_MEETING, MESSAGE_INVALID_FORMAT);
        // no date prefix
        assertParseFailure(parser, EVENT_NAME_DESC_MEETING, MESSAGE_INVALID_FORMAT);
        // no name and date prefix
        assertParseFailure(parser, VALID_EVENT_NAME_MEETING + VALID_EVENT_DATE_MEETING,
                MESSAGE_INVALID_FORMAT);
        // no arguments
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_duplicateFields_failure() {
        // duplicate name
        assertParseFailure(parser, EVENT_NAME_DESC_MEETING + EVENT_NAME_DESC_MEETING
                + EVENT_DATE_DESC_MEETING, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // duplicate date
        assertParseFailure(parser, EVENT_NAME_DESC_MEETING + EVENT_DATE_DESC_MEETING
                + EVENT_DATE_DESC_MEETING, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));

        // duplicate address
        assertParseFailure(parser, EVENT_NAME_DESC_MEETING + EVENT_ADDRESS_DESC_MEETING + EVENT_DATE_DESC_MEETING
                + EVENT_ADDRESS_DESC_CONFERENCE, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_EVENT_NAME_DESC + EVENT_DATE_DESC_MEETING, Name.MESSAGE_CONSTRAINTS);

        // invalid date
        assertParseFailure(parser, EVENT_NAME_DESC_MEETING + INVALID_EVENT_DATE_DESC1, Date.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, EVENT_NAME_DESC_MEETING + INVALID_EVENT_DATE_DESC2, Date.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, EVENT_NAME_DESC_MEETING + INVALID_EVENT_DATE_DESC3, Date.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, EVENT_NAME_DESC_MEETING + INVALID_EVENT_DATE_DESC4, Date.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_validValue_success() {
        // valid name and date
        assertParseSuccess(parser, EVENT_NAME_DESC_MEETING + EVENT_DATE_DESC_MEETING,
                new EventAddCommand(MEETING_EMPTY));

        // valid name and date with whitespaces
        assertParseSuccess(parser, EVENT_NAME_DESC_CONFERENCE + "   " + EVENT_DATE_DESC_CONFERENCE + "   ",
                new EventAddCommand(CONFERENCE_EMPTY));
    }
}
