package nusemp.logic.commands;

import static java.util.Objects.requireNonNull;
import static nusemp.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import nusemp.model.Model;

/**
 * Lists all persons in the address book to the user.
 */
public class ContactListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Successfully listed all %1$s contact(s)";

    public static final String MESSAGE_USAGE = CommandType.CONTACT + " " + COMMAND_WORD
            + ": Lists all contacts.\n"
            + "Example: " + CommandType.CONTACT + " " + COMMAND_WORD;


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof ContactListCommand;
    }
}
