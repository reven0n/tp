package nusemp.logic.parser.contact;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.contact.ContactFindCommand;
import nusemp.model.contact.ContactMatchesAllPredicates;
import nusemp.model.contact.ContactEmailContainsKeywordsPredicate;
import nusemp.model.contact.ContactNameContainsKeywordsPredicate;
import nusemp.model.contact.ContactTagContainsKeywordsPredicate;

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
                new ContactFindCommand(new ContactNameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedContactFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedContactFindCommand);
    }

    @Test
    public void parse_validNameFlag_returnsFindCommand() {
        ContactFindCommand expectedContactFindCommand =
                new ContactFindCommand(new ContactNameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")));
        assertParseSuccess(parser, " --name Alice Bob", expectedContactFindCommand);
    }

    @Test
    public void parse_validEmailFlag_returnsFindCommand() {
        ContactFindCommand expectedContactFindCommand =
                new ContactFindCommand(new ContactEmailContainsKeywordsPredicate(Arrays.asList("alice", "gmail")));
        assertParseSuccess(parser, " --email alice gmail", expectedContactFindCommand);
    }

    @Test
    public void parse_validTagFlag_returnsFindCommand() {
        ContactFindCommand expectedContactFindCommand =
                new ContactFindCommand(new ContactTagContainsKeywordsPredicate(Arrays.asList("friend", "colleague")));
        assertParseSuccess(parser, " --tag friend colleague", expectedContactFindCommand);
    }

    @Test
    public void parse_multipleFlags_returnsFindCommand() {
        ContactFindCommand expectedContactFindCommand =
                new ContactFindCommand(new ContactMatchesAllPredicates(Arrays.asList(
                        new ContactNameContainsKeywordsPredicate(Arrays.asList("Alice")),
                        new ContactEmailContainsKeywordsPredicate(Arrays.asList("gmail")),
                        new ContactTagContainsKeywordsPredicate(Arrays.asList("friend"))
                )));
        assertParseSuccess(parser, " --name Alice --email gmail --tag friend", expectedContactFindCommand);
    }

    @Test
    public void parse_emptyFlagValue_throwsParseException() {
        assertParseFailure(parser, " --email ", ContactFindCommand.MESSAGE_EMPTY_KEYWORD);
    }

}
