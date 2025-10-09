package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;

import nusemp.logic.commands.ContactFindCommand;
import nusemp.logic.parser.exceptions.ParseException;
import nusemp.model.person.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new ContactFindCommand object
 */
public class ContactFindCommandParser implements Parser<ContactFindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ContactFindCommand
     * and returns a ContactFindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ContactFindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ContactFindCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");

        return new ContactFindCommand(new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
    }

}
