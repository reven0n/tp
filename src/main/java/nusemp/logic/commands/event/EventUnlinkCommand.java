package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;

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

/**
 * Unlinks a contact from an event.
 */
public class EventUnlinkCommand extends Command {
    public static final String COMMAND_WORD = "unlink";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Unlinks contacts from an event identified by the event index.\n\n"
            + "Parameters: "
            + PREFIX_EVENT + "EVENT_INDEX "
            + PREFIX_CONTACT + "CONTACT_INDEX or \"all\"\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " "
            + PREFIX_EVENT + "1 "
            + PREFIX_CONTACT + "2\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " "
            + PREFIX_EVENT + "1 "
            + PREFIX_CONTACT + "all\n\n"
            + "Note: EVENT_INDEX and CONTACT_INDEX must be a positive integers within the size of the displayed "
            + "event list and contact list respectively.";;

    public static final String MESSAGE_SUCCESS = "Successfully unlinked contact \"%1$s\" from event \"%2$s\"";
    public static final String MESSAGE_SUCCESS_ALL = "Successfully unlinked %1$d contact(s) from event \"%2$s\". "
            + "\nContacts unlinked: ";
    public static final String MESSAGE_CONTACT_NOT_FOUND = "Contact \"%1$s\" is not a participant of event \"%2$s\"";
    public static final String MESSAGE_NO_CONTACTS_TO_UNLINK = "No contacts available to unlink from event \"%1$s\"";

    private final Index eventIndex;
    private final Index contactIndex;
    private final boolean isUnlinkAll;

    /**
     * Creates an EventUnlinkCommand to unlink the specified contacts from an event
     */
    public EventUnlinkCommand(Index eventIndex, Index contactIndexes) {
        requireNonNull(eventIndex);
        requireNonNull(contactIndexes);
        this.eventIndex = eventIndex;
        this.contactIndex = contactIndexes;
        this.isUnlinkAll = false;
    }

    /**
     * Creates an EventUnlinkCommand to unlink all contacts from an event
     */
    public EventUnlinkCommand(Index eventIndex) {
        requireNonNull(eventIndex);
        this.eventIndex = eventIndex;
        this.contactIndex = null;
        this.isUnlinkAll = true;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Event> lastShownEventList = List.copyOf(model.getFilteredEventList());
        List<Contact> lastShownContactList = List.copyOf(model.getFilteredContactList());

        if (eventIndex.getZeroBased() >= lastShownEventList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        Event eventToEdit = lastShownEventList.get(eventIndex.getZeroBased());

        if (isUnlinkAll) {
            return executeUnlinkAll(model, eventToEdit, lastShownContactList);
        } else {
            return executeUnlinkSingle(model, eventToEdit, lastShownContactList);
        }


    }

    private CommandResult executeUnlinkSingle(Model model, Event eventToUnlink,
                                              List<Contact> lastShownContactList) throws CommandException {

        if (contactIndex.getZeroBased() >= lastShownContactList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
        }

        Contact contactToUnlink = lastShownContactList.get(contactIndex.getZeroBased());

        if (!model.hasParticipant(contactToUnlink, eventToUnlink)) {
            throw new CommandException(String.format(MESSAGE_CONTACT_NOT_FOUND,
                    contactToUnlink.getName(), eventToUnlink.getName()));
        }

        model.removeParticipant(contactToUnlink, eventToUnlink);

        return new CommandResult(String.format(MESSAGE_SUCCESS,
                contactToUnlink.getName().toString(), eventToUnlink.getName().toString()));
    }

    private CommandResult executeUnlinkAll(Model model, Event eventToUnlink,
                                           List<Contact> lastShownContactList) throws CommandException {

        if (lastShownContactList.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NO_CONTACTS_TO_UNLINK, eventToUnlink.getName()));
        }

        List <String> unlinkedContacts = new ArrayList<>();
        List<String> notLinkedContacts = new ArrayList<>();

        for (Contact contact : lastShownContactList) {
            if (model.hasParticipant(contact, eventToUnlink)) {
                model.removeParticipant(contact, eventToUnlink);
                unlinkedContacts.add(contact.getName().toString());
            } else {
                notLinkedContacts.add(contact.getName().toString());
            }
        }

        if (unlinkedContacts.isEmpty()) {
            throw new CommandException("No contacts were unlinked from the event");
        }

        String resultMessage = String.format(MESSAGE_SUCCESS_ALL, unlinkedContacts.size(), eventToUnlink.getName());
        resultMessage += String.join(", ", unlinkedContacts);
        if (!notLinkedContacts.isEmpty()) {
            resultMessage += "\nSkipped contacts not linked: " + String.join(", ", notLinkedContacts);
        }

        return new CommandResult(resultMessage);
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

        if (isUnlinkAll != otherCommand.isUnlinkAll) {
            return false;
        }

        if (isUnlinkAll) {
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
                .add("isUnlinkAll", isUnlinkAll)
                .toString();
    }
}
