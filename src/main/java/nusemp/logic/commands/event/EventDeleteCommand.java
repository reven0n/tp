package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;

import java.util.List;

import nusemp.commons.core.index.Index;
import nusemp.commons.util.ToStringBuilder;
import nusemp.logic.Messages;
import nusemp.logic.commands.Command;
import nusemp.logic.commands.CommandResult;
import nusemp.logic.commands.CommandType;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.model.Model;
import nusemp.model.event.Event;

/**
 * Deletes a event identified using it's displayed index from the event list.
 */
public class EventDeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Deletes the event identified by the index number used in the displayed event list.\n\n"
            + "Parameters: INDEX\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " 1\n\n"
            + "Note: INDEX must be a positive integer within the size of the displayed event list.";

    public static final String MESSAGE_DELETE_EVENT_SUCCESS = "Successfully deleted event:\n%1$s";

    private final Index targetIndex;

    public EventDeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Event> lastShownList = model.getFilteredEventList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        Event eventToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deleteEvent(eventToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_EVENT_SUCCESS, Messages.format(eventToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventDeleteCommand)) {
            return false;
        }

        EventDeleteCommand otherEventDeleteCommand = (EventDeleteCommand) other;
        return targetIndex.equals(otherEventDeleteCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
