package nusemp.logic.parser.contact;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import nusemp.commons.core.index.Index;
import nusemp.logic.commands.contact.ContactShowCommand;
import nusemp.logic.parser.Parser;
import nusemp.logic.parser.ParserUtil;
import nusemp.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ContactShowCommand object
 */
public class ContactShowCommandParser implements Parser<ContactShowCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ContactShowCommand
     * and returns a ContactShowCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ContactShowCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ContactShowCommand.MESSAGE_USAGE));
        }
        Index index = ParserUtil.parseIndex(trimmedArgs);
        return new ContactShowCommand(index);
    }

}
