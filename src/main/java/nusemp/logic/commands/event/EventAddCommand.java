package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;
import static nusemp.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static nusemp.logic.parser.CliSyntax.PREFIX_DATE;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;
import static nusemp.logic.parser.CliSyntax.PREFIX_TAG;

import nusemp.commons.util.ToStringBuilder;
import nusemp.logic.Messages;
import nusemp.logic.commands.Command;
import nusemp.logic.commands.CommandResult;
import nusemp.logic.commands.CommandType;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.model.Model;
import nusemp.model.event.Event;

/**
 * Adds an event to the event book.
 */
public class EventAddCommand extends Command {
    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Adds an event. "
            + "Parameters: "
            + PREFIX_NAME + " NAME "
            + PREFIX_DATE + " DATE "
            + "[" + PREFIX_ADDRESS + " ADDRESS]"
            + "[" + PREFIX_TAG + " TAG]...\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " "
            + PREFIX_NAME + " Team Meeting "
            + PREFIX_DATE + " 25-12-2025 14:30 "
            + PREFIX_ADDRESS + " Conference Room "
            + PREFIX_TAG + " Work "
            + PREFIX_TAG + " Networking";

    public static final String MESSAGE_SUCCESS = "Successfully added event:\n%1$s";
    public static final String MESSAGE_DUPLICATE_EVENT = "Error adding event: event already exists";

    private final Event toAdd;

    /**
     * Creates an EventAddCommand to add the specified {@code Contact}
     */
    public EventAddCommand(Event event) {
        requireNonNull(event);
        toAdd = event;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasEvent(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_EVENT);
        }

        model.addEvent(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventAddCommand)) {
            return false;
        }

        EventAddCommand otherEventAddCommand = (EventAddCommand) other;
        return toAdd.equals(otherEventAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
