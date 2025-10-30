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

/**
 * Unlinks a contact from an event.
 */
public class EventUnlinkCommand extends Command {
    public static final String COMMAND_WORD = "unlink";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Unlinks contacts from an event identified by the event index.\n"
            + "Parameters: "
            + PREFIX_EVENT + " EVENT_INDEX (must be a positive integer) "
            + PREFIX_CONTACT + " CONTACT_INDEX (must be a positive integer) or 'all'\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " "
            + PREFIX_EVENT + " 1 "
            + PREFIX_CONTACT + " 2";

    public static final String MESSAGE_SUCCESS = "Successfully unlinked contact: %1$s from event %2$s";
    public static final String MESSAGE_SUCCESS_ALL = "Successfully unlinked %1$d contact(s) from event %2$s. "
            + "\nContacts unlinked: ";
    public static final String MESSAGE_CONTACT_NOT_FOUND = "Error unlinking contact: contact %1$s not found "
            + "in event %2$s";
    public static final String MESSAGE_NO_CONTACTS_TO_UNLINK = "No contacts available to unlink";

    private final Index eventIndex;
    private final Index contactIndex;
    private final boolean unlinkAll;

    /**
     * Creates an EventUnlinkCommand to unlink the specified contacts from an event
     */
    public EventUnlinkCommand(Index eventIndex, Index contactIndexes) {
        requireNonNull(eventIndex);
        requireNonNull(contactIndexes);
        this.eventIndex = eventIndex;
        this.contactIndex = contactIndexes;
        this.unlinkAll = false;
    }

    /**
     * Creates an EventUnlinkCommand to unlink all contacts from an event
     */
    public EventUnlinkCommand(Index eventIndex) {
        requireNonNull(eventIndex);
        this.eventIndex = eventIndex;
        this.contactIndex = null;
        this.unlinkAll = true;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Event> lastShownEventList = model.getFilteredEventList();
        List<Contact> lastShownContactList = model.getFilteredContactList();

        if (eventIndex.getZeroBased() >= lastShownEventList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        Event eventToEdit = lastShownEventList.get(eventIndex.getZeroBased());

        if (unlinkAll) {
            return executeUnlinkAll(model, eventToEdit);
        } else {
            return executeUnlinkSingle(model, eventToEdit);
        }


    }

    private CommandResult executeUnlinkSingle(Model model, Event eventToUnlink) throws CommandException {
        List<Contact> lastShownContactList = model.getFilteredContactList();

        if (contactIndex.getZeroBased() >= lastShownContactList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
        }

        Contact contactToUnlink = lastShownContactList.get(contactIndex.getZeroBased());

        if (!model.hasParticipant(contactToUnlink, eventToUnlink)) {
            throw new CommandException(String.format(MESSAGE_CONTACT_NOT_FOUND,
                    contactToUnlink.getName(), eventToUnlink.getName()));
        }

        try {
            model.removeParticipant(contactToUnlink, eventToUnlink);
            model.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
            return new CommandResult(String.format(MESSAGE_SUCCESS,
                    contactToUnlink.getName().toString(), eventToUnlink.getName().toString()));
        } catch (Exception e) {
            throw new CommandException("Error unlinking participant from event.");
        }
    }

    private CommandResult executeUnlinkAll(Model model, Event eventToUnlink) throws CommandException {
        List<Contact> filteredContactList = model.getFilteredContactList();

        if (filteredContactList.isEmpty()) {
            throw new CommandException(MESSAGE_NO_CONTACTS_TO_UNLINK);
        }

        List <String> unlinkedContacts = new ArrayList<>();
        List<String> notLinkedContacts = new ArrayList<>();

        for (Contact contact : filteredContactList) {
            if (model.hasParticipant(contact, eventToUnlink)) {
                try {
                    model.removeParticipant(contact, eventToUnlink);
                    unlinkedContacts.add(contact.getName().toString());
                } catch (Exception e) {
                    // Continue with next contact if one fails
                }
            } else {
                notLinkedContacts.add(contact.getName().toString());
            }
        }

        model.updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);

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

        if (unlinkAll != otherCommand.unlinkAll) {
            return false;
        }

        if (unlinkAll) {
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
                .add("unlinkAll", unlinkAll)
                .toString();
    }
}
