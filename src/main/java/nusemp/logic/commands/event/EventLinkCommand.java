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
            + ": Links a contact or all filtered contacts to an event. \n\n"
            + "Parameters: "
            + PREFIX_EVENT + " EVENT_INDEX "
            + PREFIX_CONTACT + " CONTACT_INDEX or 'all'\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " "
            + PREFIX_EVENT + " 1 "
            + PREFIX_CONTACT + " 2\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " "
            + PREFIX_EVENT + " 1 "
            + PREFIX_CONTACT + " all\n\n"
            + "Note: EVENT_INDEX and CONTACT_INDEX must be a positive integer within the size of the displayed "
            + "event list and contact list respectively.";

    public static final String MESSAGE_SUCCESS = "Successfully linked contact \"%1$s\" to event \"%2$s\"";
    public static final String MESSAGE_SUCCESS_ALL = "Successfully linked %1$d contact(s) to event \"%2$s\". "
            + "\nContacts linked: ";
    public static final String MESSAGE_DUPLICATE_PARTICIPANT =
            "Error linking event: contact with email \"%1$s\" is already linked to the event";
    public static final String MESSAGE_NO_CONTACTS_TO_LINK = "No contacts available to link";

    private final Index eventIndex;
    private final Index contactIndex;
    private final boolean isLinkAll;

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
        this.isLinkAll = false;
    }

    /**
     * Creates an EventLinkCommand to link all filtered {@code Contact}s
     * to the specified {@code Event}
     *
     * @param eventIndex Index of the event in the filtered event list to link all contacts to
     */
    public EventLinkCommand(Index eventIndex) {
        requireNonNull(eventIndex);
        this.eventIndex = eventIndex;
        this.contactIndex = null;
        this.isLinkAll = true;
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

        if (isLinkAll) {
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

        model.addParticipant(contactToLink, eventToLink, ParticipantStatus.UNKNOWN);
        model.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, contactToLink.getName(), eventToLink.getName()));
    }

    private CommandResult executeLinkAll(Model model, Event eventToLink) throws CommandException {
        List<Contact> filteredContactList = model.getFilteredContactList();

        if (filteredContactList.isEmpty()) {
            throw new CommandException(MESSAGE_NO_CONTACTS_TO_LINK);
        }

        List<String> linkedContacts = new ArrayList<>();
        List<String> skippedContacts = new ArrayList<>();

        for (Contact contact : filteredContactList) {
            if (!model.hasParticipant(contact, eventToLink)) {
                try {
                    model.addParticipant(contact, eventToLink, ParticipantStatus.UNKNOWN);
                    linkedContacts.add(contact.getName().toString());
                } catch (Exception e) {
                    skippedContacts.add(contact.getName().toString());
                }
            }
        }

        model.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);

        if (linkedContacts.isEmpty()) {
            throw new CommandException("All contacts are already linked to the event");
        }

        String resultMessage = String.format(MESSAGE_SUCCESS_ALL, linkedContacts.size(), eventToLink.getName());
        resultMessage += String.join(", ", linkedContacts);
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

        if (isLinkAll != otherCommand.isLinkAll) {
            return false;
        }

        if (isLinkAll) {
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
                .add("unlinkAll", isLinkAll)
                .toString();
    }
}
