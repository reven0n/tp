package nusemp.logic.parser.contact;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EMAIL;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;
import static nusemp.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import nusemp.logic.commands.contact.ContactFindCommand;
import nusemp.logic.parser.ArgumentMultimap;
import nusemp.logic.parser.ArgumentTokenizer;
import nusemp.logic.parser.Parser;
import nusemp.logic.parser.exceptions.ParseException;
import nusemp.model.contact.Contact;
import nusemp.model.contact.ContactMatchesAnyPredicatePredicate;
import nusemp.model.contact.EmailContainsKeywordsPredicate;
import nusemp.model.contact.NameContainsKeywordsPredicate;
import nusemp.model.contact.TagContainsKeywordsPredicate;

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

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_EMAIL, PREFIX_TAG);

        // Check if any prefixes are present
        boolean hasNamePrefix = argMultimap.getValue(PREFIX_NAME).isPresent();
        boolean hasEmailPrefix = argMultimap.getValue(PREFIX_EMAIL).isPresent();
        boolean hasTagPrefix = argMultimap.getValue(PREFIX_TAG).isPresent();
        boolean hasPrefixes = hasNamePrefix || hasEmailPrefix || hasTagPrefix;

        if (!hasPrefixes) {
            // Backward compatibility: treat input as name keywords
            String[] nameKeywords = trimmedArgs.split("\\s+");
            return new ContactFindCommand(new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
        }

        // Build list of predicates based on which flags are present
        List<Predicate<Contact>> predicates = new ArrayList<>();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            String nameArgs = argMultimap.getValue(PREFIX_NAME).get();
            if (!nameArgs.isEmpty()) {
                String[] nameKeywords = nameArgs.split("\\s+");
                predicates.add(new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
            }
        }

        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            String emailArgs = argMultimap.getValue(PREFIX_EMAIL).get();
            if (!emailArgs.isEmpty()) {
                String[] emailKeywords = emailArgs.split("\\s+");
                predicates.add(new EmailContainsKeywordsPredicate(Arrays.asList(emailKeywords)));
            }
        }

        if (argMultimap.getValue(PREFIX_TAG).isPresent()) {
            String tagArgs = argMultimap.getValue(PREFIX_TAG).get();
            if (!tagArgs.isEmpty()) {
                String[] tagKeywords = tagArgs.split("\\s+");
                predicates.add(new TagContainsKeywordsPredicate(Arrays.asList(tagKeywords)));
            }
        }

        if (predicates.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ContactFindCommand.MESSAGE_USAGE));
        }

        // If only one predicate, return it directly; otherwise combine with OR logic
        if (predicates.size() == 1) {
            return new ContactFindCommand(predicates.get(0));
        } else {
            return new ContactFindCommand(new ContactMatchesAnyPredicatePredicate(predicates));
        }
    }

}
