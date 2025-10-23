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
 * Shows all contacts part of the event identified using it's displayed index from the event list.
 */
public class EventShowCommand extends Command {

    public static final String COMMAND_WORD = "show";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Shows the contacts that are participating in the event, "
            + "which is identified by the index number used in the displayed event list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " 1";

    public static final String MESSAGE_EVENT_SHOW_SUCCESS =
            "Successfully shown %1$s contact(s) that are part of event:\n%2$s";

    private final Index targetIndex;

    public EventShowCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Event> lastShownList = model.getFilteredEventList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        Event targetEvent = lastShownList.get(targetIndex.getZeroBased());
        model.updateFilteredContactList(targetEvent::hasContact);
        return new CommandResult(String.format(MESSAGE_EVENT_SHOW_SUCCESS,
                model.getFilteredContactList().size(), Messages.format(targetEvent)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventShowCommand)) {
            return false;
        }

        EventShowCommand otherEventShowCommand = (EventShowCommand) other;
        return targetIndex.equals(otherEventShowCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
