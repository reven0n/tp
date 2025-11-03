package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.Messages.MESSAGE_INVALID_INDEX_FORMAT;
import static nusemp.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.CommandType;
import nusemp.logic.commands.ExitCommand;
import nusemp.logic.commands.HelpCommand;
import nusemp.logic.commands.contact.ContactAddCommand;
import nusemp.logic.commands.contact.ContactDeleteCommand;
import nusemp.logic.commands.contact.ContactEditCommand;
import nusemp.logic.commands.contact.ContactEditCommand.EditContactDescriptor;
import nusemp.logic.commands.contact.ContactFindCommand;
import nusemp.logic.commands.contact.ContactListCommand;
import nusemp.logic.commands.contact.ContactShowCommand;
import nusemp.logic.commands.event.EventAddCommand;
import nusemp.logic.commands.event.EventDeleteCommand;
import nusemp.logic.commands.event.EventLinkCommand;
import nusemp.logic.commands.event.EventListCommand;
import nusemp.logic.commands.event.EventShowCommand;
import nusemp.logic.commands.event.EventUnlinkCommand;
import nusemp.logic.parser.event.EventDeleteCommandParser;
import nusemp.logic.parser.exceptions.ParseException;
import nusemp.model.contact.Contact;
import nusemp.model.contact.ContactNameContainsKeywordsPredicate;
import nusemp.model.event.Event;
import nusemp.testutil.ContactBuilder;
import nusemp.testutil.ContactUtil;
import nusemp.testutil.EditContactDescriptorBuilder;
import nusemp.testutil.EventBuilder;
import nusemp.testutil.EventUtil;

public class AppParserTest {

    private final AppParser parser = new AppParser();

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(CommandType.EXIT.toString()) instanceof ExitCommand);
        assertTrue(parser.parseCommand(CommandType.EXIT + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(CommandType.HELP.toString()) instanceof HelpCommand);
        assertTrue(parser.parseCommand(CommandType.HELP + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                HelpCommand.MESSAGE_USAGE), () -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                parser.parseCommand("unknownCommand"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                parser.parseCommand("contact unknownCommand"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                parser.parseCommand("event unknownCommand"));
    }

    @Test
    public void parseCommand_contactAdd() throws Exception {
        Contact contact = new ContactBuilder().build();
        ContactAddCommand command = (ContactAddCommand) parser.parseCommand(ContactUtil.getAddCommand(contact));
        assertEquals(new ContactAddCommand(contact), command);
    }

    @Test
    public void parseCommand_contactDelete() throws Exception {
        ContactDeleteCommand command = (ContactDeleteCommand) parser.parseCommand(CommandType.CONTACT + " "
                + ContactDeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_CONTACT.getOneBased());
        assertEquals(new ContactDeleteCommand(INDEX_FIRST_CONTACT), command);
    }

    @Test
    public void parseCommand_contactEdit() throws Exception {
        Contact contact = new ContactBuilder().build();
        EditContactDescriptor descriptor = new EditContactDescriptorBuilder(contact).build();
        ContactEditCommand command = (ContactEditCommand) parser.parseCommand(CommandType.CONTACT + " "
                + ContactEditCommand.COMMAND_WORD + " " + INDEX_FIRST_CONTACT.getOneBased() + " "
                + ContactUtil.getEditContactDescriptorDetails(descriptor));
        assertEquals(new ContactEditCommand(INDEX_FIRST_CONTACT, descriptor), command);
    }

    @Test
    public void parseCommand_contactFind() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        ContactFindCommand command = (ContactFindCommand) parser.parseCommand(CommandType.CONTACT + " "
                + ContactFindCommand.COMMAND_WORD + " " + String.join(" ", keywords));
        assertEquals(new ContactFindCommand(new ContactNameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_contactList() throws Exception {
        assertTrue(parser.parseCommand(CommandType.CONTACT + " " + ContactListCommand.COMMAND_WORD)
                instanceof ContactListCommand);
        assertTrue(parser.parseCommand(CommandType.EVENT + " " + ContactListCommand.COMMAND_WORD)
                instanceof EventListCommand);
    }

    @Test
    public void parseCommand_contactShow() throws Exception {
        ContactShowCommand command = (ContactShowCommand) parser.parseCommand(CommandType.CONTACT + " "
                + ContactShowCommand.COMMAND_WORD + " " + INDEX_FIRST_CONTACT.getOneBased());
        assertEquals(new ContactShowCommand(INDEX_FIRST_CONTACT), command);
    }

    @Test
    public void parseCommand_eventAdd() throws Exception {
        Event event = new EventBuilder().build();
        EventAddCommand command = (EventAddCommand) parser.parseCommand(EventUtil.getAddCommand(event));
        assertEquals(new EventAddCommand(event), command);
    }

    @Test
    public void parseCommand_eventDelete() throws Exception {
        EventDeleteCommandParser parser = new EventDeleteCommandParser();
        assertParseSuccess(parser, "1", new EventDeleteCommand(INDEX_FIRST_EVENT));
    }

    @Test
    public void parseCommand_eventList() throws Exception {
        assertTrue(parser.parseCommand(CommandType.EVENT + " " + EventListCommand.COMMAND_WORD)
                instanceof EventListCommand);
    }

    @Test
    public void parseCommand_eventLink() throws Exception {
        String command = CommandType.EVENT + " " + EventLinkCommand.COMMAND_WORD + " "
                + PREFIX_EVENT + " 1 " + PREFIX_CONTACT + " 1";
        assertTrue(parser.parseCommand(command) instanceof EventLinkCommand);
    }

    @Test
    public void parseCommand_eventUnlink() throws Exception {
        String command = CommandType.EVENT + " " + EventUnlinkCommand.COMMAND_WORD + " "
                + PREFIX_EVENT + " 1 " + PREFIX_CONTACT + " 1";
        assertTrue(parser.parseCommand(command) instanceof EventUnlinkCommand);
    }

    @Test
    public void parseCommand_eventShow() throws Exception {
        EventShowCommand command = (EventShowCommand) parser.parseCommand(CommandType.EVENT + " "
                + EventShowCommand.COMMAND_WORD + " " + INDEX_FIRST_EVENT.getOneBased());
        assertEquals(new EventShowCommand(INDEX_FIRST_EVENT), command);
    }

    @Test
    public void parseCommand_invalidEventDeleteArgs_throwsParseException() {
        // No index provided
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EventDeleteCommand.MESSAGE_USAGE), () ->
                parser.parseCommand(CommandType.EVENT + " " + EventDeleteCommand.COMMAND_WORD));

        // Invalid index
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX_FORMAT, () ->
                parser.parseCommand(CommandType.EVENT + " " + EventDeleteCommand.COMMAND_WORD + " abc"));
    }

    @Test
    public void parseCommand_unknownEventCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                parser.parseCommand(CommandType.EVENT + " unknownCommand"));
    }

    @Test
    public void parseCommand_missingEventCommandWord_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                HelpCommand.MESSAGE_USAGE), () ->
                parser.parseCommand(CommandType.EVENT.toString()));
    }
}
