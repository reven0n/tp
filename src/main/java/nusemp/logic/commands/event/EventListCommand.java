package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;
import static nusemp.model.Model.PREDICATE_SHOW_ALL_EVENTS;

import nusemp.logic.Messages;
import nusemp.logic.commands.Command;
import nusemp.logic.commands.CommandResult;
import nusemp.logic.commands.CommandType;
import nusemp.model.Model;

/**
 * Lists all events to the user.
 */
public class EventListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Successfully listed all %1$s event(s)";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Lists all events.\n\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD;


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredEventList(PREDICATE_SHOW_ALL_EVENTS);
        int size = model.getFilteredEventList().size();
        String heading = size == 0 ? Messages.HEADING_EVENTS_NONE : Messages.HEADING_EVENTS;
        return new CommandResult(String.format(MESSAGE_SUCCESS, size), CommandResult.UiBehavior.SHOW_EVENTS, heading);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof EventListCommand; // instanceof handles nulls
    }
}
