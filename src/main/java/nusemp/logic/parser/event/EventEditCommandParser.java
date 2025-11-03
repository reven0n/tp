package nusemp.logic.parser.event;

import static java.util.Objects.requireNonNull;
import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static nusemp.logic.parser.CliSyntax.PREFIX_DATE;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;
import static nusemp.logic.parser.CliSyntax.PREFIX_STATUS;
import static nusemp.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import nusemp.commons.core.index.Index;
import nusemp.logic.commands.event.EventEditCommand;
import nusemp.logic.commands.event.EventEditCommand.EditEventDescriptor;
import nusemp.logic.parser.ArgumentMultimap;
import nusemp.logic.parser.ArgumentTokenizer;
import nusemp.logic.parser.Parser;
import nusemp.logic.parser.ParserUtil;
import nusemp.logic.parser.exceptions.ParseException;
import nusemp.model.fields.Tag;

/**
 * Parses input arguments and creates a new EventEditCommand object
 */
public class EventEditCommandParser implements Parser<EventEditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EventEditCommand
     * and returns an EventEditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EventEditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DATE, PREFIX_ADDRESS, PREFIX_STATUS, PREFIX_TAG);

        String preamble = argMultimap.getPreamble().trim();
        if (preamble.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EventEditCommand.MESSAGE_USAGE));
        }

        Index index = ParserUtil.parseIndex(preamble);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_DATE, PREFIX_ADDRESS, PREFIX_STATUS);

        EditEventDescriptor editEventDescriptor = new EditEventDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editEventDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_DATE).isPresent()) {
            editEventDescriptor.setDate(ParserUtil.parseDate(argMultimap.getValue(PREFIX_DATE).get()));
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editEventDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }
        if (argMultimap.getValue(PREFIX_STATUS).isPresent()) {
            editEventDescriptor.setStatus(ParserUtil.parseEventStatus(argMultimap.getValue(PREFIX_STATUS).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editEventDescriptor::setTags);

        if (!editEventDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EventEditCommand.MESSAGE_NOT_EDITED);
        }

        return new EventEditCommand(index, editEventDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
