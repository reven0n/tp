package nusemp.logic.commands;

import static java.util.Objects.requireNonNull;
import static nusemp.logic.parser.CliSyntax.PREFIX_CONTACT;
import static nusemp.logic.parser.CliSyntax.PREFIX_EVENT;

import java.util.List;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.model.Model;
import nusemp.model.event.Event;
import nusemp.model.event.exceptions.DuplicateParticipantException;
import nusemp.model.person.Person;

/**
 * Links a contact to an event in the event book.
 */
public class EventLinkCommand extends Command {
    public static final String COMMAND_WORD = "link";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Links a contact to an event in the event book. "
            + "Parameters: "
            + PREFIX_EVENT + " EVENT_INDEX "
            + PREFIX_CONTACT + " CONTACT_INDEX\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " "
            + PREFIX_EVENT + " 1 "
            + PREFIX_CONTACT + " 2";

    public static final String MESSAGE_SUCCESS = "New event link has been added.";
    public static final String MESSAGE_DUPLICATE_PARTICIPANT = "This contact is already linked to the event.";

    private final Index eventIndex;
    private final Index contactIndex;

    /**
     * Creates an EventLinkCommand to link the specified {@code Person}
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
        List<Person> lastShownPersonList = model.getFilteredPersonList();

        // check if the event index and contact index are within bounds
        if (eventIndex.getZeroBased() >= lastShownEventList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }
        if (contactIndex.getZeroBased() >= lastShownPersonList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Event eventToUpdate = lastShownEventList.get(eventIndex.getZeroBased());
        Person personToLink = lastShownPersonList.get(contactIndex.getZeroBased());

        // link the contact to the event
        try {
            Event updatedEvent = eventToUpdate.withParticipant(personToLink);
            model.setEvent(eventToUpdate, updatedEvent);
            return new CommandResult(String.format(MESSAGE_SUCCESS,
                    personToLink.getName(), updatedEvent.getName()));
        } catch (DuplicateParticipantException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PARTICIPANT);
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
