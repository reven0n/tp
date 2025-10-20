package nusemp.logic.parser.contact;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import nusemp.logic.commands.contact.ContactListCommand;
import nusemp.logic.parser.ArgumentMultimap;
import nusemp.logic.parser.ArgumentTokenizer;
import nusemp.logic.parser.Parser;
import nusemp.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ContactListCommand object
 */
public class ContactListCommandParser implements Parser<ContactListCommand> {

    @Override
    public ContactListCommand parse(String args) throws ParseException {
        // Tokenize the arguments
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args);

        // Check if there are any extra parameters provided after the "contact list" command
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ContactListCommand.MESSAGE_USAGE));
        }

        // If no extra arguments, return the ContactListCommand for execution
        return new ContactListCommand();
    }
}
