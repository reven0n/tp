package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;
import static nusemp.model.Model.PREDICATE_SHOW_ALL_CONTACTS;

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
import nusemp.model.event.ParticipantStatus;

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

        Event eventToLink = lastShownEventList.get(eventIndex.getZeroBased());
        Contact contactToLink = lastShownContactList.get(contactIndex.getZeroBased());

        if (model.hasParticipantEvent(contactToLink, eventToLink)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_PARTICIPANT,
                    contactToLink.getEmail()));
        }

        try {
            model.addParticipantEvent(contactToLink, eventToLink, ParticipantStatus.UNKNOWN);
            model.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
            return new CommandResult(MESSAGE_SUCCESS);
        } catch (Exception e) {
            throw new CommandException("Error linking participant to event.");
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
