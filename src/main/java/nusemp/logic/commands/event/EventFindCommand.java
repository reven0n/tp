package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;
import static nusemp.commons.util.StringUtil.prependLines;
import static nusemp.logic.Messages.MESSAGE_EVENTS_LISTED_OVERVIEW;

import java.util.function.Predicate;

import nusemp.commons.util.ToStringBuilder;
import nusemp.logic.Messages;
import nusemp.logic.commands.Command;
import nusemp.logic.commands.CommandResult;
import nusemp.logic.commands.CommandType;
import nusemp.model.Model;
import nusemp.model.event.Event;

/**
 * Finds and lists all events whose fields contain any of the argument keywords.
 * Keyword matching is case-insensitive.
 */
public class EventFindCommand extends Command {
    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Finds events by searching their fields (case-insensitive).\n\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]... OR --FIELD KEYWORD [MORE_KEYWORDS]...\n"
            + "Available fields: name, date, address, tag, status\n"
            + "Examples:\n"
            + "  " + CommandType.EVENT + " " + COMMAND_WORD + " meeting\n"
            + "  " + CommandType.EVENT + " " + COMMAND_WORD + " --name meeting conference\n"
            + "  " + CommandType.EVENT + " " + COMMAND_WORD + " --date 12-01-2023 --tag work";

    public static final String MESSAGE_EMPTY_KEYWORD = "Search keywords cannot be empty.\n"
            + "Please provide at least one keyword after the field prefix.\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " --name meeting";

    private final Predicate<Event> predicate;
    private final String conditions;

    /**
     * Creates an EventFindCommand to find events matching the given predicate.
     */
    public EventFindCommand(Predicate<Event> predicate) {
        this(predicate, "");
    }

    /**
     * Creates an EventFindCommand to find events matching the given predicate.
     *
     * @param predicate The predicate to filter events.
     * @param conditions The string representation of the search conditions.
     */
    public EventFindCommand(Predicate<Event> predicate, String conditions) {
        this.predicate = predicate;
        this.conditions = conditions;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredEventList(predicate);
        int size = model.getFilteredEventList().size();
        String feedbackToUser = String.format(MESSAGE_EVENTS_LISTED_OVERVIEW, size);
        String heading = String.format(size == 0 ? Messages.HEADING_EVENT_FIND_NONE : Messages.HEADING_EVENT_FIND,
                prependLines(conditions, "    ")).trim();
        return new CommandResult(feedbackToUser, CommandResult.UiBehavior.SHOW_EVENTS, heading);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventFindCommand)) {
            return false;
        }

        return predicate.equals(((EventFindCommand) other).predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
