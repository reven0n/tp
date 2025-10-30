package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;
import static nusemp.model.Model.PREDICATE_SHOW_ALL_CONTACTS;

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
import nusemp.model.event.Event;

/**
 * Unlinks a contact from an event.
 */
public class EventUnlinkCommand extends Command {
    public static final String COMMAND_WORD = "unlink";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Unlinks contacts from an event identified by the event index.\n"
            + "Parameters: "
            + PREFIX_EVENT + " EVENT_INDEX "
            + PREFIX_CONTACT + " CONTACT_INDEX \n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " "
            + PREFIX_EVENT + " 1 "
            + PREFIX_CONTACT + " 2";

    public static final String MESSAGE_SUCCESS = "Successfully unlinked contact from event:\n%1$s";
    public static final String MESSAGE_CONTACT_NOT_FOUND = "Error unlinking contact: contact not found in event";

    private final Index eventIndex;
    private final Index contactIndex;

    /**
     * Creates an EventUnlinkCommand to unlink the specified contacts from an event
     */
    public EventUnlinkCommand(Index eventIndex, Index contactIndexes) {
        requireNonNull(eventIndex);
        requireNonNull(contactIndexes);
        this.eventIndex = eventIndex;
        this.contactIndex = contactIndexes;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Event> lastShownEventList = model.getFilteredEventList();
        List<Contact> lastShownContactList = model.getFilteredContactList();

        if (eventIndex.getZeroBased() >= lastShownEventList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        if (contactIndex.getZeroBased() >= lastShownContactList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
        }

        Event eventToEdit = lastShownEventList.get(eventIndex.getZeroBased());
        Contact contactToUnlink = lastShownContactList.get(contactIndex.getZeroBased());

        // Check if participant event exists
        if (!model.hasParticipant(contactToUnlink, eventToEdit)) {
            throw new CommandException(MESSAGE_CONTACT_NOT_FOUND);
        }

        // unlink both sides
        try {
            model.removeParticipant(contactToUnlink, eventToEdit);
            model.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
            return new CommandResult(String.format(MESSAGE_SUCCESS, contactToUnlink.getName().toString()),
                    Messages.HEADING_PREVIOUS, null);
        } catch (Exception e) {
            throw new CommandException("An error occurred while unlinking the contact from the event.");
        }

    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof EventUnlinkCommand)) {
            return false;
        }

        EventUnlinkCommand otherCommand = (EventUnlinkCommand) other;
        return eventIndex.equals(otherCommand.eventIndex)
                && contactIndex.equals(otherCommand.contactIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("eventIndex", eventIndex)
                .add("contactIndex", contactIndex)
                .toString();
    }
}
