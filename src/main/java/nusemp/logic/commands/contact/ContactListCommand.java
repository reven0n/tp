package nusemp.logic.commands.contact;

import static java.util.Objects.requireNonNull;
import static nusemp.model.Model.PREDICATE_SHOW_ALL_CONTACTS;

import nusemp.logic.Messages;
import nusemp.logic.commands.Command;
import nusemp.logic.commands.CommandResult;
import nusemp.logic.commands.CommandType;
import nusemp.model.Model;

/**
 * Lists all contacts to the user.
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
        model.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
        int size = model.getFilteredContactList().size();
        String heading = size == 0 ? Messages.HEADING_CONTACTS_NONE : Messages.HEADING_CONTACTS;
        return new CommandResult(String.format(MESSAGE_SUCCESS, size), heading, false);
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof ContactListCommand;
    }
}
