package nusemp.logic.parser.event;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.Messages.MESSAGE_INVALID_INDEX_FORMAT;
import static nusemp.logic.commands.CommandTestUtil.EVENT_ADDRESS_DESC_CONFERENCE;
import static nusemp.logic.commands.CommandTestUtil.EVENT_ADDRESS_DESC_MEETING;
import static nusemp.logic.commands.CommandTestUtil.EVENT_DATE_DESC_CONFERENCE;
import static nusemp.logic.commands.CommandTestUtil.EVENT_DATE_DESC_MEETING;
import static nusemp.logic.commands.CommandTestUtil.EVENT_NAME_DESC_CONFERENCE;
import static nusemp.logic.commands.CommandTestUtil.EVENT_NAME_DESC_MEETING;
import static nusemp.logic.commands.CommandTestUtil.EVENT_STATUS_DESC_DONE;
import static nusemp.logic.commands.CommandTestUtil.EVENT_STATUS_DESC_ONGOING;
import static nusemp.logic.commands.CommandTestUtil.EVENT_STATUS_DESC_PENDING;
import static nusemp.logic.commands.CommandTestUtil.EVENT_TAG_DESC_IMPORTANT;
import static nusemp.logic.commands.CommandTestUtil.EVENT_TAG_DESC_URGENT;
import static nusemp.logic.commands.CommandTestUtil.INVALID_EVENT_DATE_DESC1;
import static nusemp.logic.commands.CommandTestUtil.INVALID_EVENT_NAME_DESC;
import static nusemp.logic.commands.CommandTestUtil.INVALID_EVENT_STATUS_DESC;
import static nusemp.logic.commands.CommandTestUtil.INVALID_EVENT_TAG_DESC;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_ADDRESS_MEETING;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_DATE_CONFERENCE;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_DATE_MEETING;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_NAME_MEETING;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_STATUS_DONE;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_STATUS_ONGOING;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_STATUS_PENDING;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_TAG_IMPORTANT;
import static nusemp.logic.commands.CommandTestUtil.VALID_EVENT_TAG_URGENT;
import static nusemp.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static nusemp.logic.parser.CliSyntax.PREFIX_DATE;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;
import static nusemp.logic.parser.CliSyntax.PREFIX_STATUS;
import static nusemp.logic.parser.CliSyntax.PREFIX_TAG;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_EVENT;
import static nusemp.testutil.TypicalIndexes.INDEX_THIRD_EVENT;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.event.EventEditCommand;
import nusemp.logic.commands.event.EventEditCommand.EditEventDescriptor;
import nusemp.model.event.EventStatus;
import nusemp.model.fields.Date;
import nusemp.model.fields.Name;
import nusemp.model.fields.Tag;
import nusemp.testutil.EditEventDescriptorBuilder;

/**
 * Contains tests for EventEditCommandParser.
 * Tests from parse_missingParts_failure through parse_resetTags_success
 * authored by @reven0n (PR #179).
 * EventStatus-related tests added subsequently for additional coverage.
 */
public class EventEditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EventEditCommand.MESSAGE_USAGE);

    private EventEditCommandParser parser = new EventEditCommandParser();

    // Tests below (parse_missingParts_failure through parse_resetTags_success) by @reven0n (PR #179)

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_EVENT_NAME_MEETING, MESSAGE_INVALID_INDEX_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EventEditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + EVENT_NAME_DESC_MEETING, MESSAGE_INVALID_INDEX_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + EVENT_NAME_DESC_MEETING, MESSAGE_INVALID_INDEX_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_INDEX_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_INDEX_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, " 1" + INVALID_EVENT_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, " 1" + INVALID_EVENT_DATE_DESC1, Date.MESSAGE_CONSTRAINTS); // invalid date
        assertParseFailure(parser, " 1" + INVALID_EVENT_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid date followed by valid address
        assertParseFailure(parser, " 1" + INVALID_EVENT_DATE_DESC1 + EVENT_ADDRESS_DESC_MEETING,
                Date.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Event} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, " 1" + EVENT_TAG_DESC_IMPORTANT + EVENT_TAG_DESC_URGENT + TAG_EMPTY,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1" + EVENT_TAG_DESC_IMPORTANT + TAG_EMPTY + EVENT_TAG_DESC_URGENT,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1" + TAG_EMPTY + EVENT_TAG_DESC_IMPORTANT + EVENT_TAG_DESC_URGENT,
                Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, " 1" + INVALID_EVENT_NAME_DESC + INVALID_EVENT_DATE_DESC1
                        + VALID_EVENT_ADDRESS_MEETING,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_EVENT;
        String userInput = targetIndex.getOneBased() + EVENT_NAME_DESC_MEETING + EVENT_DATE_DESC_CONFERENCE
                + EVENT_ADDRESS_DESC_MEETING + EVENT_TAG_DESC_IMPORTANT + EVENT_TAG_DESC_URGENT;

        EditEventDescriptor descriptor = new EditEventDescriptorBuilder()
                .withName(VALID_EVENT_NAME_MEETING)
                .withDate(VALID_EVENT_DATE_CONFERENCE)
                .withAddress(VALID_EVENT_ADDRESS_MEETING)
                .withTags(VALID_EVENT_TAG_IMPORTANT, VALID_EVENT_TAG_URGENT)
                .build();
        EventEditCommand expectedCommand = new EventEditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_EVENT;
        String userInput = targetIndex.getOneBased() + EVENT_NAME_DESC_MEETING + EVENT_DATE_DESC_CONFERENCE;

        EditEventDescriptor descriptor = new EditEventDescriptorBuilder()
                .withName(VALID_EVENT_NAME_MEETING)
                .withDate(VALID_EVENT_DATE_CONFERENCE)
                .build();
        EventEditCommand expectedCommand = new EventEditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_EVENT;
        String userInput = targetIndex.getOneBased() + EVENT_NAME_DESC_MEETING;
        EditEventDescriptor descriptor = new EditEventDescriptorBuilder()
                .withName(VALID_EVENT_NAME_MEETING)
                .build();
        EventEditCommand expectedCommand = new EventEditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // date
        userInput = targetIndex.getOneBased() + EVENT_DATE_DESC_MEETING;
        descriptor = new EditEventDescriptorBuilder()
                .withDate(VALID_EVENT_DATE_MEETING)
                .build();
        expectedCommand = new EventEditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = targetIndex.getOneBased() + EVENT_ADDRESS_DESC_MEETING;
        descriptor = new EditEventDescriptorBuilder()
                .withAddress(VALID_EVENT_ADDRESS_MEETING)
                .build();
        expectedCommand = new EventEditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + EVENT_TAG_DESC_IMPORTANT;
        descriptor = new EditEventDescriptorBuilder()
                .withTags(VALID_EVENT_TAG_IMPORTANT)
                .build();
        expectedCommand = new EventEditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_EVENT;
        String userInput = targetIndex.getOneBased() + INVALID_EVENT_DATE_DESC1 + EVENT_DATE_DESC_MEETING;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + EVENT_DATE_DESC_MEETING + INVALID_EVENT_DATE_DESC1;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));

        // multiple valid fields repeated
        userInput = targetIndex.getOneBased() + EVENT_NAME_DESC_MEETING + EVENT_DATE_DESC_MEETING
                + EVENT_ADDRESS_DESC_MEETING + EVENT_TAG_DESC_IMPORTANT
                + EVENT_NAME_DESC_MEETING + EVENT_DATE_DESC_MEETING + EVENT_ADDRESS_DESC_MEETING
                + EVENT_TAG_DESC_IMPORTANT
                + EVENT_NAME_DESC_CONFERENCE + EVENT_DATE_DESC_CONFERENCE + EVENT_ADDRESS_DESC_CONFERENCE
                + EVENT_TAG_DESC_URGENT;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_DATE, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + INVALID_EVENT_NAME_DESC + INVALID_EVENT_DATE_DESC1
                + INVALID_EVENT_NAME_DESC + INVALID_EVENT_DATE_DESC1;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_DATE));
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_EVENT;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditEventDescriptor descriptor = new EditEventDescriptorBuilder().withTags().build();
        EventEditCommand expectedCommand = new EventEditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    // EventStatus-related tests below (parse_statusFieldSpecified_success through parse_duplicateStatusPrefix_failure)

    @Test
    public void parse_statusFieldSpecified_success() {
        // status PENDING
        Index targetIndex = INDEX_THIRD_EVENT;
        String userInput = targetIndex.getOneBased() + EVENT_STATUS_DESC_PENDING;
        EditEventDescriptor descriptor = new EditEventDescriptorBuilder()
                .withStatus(VALID_EVENT_STATUS_PENDING)
                .build();
        EventEditCommand expectedCommand = new EventEditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // status ONGOING
        userInput = targetIndex.getOneBased() + EVENT_STATUS_DESC_ONGOING;
        descriptor = new EditEventDescriptorBuilder()
                .withStatus(VALID_EVENT_STATUS_ONGOING)
                .build();
        expectedCommand = new EventEditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // status DONE
        userInput = targetIndex.getOneBased() + EVENT_STATUS_DESC_DONE;
        descriptor = new EditEventDescriptorBuilder()
                .withStatus(VALID_EVENT_STATUS_DONE)
                .build();
        expectedCommand = new EventEditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidStatus_failure() {
        assertParseFailure(parser, " 1" + INVALID_EVENT_STATUS_DESC, EventStatus.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_statusWithOtherFields_success() {
        Index targetIndex = INDEX_SECOND_EVENT;
        String userInput = targetIndex.getOneBased() + EVENT_NAME_DESC_MEETING
                + EVENT_STATUS_DESC_ONGOING + EVENT_DATE_DESC_CONFERENCE;

        EditEventDescriptor descriptor = new EditEventDescriptorBuilder()
                .withName(VALID_EVENT_NAME_MEETING)
                .withStatus(VALID_EVENT_STATUS_ONGOING)
                .withDate(VALID_EVENT_DATE_CONFERENCE)
                .build();
        EventEditCommand expectedCommand = new EventEditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_duplicateStatusPrefix_failure() {
        Index targetIndex = INDEX_FIRST_EVENT;
        String userInput = targetIndex.getOneBased() + EVENT_STATUS_DESC_PENDING + EVENT_STATUS_DESC_ONGOING;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_STATUS));
    }
}
