package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_ADDRESS_DESC_AMY;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_ADDRESS_DESC_BOB;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_EMAIL_DESC_AMY;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_EMAIL_DESC_BOB;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_NAME_DESC_AMY;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_NAME_DESC_BOB;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_PHONE_DESC_AMY;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_PHONE_DESC_BOB;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_TAG_DESC_FRIEND;
import static nusemp.logic.commands.CommandTestUtil.CONTACT_TAG_DESC_HUSBAND;
import static nusemp.logic.commands.CommandTestUtil.INVALID_CONTACT_EMAIL_DESC;
import static nusemp.logic.commands.CommandTestUtil.INVALID_CONTACT_NAME_DESC;
import static nusemp.logic.commands.CommandTestUtil.INVALID_CONTACT_PHONE_DESC;
import static nusemp.logic.commands.CommandTestUtil.INVALID_CONTACT_TAG_DESC;
import static nusemp.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static nusemp.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_ADDRESS_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_EMAIL_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_NAME_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_PHONE_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_TAG_FRIEND;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_TAG_HUSBAND;
import static nusemp.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static nusemp.logic.parser.CliSyntax.PREFIX_EMAIL;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;
import static nusemp.logic.parser.CliSyntax.PREFIX_PHONE;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static nusemp.testutil.TypicalContacts.AMY;
import static nusemp.testutil.TypicalContacts.BOB;

import org.junit.jupiter.api.Test;

import nusemp.logic.Messages;
import nusemp.logic.commands.ContactAddCommand;
import nusemp.model.contact.Contact;
import nusemp.model.contact.Email;
import nusemp.model.contact.Name;
import nusemp.model.contact.Phone;
import nusemp.model.tag.Tag;
import nusemp.testutil.ContactBuilder;

public class ContactAddCommandParserTest {
    private ContactAddCommandParser parser = new ContactAddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Contact expectedContact = new ContactBuilder(BOB).withTags(VALID_CONTACT_TAG_FRIEND).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + CONTACT_NAME_DESC_BOB
                + CONTACT_PHONE_DESC_BOB + CONTACT_EMAIL_DESC_BOB
                + CONTACT_ADDRESS_DESC_BOB + CONTACT_TAG_DESC_FRIEND, new ContactAddCommand(expectedContact));


        // multiple tags - all accepted
        Contact expectedContactMultipleTags = new ContactBuilder(BOB)
                .withTags(VALID_CONTACT_TAG_FRIEND, VALID_CONTACT_TAG_HUSBAND)
                .build();
        assertParseSuccess(parser,
                CONTACT_NAME_DESC_BOB + CONTACT_PHONE_DESC_BOB + CONTACT_EMAIL_DESC_BOB
                        + CONTACT_ADDRESS_DESC_BOB + CONTACT_TAG_DESC_HUSBAND + CONTACT_TAG_DESC_FRIEND,
                new ContactAddCommand(expectedContactMultipleTags));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedContactString = CONTACT_NAME_DESC_BOB + CONTACT_PHONE_DESC_BOB + CONTACT_EMAIL_DESC_BOB
                + CONTACT_ADDRESS_DESC_BOB + CONTACT_TAG_DESC_FRIEND;

        // multiple names
        assertParseFailure(parser, CONTACT_NAME_DESC_AMY + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple phones
        assertParseFailure(parser, CONTACT_PHONE_DESC_AMY + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple emails
        assertParseFailure(parser, CONTACT_EMAIL_DESC_AMY + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // multiple addresses
        assertParseFailure(parser, CONTACT_ADDRESS_DESC_AMY + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedContactString + CONTACT_PHONE_DESC_AMY + CONTACT_EMAIL_DESC_AMY
                        + CONTACT_NAME_DESC_AMY + CONTACT_ADDRESS_DESC_AMY
                        + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_CONTACT_NAME_DESC + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, INVALID_CONTACT_EMAIL_DESC + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, INVALID_CONTACT_PHONE_DESC + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid name
        assertParseFailure(parser, validExpectedContactString + INVALID_CONTACT_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, validExpectedContactString + INVALID_CONTACT_EMAIL_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, validExpectedContactString + INVALID_CONTACT_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Contact expectedContact = new ContactBuilder(AMY).withTags().build();
        assertParseSuccess(parser, CONTACT_NAME_DESC_AMY + CONTACT_PHONE_DESC_AMY
                        + CONTACT_EMAIL_DESC_AMY + CONTACT_ADDRESS_DESC_AMY,
                new ContactAddCommand(expectedContact));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ContactAddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_CONTACT_NAME_BOB + CONTACT_PHONE_DESC_BOB
                        + CONTACT_EMAIL_DESC_BOB + CONTACT_ADDRESS_DESC_BOB,
                expectedMessage);


        // missing email prefix
        assertParseFailure(parser, CONTACT_NAME_DESC_BOB + CONTACT_PHONE_DESC_BOB
                        + VALID_CONTACT_EMAIL_BOB + CONTACT_ADDRESS_DESC_BOB,
                expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, VALID_CONTACT_NAME_BOB + VALID_CONTACT_PHONE_BOB + VALID_CONTACT_EMAIL_BOB
                        + VALID_CONTACT_ADDRESS_BOB, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid phone
        assertParseFailure(parser, CONTACT_NAME_DESC_BOB + INVALID_CONTACT_PHONE_DESC
                + CONTACT_EMAIL_DESC_BOB + CONTACT_ADDRESS_DESC_BOB
                + CONTACT_TAG_DESC_HUSBAND + CONTACT_TAG_DESC_FRIEND, Phone.MESSAGE_CONSTRAINTS);

        // invalid email
        assertParseFailure(parser, CONTACT_NAME_DESC_BOB + CONTACT_PHONE_DESC_BOB
                + INVALID_CONTACT_EMAIL_DESC + CONTACT_ADDRESS_DESC_BOB
                + CONTACT_TAG_DESC_HUSBAND + CONTACT_TAG_DESC_FRIEND, Email.MESSAGE_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser, CONTACT_NAME_DESC_BOB + CONTACT_PHONE_DESC_BOB
                + CONTACT_EMAIL_DESC_BOB + CONTACT_ADDRESS_DESC_BOB
                + INVALID_CONTACT_TAG_DESC + VALID_CONTACT_TAG_FRIEND, Tag.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_CONTACT_NAME_DESC + INVALID_CONTACT_PHONE_DESC
                        + CONTACT_EMAIL_DESC_BOB + CONTACT_ADDRESS_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + CONTACT_NAME_DESC_BOB
                        + CONTACT_PHONE_DESC_BOB + CONTACT_EMAIL_DESC_BOB
                + CONTACT_ADDRESS_DESC_BOB + CONTACT_TAG_DESC_HUSBAND + CONTACT_TAG_DESC_FRIEND,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ContactAddCommand.MESSAGE_USAGE));
    }
}
