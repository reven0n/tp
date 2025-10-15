package nusemp.logic.commands;

import static java.util.Objects.requireNonNull;
import static nusemp.model.Model.PREDICATE_SHOW_ALL_EVENTS;

import nusemp.logic.Messages;
import nusemp.model.Model;

/**
 * Lists all persons in the address book to the user.
 */
public class EventListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Lists all events.\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD;


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredEventList(PREDICATE_SHOW_ALL_EVENTS);
        return new CommandResult(String.format(Messages.MESSAGE_EVENTS_LISTED_OVERVIEW,
                model.getFilteredEventList().size()));
    }
}
