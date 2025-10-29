package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;
import static nusemp.model.Model.PREDICATE_SHOW_ALL_CONTACTS;

import java.util.ArrayList;
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
import nusemp.model.participant.ParticipantStatus;

/**
 * Links a contact to an event in the event book.
 */
public class EventLinkCommand extends Command {
    public static final String COMMAND_WORD = "link";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Links a contact or all filtered contacts to an event. "
            + "Parameters: "
            + PREFIX_EVENT + " EVENT_INDEX "
            + PREFIX_CONTACT + " CONTACT_INDEX or 'all'\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " "
            + PREFIX_EVENT + " 1 "
            + PREFIX_CONTACT + " 2\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " "
            + PREFIX_EVENT + " 1 "
            + PREFIX_CONTACT + " all";

    public static final String MESSAGE_SUCCESS = "Successfully linked contact to event";
    public static final String MESSAGE_SUCCESS_ALL = "Successfully linked %1$d contact(s) to event";
    public static final String MESSAGE_DUPLICATE_PARTICIPANT =
            "Error linking event: contact with email %1$s is already linked to the event";
    public static final String MESSAGE_NO_CONTACTS_TO_LINK = "No contacts available to link";

    private final Index eventIndex;
    private final Index contactIndex;
    private final boolean linkAll;

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
        this.linkAll = false;
    }

    public EventLinkCommand(Index eventIndex) {
        requireNonNull(eventIndex);
        this.eventIndex = eventIndex;
        this.contactIndex = null;
        this.linkAll = true;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Event> lastShownEventList = model.getFilteredEventList();

        // check if the event index and contact index are within bounds
        if (eventIndex.getZeroBased() >= lastShownEventList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        Event eventToLink = lastShownEventList.get(eventIndex.getZeroBased());

        if (linkAll) {
            return executeLinkAll(model, eventToLink);
        } else {
            return executeLinkSingle(model, eventToLink);
        }

    }

    private CommandResult executeLinkSingle(Model model, Event eventToLink) throws CommandException {
        List<Contact> lastShownContactList = model.getFilteredContactList();

        if (contactIndex.getZeroBased() >= lastShownContactList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
        }

        Contact contactToLink = lastShownContactList.get(contactIndex.getZeroBased());

        if (model.hasParticipant(contactToLink, eventToLink)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_PARTICIPANT,
                    contactToLink.getEmail()));
        }

        try {
            model.addParticipant(contactToLink, eventToLink, ParticipantStatus.AVAILABLE);
            model.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
            return new CommandResult(MESSAGE_SUCCESS);
        } catch (Exception e) {
            throw new CommandException("Error linking participant to event.");
        }
    }

    private CommandResult executeLinkAll(Model model, Event eventToLink) throws CommandException {
        List<Contact> filteredContactList = model.getFilteredContactList();

        if (filteredContactList.isEmpty()) {
            throw new CommandException(MESSAGE_NO_CONTACTS_TO_LINK);
        }

        int linkedCount = 0;
        List<String> skippedContacts = new ArrayList<>();

        for (Contact contact : filteredContactList) {
            if (!model.hasParticipant(contact, eventToLink)) {
                try {
                    model.addParticipant(contact, eventToLink, ParticipantStatus.AVAILABLE);
                    linkedCount++;
                } catch (Exception e) {
                    skippedContacts.add(contact.getEmail().toString());
                }
            }
        }

        model.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);

        if (linkedCount == 0) {
            throw new CommandException("All contacts are already linked to the event");
        }

        String resultMessage = String.format(MESSAGE_SUCCESS_ALL, linkedCount);
        if (!skippedContacts.isEmpty()) {
            resultMessage += "\nSkipped contacts already linked: " + String.join(", ", skippedContacts);
        }

        return new CommandResult(resultMessage);
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

        if (linkAll != otherCommand.linkAll) {
            return false;
        }

        if (linkAll) {
            return eventIndex.equals(otherCommand.eventIndex);
        }

        return eventIndex.equals(otherCommand.eventIndex)
                && contactIndex.equals(otherCommand.contactIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("eventIndex", eventIndex)
                .add("contactIndex", contactIndex)
                .add("unlinkAll", linkAll)
                .toString();
    }
}
