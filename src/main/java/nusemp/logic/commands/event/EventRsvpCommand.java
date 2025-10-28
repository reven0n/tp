package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;
import static nusemp.commons.util.CollectionUtil.requireAllNonNull;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;
import static nusemp.logic.parser.CliSyntax.PREFIX_STATUS;

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
import nusemp.model.event.Participant;
import nusemp.model.event.ParticipantStatus;
import nusemp.model.event.exceptions.ParticipantNotFoundException;

/**
 * Handles RSVP actions for events.
 */
public class EventRsvpCommand extends Command {
    public static final String COMMAND_WORD = "rsvp";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": RSVP to an event by its index.\n" + "Parameters:  "
            + PREFIX_EVENT + " EVENT_INDEX " + PREFIX_CONTACT + " CONTACT_INDEX " + PREFIX_STATUS + " STATUS\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " "
            + PREFIX_EVENT + " 1 " + PREFIX_CONTACT + " 2" + " " + PREFIX_STATUS + " "
            + ParticipantStatus.AVAILABLE;

    public static final String MESSAGE_SUCCESS = "Successfully RSVPed to event: %1$s for contact: %2$s";

    public static final String MESSAGE_CONTACT_NOT_PARTICIPANT = "Contact: %1$s is not a participant of event: %2$s";

    private final Index eventIndex;
    private final Index contactIndex;
    private final ParticipantStatus status;

    /**
     * Creates an EventRsvpCommand to change the specified {@code Contact} status
     * in the specified {@code Event}.
     * @param eventIndex
     * @param contactIndex
     */
    public EventRsvpCommand(Index eventIndex, Index contactIndex, ParticipantStatus status) {
        requireAllNonNull(eventIndex, contactIndex, status);
        this.eventIndex = eventIndex;
        this.contactIndex = contactIndex;
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Contact contactToRsvp = getContactFromModel(model);
        Event eventToRsvp = getEventFromModel(model);
        Participant updatedParticipant = new Participant(contactToRsvp, status);

        try {
            Event rsvpedEvent = eventToRsvp.withUpdatedParticipant(updatedParticipant);
            model.setEvent(eventToRsvp, rsvpedEvent);
            return new CommandResult(String.format(MESSAGE_SUCCESS,
                    Messages.format(eventToRsvp), Messages.format(contactToRsvp)));
        } catch (ParticipantNotFoundException e) {
            throw new CommandException(String.format(MESSAGE_CONTACT_NOT_PARTICIPANT,
                    Messages.format(contactToRsvp), Messages.format(eventToRsvp)));
        }
    }

    /**
    * Helper function to get the event from the model based on the event index.
    */
    private Event getEventFromModel(Model model) throws CommandException {
        List<Event> lastShownEventList = model.getFilteredEventList();
        // check if the event index is within bounds
        if (eventIndex.getZeroBased() >= lastShownEventList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        return lastShownEventList.get(eventIndex.getZeroBased());
    }

    /**
     * Helper function to get the contact from the model based on the contact index.
     */
    private Contact getContactFromModel(Model model) throws CommandException {
        List<Contact> lastShownContactList = model.getFilteredContactList();
        if (contactIndex.getZeroBased() >= lastShownContactList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
        }

        return lastShownContactList.get(contactIndex.getZeroBased());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventRsvpCommand)) {
            return false;
        }

        EventRsvpCommand otherEventRsvpCommand = (EventRsvpCommand) other;
        return eventIndex.equals(otherEventRsvpCommand.eventIndex)
                && contactIndex.equals(otherEventRsvpCommand.contactIndex)
                && status == otherEventRsvpCommand.status;
    }
}
