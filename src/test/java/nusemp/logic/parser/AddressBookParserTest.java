package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.ContactAddCommand;
import nusemp.logic.commands.ContactClearCommand;
import nusemp.logic.commands.ContactDeleteCommand;
import nusemp.logic.commands.ContactEditCommand;
import nusemp.logic.commands.ContactEditCommand.EditPersonDescriptor;
import nusemp.logic.commands.ContactFindCommand;
import nusemp.logic.commands.ContactHelpCommand;
import nusemp.logic.commands.ContactListCommand;
import nusemp.logic.commands.ExitCommand;
import nusemp.logic.parser.exceptions.ParseException;
import nusemp.model.person.NameContainsKeywordsPredicate;
import nusemp.model.person.Person;
import nusemp.testutil.EditPersonDescriptorBuilder;
import nusemp.testutil.PersonBuilder;
import nusemp.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        ContactAddCommand command = (ContactAddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new ContactAddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ContactClearCommand.COMMAND_WORD) instanceof ContactClearCommand);
        assertTrue(parser.parseCommand(ContactClearCommand.COMMAND_WORD + " 3") instanceof ContactClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        ContactDeleteCommand command = (ContactDeleteCommand) parser.parseCommand(
                ContactDeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new ContactDeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        ContactEditCommand command = (ContactEditCommand) parser.parseCommand(ContactEditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new ContactEditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        ContactFindCommand command = (ContactFindCommand) parser.parseCommand(
                ContactFindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new ContactFindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(ContactHelpCommand.COMMAND_WORD) instanceof ContactHelpCommand);
        assertTrue(parser.parseCommand(ContactHelpCommand.COMMAND_WORD + " 3") instanceof ContactHelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ContactListCommand.COMMAND_WORD) instanceof ContactListCommand);
        assertTrue(parser.parseCommand(ContactListCommand.COMMAND_WORD + " 3") instanceof ContactListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ContactHelpCommand.MESSAGE_USAGE), () -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
