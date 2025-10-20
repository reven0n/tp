package nusemp.model.event;

import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import nusemp.commons.util.ToStringBuilder;
import nusemp.model.contact.Contact;
import nusemp.model.event.exceptions.DuplicateParticipantException;
import nusemp.model.fields.Date;
import nusemp.model.fields.Name;

/**
 * Represents an Event.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Event {

    // Identity fields
    private final Name name;
    private final Date date;

    // Data fields
    private final List<Contact> participants = new ArrayList<>();

    /**
     * Every field must be present and not null.
     */
    public Event(Name name, Date date, List<Contact> participants) {
        requireAllNonNull(name, date, participants);
        checkForDuplicateParticipants(participants);
        this.name = name;
        this.date = date;
        this.participants.addAll(participants);
    }

    /**
     * Convenience constructor without participants.
     */
    public Event(Name name, Date date) {
        this(name, date, new ArrayList<>());
    }

    public Name getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Returns an immutable participant list, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public List<Contact> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    /**
     * Returns true if the event has the given participant.
     */
    public boolean hasParticipant(Contact contact) {
        requireAllNonNull(contact);
        return participants.contains(contact);
    }

    /**
     * Returns true if the event has a participant with the given email.
     */
    public boolean hasParticipantWithEmail(String email) {
        requireAllNonNull(email);
        return participants.stream()
                .anyMatch(contact -> contact.getEmail().value.equals(email));
    }

    /**
     * Checks if the given list of participants contains duplicate emails.
     * @throws DuplicateParticipantException if duplicates are found.
     */
    private static void checkForDuplicateParticipants(List<Contact> participants) {
        Set<String> emails = new HashSet<>();
        for (Contact contact : participants) {
            String email = contact.getEmail().value;
            if (!emails.add(email)) {
                throw new DuplicateParticipantException();
            }
        }
    }

    /**
     * Returns a new Event with the given participant added.
     * This maintains immutability by returning a new Event instance.
     * @throws DuplicateParticipantException if the participant already exists in the event.
     */
    public Event withParticipant(Contact contact) {
        requireAllNonNull(contact);
        if (hasParticipantWithEmail(contact.getEmail().value)) {
            throw new DuplicateParticipantException();
        }
        List<Contact> updatedParticipants = new ArrayList<>(participants);
        updatedParticipants.add(contact);
        return new Event(name, date, updatedParticipants);
    }

    /**
     * Returns a new Event with the given participant removed.
     * This maintains immutability by returning a new Event instance.
     */
    public Event withoutParticipant(Contact contact) {
        requireAllNonNull(contact);
        List<Contact> updatedParticipants = new ArrayList<>(participants);
        updatedParticipants.remove(contact);
        return new Event(name, date, updatedParticipants);
    }

    /**
     * Returns true if both events have the same name.
     * This defines a weaker notion of equality for duplicate detection.
     */
    public boolean isSameEvent(Event otherEvent) {
        if (otherEvent == this) {
            return true;
        }

        return otherEvent != null
                && otherEvent.getName().equals(getName());
    }

    /**
     * Returns true if both events have the same identity and data fields.
     * This defines a stronger notion of equality between two events.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Event)) {
            return false;
        }

        Event otherEvent = (Event) other;
        return name.equals(otherEvent.name)
                && date.equals(otherEvent.date)
                && participants.equals(otherEvent.participants);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, date, participants);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("date", date)
                .add("participants", participants)
                .toString();
    }
}
