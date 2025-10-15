package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import nusemp.logic.commands.ContactListCommand;
import nusemp.logic.commands.EventListCommand;
import nusemp.logic.parser.exceptions.ParseException;

public class ContactListCommandParser implements Parser<ContactListCommand>{

    @Override
    public ContactListCommand parse(String args) throws ParseException {
        // Tokenize the arguments
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args);

        // Check if there are any extra parameters provided after the "event list" command
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ContactListCommand.MESSAGE_USAGE));
        }

        // If no extra arguments, return the EventListCommand for execution
        return new ContactListCommand();
    }
}
