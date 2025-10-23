package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;

import java.util.List;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.Command;
import nusemp.logic.commands.CommandResult;
import nusemp.logic.commands.CommandType;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.model.Model;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.event.exceptions.DuplicateParticipantException;

/**
 * Links a contact to an event in the event book.
 */
public class EventLinkCommand extends Command {
    public static final String COMMAND_WORD = "link";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Links a contact to an event. "
            + "Parameters: "
            + PREFIX_EVENT + " EVENT_INDEX "
            + PREFIX_CONTACT + " CONTACT_INDEX\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " "
            + PREFIX_EVENT + " 1 "
            + PREFIX_CONTACT + " 2";

    public static final String MESSAGE_SUCCESS = "Successfully linked contact to event";
    public static final String MESSAGE_DUPLICATE_PARTICIPANT =
            "Error linking event: contact with email %1$s is already linked to the event";

    private final Index eventIndex;
    private final Index contactIndex;

    /**
     * Creates an EventLinkCommand to link the specified {@code Contact}
     * to the specified {@code Event}
     *
     * @param eventIndex Index of the event in the filtered event list to link the contact to
     * @param contactIndex Index of the contact in the filtered contact list to be linked to the event
     */
    public EventLinkCommand(Index eventIndex, Index contactIndex) {
        requireNonNull(eventIndex);
        requireNonNull(contactIndex);
        this.eventIndex = eventIndex;
        this.contactIndex = contactIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Event> lastShownEventList = model.getFilteredEventList();
        List<Contact> lastShownContactList = model.getFilteredContactList();

        // check if the event index and contact index are within bounds
        if (eventIndex.getZeroBased() >= lastShownEventList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }
        if (contactIndex.getZeroBased() >= lastShownContactList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
        }

        Event eventToUpdate = lastShownEventList.get(eventIndex.getZeroBased());
        Contact contactToLink = lastShownContactList.get(contactIndex.getZeroBased());

        // Check for duplicate participant
        if (eventToUpdate.hasContactWithEmail(contactToLink.getEmail().value)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_PARTICIPANT,
                    contactToLink.getEmail()));
        }

        // Link both sides
        try {
            Event updatedEvent = eventToUpdate.withContact(contactToLink);
            Contact updatedContact = contactToLink.addEvent(updatedEvent);

            model.setEvent(eventToUpdate, updatedEvent);
            model.setContact(contactToLink, updatedContact);

            return new CommandResult(MESSAGE_SUCCESS);
        } catch (DuplicateParticipantException e) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_PARTICIPANT, contactToLink.getEmail()));
        }
    }


    @Override
    public boolean equals(Object other) {
        // checks for duplicates
        if (other == this) {
            return true;
        }

        if (!(other instanceof EventLinkCommand)) {
            return false;
        }
        EventLinkCommand otherCommand = (EventLinkCommand) other;
        return eventIndex.equals(otherCommand.eventIndex)
                && contactIndex.equals(otherCommand.contactIndex);
    }
}
