package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import nusemp.commons.core.index.Index;
import nusemp.logic.commands.ContactDeleteCommand;
import nusemp.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ContactDeleteCommand object
 */
public class ContactDeleteCommandParser implements Parser<ContactDeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ContactDeleteCommand
     * and returns a ContactDeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ContactDeleteCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new ContactDeleteCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ContactDeleteCommand.MESSAGE_USAGE), pe);
        }
    }

}
