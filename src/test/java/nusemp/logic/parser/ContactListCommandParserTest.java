package nusemp.logic.parser;

import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import nusemp.logic.Messages;
import nusemp.logic.commands.ContactListCommand;
import nusemp.logic.parser.exceptions.ParseException;

class ContactListCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT = String.format(
            Messages.MESSAGE_INVALID_COMMAND_FORMAT, ContactListCommand.MESSAGE_USAGE);
    private ContactListCommandParser parser = new ContactListCommandParser();

    @Test
    public void parse_validArgs_success() throws ParseException {
        assertParseSuccess(parser, "", new ContactListCommand());
        assertParseSuccess(parser, "     ", new ContactListCommand());
    }

    @Test
    public void parse_invalidArgs_failure() {
        assertParseFailure(parser, " extraArg", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " " + PREFIX_NAME + "John", MESSAGE_INVALID_FORMAT);
    }
}
