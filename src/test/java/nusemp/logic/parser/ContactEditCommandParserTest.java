package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_ADDRESS_DESC_AMY;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_ADDRESS_DESC_BOB;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_EMAIL_DESC_AMY;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_EMAIL_DESC_BOB;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_NAME_DESC_AMY;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_PHONE_DESC_AMY;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_PHONE_DESC_BOB;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_TAG_DESC_FRIEND;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_TAG_DESC_HUSBAND;
import static nusemp.logic.commands.CommandTestUtil.INVALID_CONTACT_ADDRESS_DESC;
import static nusemp.logic.commands.CommandTestUtil.INVALID_CONTACT_EMAIL_DESC;
import static nusemp.logic.commands.CommandTestUtil.INVALID_CONTACT_NAME_DESC;
import static nusemp.logic.commands.CommandTestUtil.INVALID_CONTACT_PHONE_DESC;
import static nusemp.logic.commands.CommandTestUtil.INVALID_CONTACT_TAG_DESC;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_ADDRESS_AMY;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_EMAIL_AMY;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_NAME_AMY;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_PHONE_AMY;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_PHONE_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_TAG_FRIEND;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_TAG_HUSBAND;
import static nusemp.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static nusemp.logic.parser.CliSyntax.PREFIX_EMAIL;
import static nusemp.logic.parser.CliSyntax.PREFIX_PHONE;
import static nusemp.logic.parser.CliSyntax.PREFIX_TAG;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static nusemp.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.ContactEditCommand;
import nusemp.logic.commands.ContactEditCommand.EditPersonDescriptor;
import nusemp.model.person.Email;
import nusemp.model.person.Name;
import nusemp.model.person.Phone;
import nusemp.model.tag.Tag;
import nusemp.testutil.EditPersonDescriptorBuilder;

public class ContactEditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ContactEditCommand.MESSAGE_USAGE);

    private ContactEditCommandParser parser = new ContactEditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_CONTACT_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", ContactEditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + CONTACT_NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + CONTACT_NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_CONTACT_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_CONTACT_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_CONTACT_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1" + INVALID_CONTACT_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "1" + INVALID_CONTACT_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_CONTACT_PHONE_DESC + CONTACT_EMAIL_DESC_AMY,
                Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Person} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "1" + CONTACT_TAG_DESC_FRIEND + CONTACT_TAG_DESC_HUSBAND + TAG_EMPTY,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + CONTACT_TAG_DESC_FRIEND + TAG_EMPTY + CONTACT_TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_EMPTY + CONTACT_TAG_DESC_FRIEND + CONTACT_TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_CONTACT_NAME_DESC + INVALID_CONTACT_EMAIL_DESC
                        + VALID_CONTACT_ADDRESS_AMY + VALID_CONTACT_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = targetIndex.getOneBased() + CONTACT_PHONE_DESC_BOB + CONTACT_TAG_DESC_HUSBAND
                + CONTACT_EMAIL_DESC_AMY + CONTACT_ADDRESS_DESC_AMY + CONTACT_NAME_DESC_AMY + CONTACT_TAG_DESC_FRIEND;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_CONTACT_NAME_AMY)
                .withPhone(VALID_CONTACT_PHONE_BOB).withEmail(VALID_CONTACT_EMAIL_AMY)
                .withAddress(VALID_CONTACT_ADDRESS_AMY)
                .withTags(VALID_CONTACT_TAG_HUSBAND, VALID_CONTACT_TAG_FRIEND).build();
        ContactEditCommand expectedCommand = new ContactEditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + CONTACT_PHONE_DESC_BOB + CONTACT_EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_CONTACT_PHONE_BOB)
                .withEmail(VALID_CONTACT_EMAIL_AMY).build();
        ContactEditCommand expectedCommand = new ContactEditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + CONTACT_NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_CONTACT_NAME_AMY).build();
        ContactEditCommand expectedCommand = new ContactEditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + CONTACT_PHONE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_CONTACT_PHONE_AMY).build();
        expectedCommand = new ContactEditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + CONTACT_EMAIL_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withEmail(VALID_CONTACT_EMAIL_AMY).build();
        expectedCommand = new ContactEditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = targetIndex.getOneBased() + CONTACT_ADDRESS_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withAddress(VALID_CONTACT_ADDRESS_AMY).build();
        expectedCommand = new ContactEditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + CONTACT_TAG_DESC_FRIEND;
        descriptor = new EditPersonDescriptorBuilder().withTags(VALID_CONTACT_TAG_FRIEND).build();
        expectedCommand = new ContactEditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // ContactContactAddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + INVALID_CONTACT_PHONE_DESC + CONTACT_PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + CONTACT_PHONE_DESC_BOB + INVALID_CONTACT_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // mulltiple valid fields repeated
        userInput = targetIndex.getOneBased() + CONTACT_PHONE_DESC_AMY + CONTACT_ADDRESS_DESC_AMY
                + CONTACT_EMAIL_DESC_AMY + CONTACT_TAG_DESC_FRIEND
                + CONTACT_PHONE_DESC_AMY + CONTACT_ADDRESS_DESC_AMY + CONTACT_EMAIL_DESC_AMY + CONTACT_TAG_DESC_FRIEND
                + CONTACT_PHONE_DESC_BOB + CONTACT_ADDRESS_DESC_BOB + CONTACT_EMAIL_DESC_BOB + CONTACT_TAG_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + INVALID_CONTACT_PHONE_DESC
                + INVALID_CONTACT_ADDRESS_DESC + INVALID_CONTACT_EMAIL_DESC
                + INVALID_CONTACT_PHONE_DESC + INVALID_CONTACT_ADDRESS_DESC + INVALID_CONTACT_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        ContactEditCommand expectedCommand = new ContactEditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
