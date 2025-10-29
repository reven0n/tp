package nusemp.logic.commands;

import static nusemp.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static nusemp.logic.parser.CliSyntax.PREFIX_DATE;
import static nusemp.logic.parser.CliSyntax.PREFIX_EMAIL;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;
import static nusemp.logic.parser.CliSyntax.PREFIX_PHONE;
import static nusemp.logic.parser.CliSyntax.PREFIX_STATUS;
import static nusemp.logic.parser.CliSyntax.PREFIX_TAG;
import static nusemp.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nusemp.commons.core.index.Index;
import nusemp.logic.commands.contact.ContactEditCommand;
import nusemp.logic.commands.event.EventEditCommand;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.model.AppData;
import nusemp.model.Model;
import nusemp.model.contact.Contact;
import nusemp.model.contact.NameContainsKeywordsPredicate;
import nusemp.model.event.Event;
import nusemp.testutil.EditContactDescriptorBuilder;
import nusemp.testutil.EditEventDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {
    // Valid Contact details
    public static final String VALID_CONTACT_NAME_AMY = "Amy Bee";
    public static final String VALID_CONTACT_NAME_BOB = "Bob Choo";
    public static final String VALID_CONTACT_PHONE_AMY = "11111111";
    public static final String VALID_CONTACT_PHONE_BOB = "22222222";
    public static final String VALID_CONTACT_EMAIL_AMY = "amy@example.com";
    public static final String VALID_CONTACT_EMAIL_BOB = "bob@example.com";
    public static final String VALID_CONTACT_ADDRESS_AMY = "Block 312, Amy Street 1";
    public static final String VALID_CONTACT_ADDRESS_BOB = "Block 123, Bobby Street 3";
    public static final String VALID_CONTACT_TAG_HUSBAND = "husband";
    public static final String VALID_CONTACT_TAG_FRIEND = "friend";

    // valid Event details
    public static final String VALID_EVENT_NAME_MEETING = "MEETING";
    public static final String VALID_EVENT_NAME_CONFERENCE = "CONFERENCE";
    public static final String VALID_EVENT_DATE_MEETING = "01-10-2025 14:00";
    public static final String VALID_EVENT_DATE_CONFERENCE = "29-02-2024 09:00";
    public static final String VALID_EVENT_ADDRESS_MEETING = "Meeting Room A";
    public static final String VALID_EVENT_ADDRESS_CONFERENCE = "Conference Hall B";
    public static final String VALID_EVENT_STATUS_STARTING = "STARTING";
    public static final String VALID_EVENT_STATUS_ONGOING = "ONGOING";
    public static final String VALID_EVENT_STATUS_CLOSED = "CLOSED";
    public static final String VALID_EVENT_TAG_URGENT = "urgent";
    public static final String VALID_EVENT_TAG_IMPORTANT = "important";

    // valid Contact descriptions
    public static final String CONTACT_NAME_DESC_AMY = " " + PREFIX_NAME + VALID_CONTACT_NAME_AMY;
    public static final String CONTACT_NAME_DESC_BOB = " " + PREFIX_NAME + VALID_CONTACT_NAME_BOB;
    public static final String CONTACT_PHONE_DESC_AMY = " " + PREFIX_PHONE + VALID_CONTACT_PHONE_AMY;
    public static final String CONTACT_PHONE_DESC_BOB = " " + PREFIX_PHONE + VALID_CONTACT_PHONE_BOB;
    public static final String CONTACT_EMAIL_DESC_AMY = " " + PREFIX_EMAIL + VALID_CONTACT_EMAIL_AMY;
    public static final String CONTACT_EMAIL_DESC_BOB = " " + PREFIX_EMAIL + VALID_CONTACT_EMAIL_BOB;
    public static final String CONTACT_ADDRESS_DESC_AMY = " " + PREFIX_ADDRESS + VALID_CONTACT_ADDRESS_AMY;
    public static final String CONTACT_ADDRESS_DESC_BOB = " " + PREFIX_ADDRESS + VALID_CONTACT_ADDRESS_BOB;
    public static final String CONTACT_TAG_DESC_FRIEND = " " + PREFIX_TAG + VALID_CONTACT_TAG_FRIEND;
    public static final String CONTACT_TAG_DESC_HUSBAND = " " + PREFIX_TAG + VALID_CONTACT_TAG_HUSBAND;

    // valid Event descriptions
    public static final String EVENT_NAME_DESC_MEETING = " " + PREFIX_NAME + VALID_EVENT_NAME_MEETING;
    public static final String EVENT_NAME_DESC_CONFERENCE = " " + PREFIX_NAME + VALID_EVENT_NAME_CONFERENCE;
    public static final String EVENT_DATE_DESC_MEETING = " " + PREFIX_DATE + VALID_EVENT_DATE_MEETING;
    public static final String EVENT_DATE_DESC_CONFERENCE = " " + PREFIX_DATE + VALID_EVENT_DATE_CONFERENCE;
    public static final String EVENT_ADDRESS_DESC_MEETING = " " + PREFIX_ADDRESS + VALID_EVENT_ADDRESS_MEETING;
    public static final String EVENT_ADDRESS_DESC_CONFERENCE = " " + PREFIX_ADDRESS + VALID_EVENT_ADDRESS_CONFERENCE;
    public static final String EVENT_STATUS_DESC_STARTING = " " + PREFIX_STATUS + VALID_EVENT_STATUS_STARTING;
    public static final String EVENT_STATUS_DESC_ONGOING = " " + PREFIX_STATUS + VALID_EVENT_STATUS_ONGOING;
    public static final String EVENT_STATUS_DESC_CLOSED = " " + PREFIX_STATUS + VALID_EVENT_STATUS_CLOSED;
    public static final String EVENT_TAG_DESC_MUSIC = " " + PREFIX_TAG + "Music";
    public static final String EVENT_TAG_DESC_NETWORKING = " " + PREFIX_TAG + "Networking";
    public static final String EVENT_TAG_DESC_URGENT = " " + PREFIX_TAG + VALID_EVENT_TAG_URGENT;
    public static final String EVENT_TAG_DESC_IMPORTANT = " " + PREFIX_TAG + VALID_EVENT_TAG_IMPORTANT;


    // invalid Contact descriptions
    public static final String INVALID_CONTACT_NAME_DESC = " " + PREFIX_NAME; // cannot be empty
    public static final String INVALID_CONTACT_PHONE_DESC = " " + PREFIX_PHONE + "911a"; // 'a' not allowed in phones
    public static final String INVALID_CONTACT_EMAIL_DESC = " " + PREFIX_EMAIL + "bob!yahoo"; // missing '@' symbol
    public static final String INVALID_CONTACT_TAG_DESC = " " + PREFIX_TAG + "hubby*"; // '*' not allowed in tags

    // invalid Event descriptions
    public static final String INVALID_EVENT_NAME_DESC = " " + PREFIX_NAME + "   "; // name must not be blank
    public static final String INVALID_EVENT_DATE_DESC1 = " " + PREFIX_DATE + "2024-02-30 14:00"; // invalid date
    public static final String INVALID_EVENT_DATE_DESC2 = " " + PREFIX_DATE + "2024-12-01 24:00"; // invalid time
    public static final String INVALID_EVENT_DATE_DESC3 = " " + PREFIX_DATE + "2024/12/01 14:00"; // wrong format
    public static final String INVALID_EVENT_DATE_DESC4 = " " + PREFIX_DATE + "2024-12-01"; // missing time
    public static final String INVALID_EVENT_STATUS_DESC = " " + PREFIX_STATUS + "invalid"; // not a valid status
    public static final String INVALID_EVENT_TAG_DESC = " " + PREFIX_TAG + "Music&"; // '&' not allowed
    public static final String INVALID_EVENT_ADDRESS_DESC = " " + PREFIX_ADDRESS + "   "; // cannot be blank

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final ContactEditCommand.EditContactDescriptor DESC_AMY;
    public static final ContactEditCommand.EditContactDescriptor DESC_BOB;
    public static final EventEditCommand.EditEventDescriptor DESC_EVENT_MEETING;
    public static final EventEditCommand.EditEventDescriptor DESC_EVENT_CONFERENCE;

    static {
        DESC_AMY = new EditContactDescriptorBuilder().withName(VALID_CONTACT_NAME_AMY)
                .withPhone(VALID_CONTACT_PHONE_AMY).withEmail(VALID_CONTACT_EMAIL_AMY)
                .withAddress(VALID_CONTACT_ADDRESS_AMY)
                .withTags(VALID_CONTACT_TAG_FRIEND).build();
        DESC_BOB = new EditContactDescriptorBuilder().withName(VALID_CONTACT_NAME_BOB)
                .withPhone(VALID_CONTACT_PHONE_BOB).withEmail(VALID_CONTACT_EMAIL_BOB)
                .withAddress(VALID_CONTACT_ADDRESS_BOB)
                .withTags(VALID_CONTACT_TAG_HUSBAND, VALID_CONTACT_TAG_FRIEND).build();
        DESC_EVENT_MEETING = new EditEventDescriptorBuilder()
                .withName(VALID_EVENT_NAME_MEETING)
                .withDate(VALID_EVENT_DATE_MEETING)
                .withAddress(VALID_EVENT_ADDRESS_MEETING)
                .withStatus(VALID_EVENT_STATUS_STARTING)
                .withTags(VALID_EVENT_TAG_IMPORTANT)
                .build();
        DESC_EVENT_CONFERENCE = new EditEventDescriptorBuilder()
                .withName(VALID_EVENT_NAME_CONFERENCE)
                .withDate(VALID_EVENT_DATE_CONFERENCE)
                .withAddress(VALID_EVENT_ADDRESS_CONFERENCE)
                .withStatus(VALID_EVENT_STATUS_ONGOING)
                .withTags(VALID_EVENT_TAG_URGENT)
                .build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
            Model expectedModel) {
        try {
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
            Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the app data, filtered contact list and selected contact in {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        AppData expectedAppData = new AppData(actualModel.getAppData());
        List<Contact> expectedFilteredList = new ArrayList<>(actualModel.getFilteredContactList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel));
        assertEquals(expectedAppData, actualModel.getAppData());
        assertEquals(expectedFilteredList, actualModel.getFilteredContactList());
    }
    /**
     * Updates {@code model}'s filtered list to show only the contact at the given {@code targetIndex} in the
     * {@code model}'s app data.
     */
    public static void showContactAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredContactList().size());

        Contact contact = model.getFilteredContactList().get(targetIndex.getZeroBased());
        final String[] splitName = contact.getName().value.split("\\s+");
        model.updateFilteredContactList(new NameContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, model.getFilteredContactList().size());
    }

    /**
     * Updates {@code model}'s filtered event list to show only the event at the given {@code targetIndex} in the
     * {@code model}'s app data.
     */
    public static void showEventAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredEventList().size());

        Event event = model.getFilteredEventList().get(targetIndex.getZeroBased());
        final String eventName = event.getName().value;
        model.updateFilteredEventList(e -> e.getName().value.equals(eventName));

        assertEquals(1, model.getFilteredEventList().size());
    }

}
