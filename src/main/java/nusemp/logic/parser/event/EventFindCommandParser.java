package nusemp.logic.parser.event;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static nusemp.logic.parser.CliSyntax.PREFIX_DATE;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;
import static nusemp.logic.parser.CliSyntax.PREFIX_STATUS;
import static nusemp.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import nusemp.logic.commands.event.EventFindCommand;
import nusemp.logic.parser.ArgumentMultimap;
import nusemp.logic.parser.ArgumentTokenizer;
import nusemp.logic.parser.Parser;
import nusemp.logic.parser.exceptions.ParseException;
import nusemp.model.event.Event;
import nusemp.model.event.EventAddressContainsKeywordsPredicate;
import nusemp.model.event.EventDateContainsKeywordsPredicate;
import nusemp.model.event.EventMatchesAnyPredicatePredicate;
import nusemp.model.event.EventNameContainsKeywordsPredicate;
import nusemp.model.event.EventStatusPredicate;
import nusemp.model.event.EventTagContainsKeywordsPredicate;
import nusemp.model.fields.Date;

/**
 * Parses input arguments and creates a new EventFindCommand object
 */
public class EventFindCommandParser implements Parser<EventFindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EventFindCommand
     * and returns an EventFindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EventFindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EventFindCommand.MESSAGE_USAGE));
        }

        ArgumentMultimap argumentMultimap = ArgumentTokenizer
                .tokenize(args, PREFIX_NAME, PREFIX_DATE, PREFIX_ADDRESS, PREFIX_TAG, PREFIX_STATUS);

        // Check if any prefixes are present
        boolean hasNamePrefix = argumentMultimap.getValue(PREFIX_NAME).isPresent();
        boolean hasDatePrefix = argumentMultimap.getValue(PREFIX_DATE).isPresent();
        boolean hasAddressPrefix = argumentMultimap.getValue(PREFIX_ADDRESS).isPresent();
        boolean hasTagPrefix = argumentMultimap.getValue(PREFIX_TAG).isPresent();
        boolean hasStatusPrefix = argumentMultimap.getValue(PREFIX_STATUS).isPresent();
        boolean hasPrefixes = hasNamePrefix || hasDatePrefix || hasAddressPrefix
                || hasTagPrefix || hasStatusPrefix;

        if (!hasPrefixes) {
            // Backward compatibility: treat input as name keywords
            String[] nameKeywords = trimmedArgs.split("\\s+");
            return new EventFindCommand(new EventNameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
        }

        // Build list of predicates based on provided prefixes
        List<Predicate<Event>> predicates = new ArrayList<>();

        if (hasNamePrefix) {
            String nameArgs = argumentMultimap.getValue(PREFIX_NAME).get();
            if (!nameArgs.isEmpty()) {
                String[] nameKeywords = argumentMultimap.getValue(PREFIX_NAME).get().split("\\s+");
                predicates.add(new EventNameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
            }
        }

        if (hasDatePrefix) {
            String dateArgs = argumentMultimap.getValue(PREFIX_DATE).get();
            if (!dateArgs.isEmpty()) {
                try {
                    Date date = new Date(dateArgs);
                    predicates.add(new EventDateContainsKeywordsPredicate(date));
                } catch (IllegalArgumentException e) {
                    throw new ParseException(e.getMessage());
                }
            }
        }

        if (hasAddressPrefix) {
            String addressArgs = argumentMultimap.getValue(PREFIX_ADDRESS).get();
            if (!addressArgs.isEmpty()) {
                String[] addressKeywords = argumentMultimap.getValue(PREFIX_ADDRESS).get().split("\\s+");
                predicates.add(new EventAddressContainsKeywordsPredicate(Arrays.asList(addressKeywords)));
            }
        }

        if (hasTagPrefix) {
            String tagArgs = argumentMultimap.getValue(PREFIX_TAG).get();
            if (!tagArgs.isEmpty()) {
                String[] tagKeywords = argumentMultimap.getValue(PREFIX_TAG).get().split("\\s+");
                predicates.add(new EventTagContainsKeywordsPredicate(Arrays.asList(tagKeywords)));
            }
        }

        if (hasStatusPrefix) {
            String statusArgs = argumentMultimap.getValue(PREFIX_STATUS).get();
            if (!statusArgs.isEmpty()) {
                String[] statusKeywords = argumentMultimap.getValue(PREFIX_STATUS).get().split("\\s+");
                try {
                    predicates.add(new EventStatusPredicate(Arrays.asList(statusKeywords)));
                } catch (IllegalArgumentException e) {
                    throw new ParseException(e.getMessage());
                }
            }
        }

        // If only one predicate, return it directly; otherwise combine with OR logic
        if (predicates.size() == 1) {
            return new EventFindCommand(predicates.get(0));
        } else {
            return new EventFindCommand(new EventMatchesAnyPredicatePredicate(predicates));
        }
    }
}
