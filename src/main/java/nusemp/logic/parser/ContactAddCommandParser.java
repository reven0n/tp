package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static nusemp.logic.parser.CliSyntax.PREFIX_EMAIL;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;
import static nusemp.logic.parser.CliSyntax.PREFIX_PHONE;
import static nusemp.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import nusemp.logic.commands.ContactAddCommand;
import nusemp.logic.parser.exceptions.ParseException;
import nusemp.model.person.Address;
import nusemp.model.person.Email;
import nusemp.model.person.Name;
import nusemp.model.person.Person;
import nusemp.model.person.Phone;
import nusemp.model.tag.Tag;

/**
 * Parses input arguments and creates a new ContactAddCommand object
 */
public class ContactAddCommandParser implements Parser<ContactAddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ContactAddCommand
     * and returns an ContactAddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ContactAddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);

        if (!argMultimap.arePrefixesPresent(PREFIX_NAME, PREFIX_EMAIL) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ContactAddCommand.MESSAGE_USAGE));
        }

        Person person = createPerson(argMultimap);

        return new ContactAddCommand(person);
    }

    private Person createPerson(ArgumentMultimap argMultimap) throws ParseException {
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).orElse(""));
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).orElse(""));
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        return new Person(name, email, phone, address, tagList);
    }

}
