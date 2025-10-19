package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.contact.ContactFindCommand;
import nusemp.logic.parser.contact.ContactFindCommandParser;
import nusemp.model.contact.NameContainsKeywordsPredicate;

public class ContactFindCommandParserTest {

    private ContactFindCommandParser parser = new ContactFindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ContactFindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        ContactFindCommand expectedContactFindCommand =
                new ContactFindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedContactFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedContactFindCommand);
    }

}
