package nusemp.logic.commands.contact;

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
import nusemp.model.contact.Contact;

/**
 * Shows all events which the contacts are a part of,
 * where the contact is identified using it's displayed index from the contact list.
 */
public class ContactShowCommand extends Command {

    public static final String COMMAND_WORD = "show";

    public static final String MESSAGE_USAGE = CommandType.CONTACT + " " + COMMAND_WORD
            + ": Shows the events that the contact is part of. "
            + "The contact is identified by the index number used in the displayed contact list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + CommandType.CONTACT + " " + COMMAND_WORD + " 1";

    public static final String MESSAGE_CONTACT_SHOW_SUCCESS =
            "Successfully shown %1$s event(s) that has the contact:\n%2$s";

    private final Index targetIndex;

    public ContactShowCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Contact> lastShownList = model.getFilteredContactList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
        }

        Contact targetContact = lastShownList.get(targetIndex.getZeroBased());
        model.updateFilteredEventList(event -> model.hasParticipant(targetContact, event));
        return new CommandResult(String.format(MESSAGE_CONTACT_SHOW_SUCCESS,
                model.getFilteredEventList().size(), Messages.format(targetContact)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ContactShowCommand)) {
            return false;
        }

        ContactShowCommand otherContactShowCommand = (ContactShowCommand) other;
        return targetIndex.equals(otherContactShowCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
