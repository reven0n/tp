package nusemp.model.event;

import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import nusemp.commons.util.ToStringBuilder;
import nusemp.model.person.Person;

/**
 * Represents an Event in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Event {

    // Identity fields
    private final EventName name;
    private final EventDate date;

    // Data fields
    private final List<Person> participants = new ArrayList<>();

    /**
     * Every field must be present and not null.
     */
    public Event(EventName name, EventDate date, List<Person> participants) {
        requireAllNonNull(name, date, participants);
        this.name = name;
        this.date = date;
        this.participants.addAll(participants);
    }

    /**
     * Convenience constructor without participants.
     */
    public Event(EventName name, EventDate date) {
        this(name, date, new ArrayList<>());
    }

    public EventName getName() {
        return name;
    }

    public EventDate getDate() {
        return date;
    }

    /**
     * Returns an immutable participant list, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public List<Person> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    /**
     * Returns true if the event has the given participant.
     */
    public boolean hasParticipant(Person person) {
        requireAllNonNull(person);
        return participants.contains(person);
    }

    /**
     * Returns a new Event with the given participant added.
     * This maintains immutability by returning a new Event instance.
     */
    public Event withParticipant(Person person) {
        requireAllNonNull(person);
        List<Person> updatedParticipants = new ArrayList<>(participants);
        updatedParticipants.add(person);
        return new Event(name, date, updatedParticipants);
    }

    /**
     * Returns a new Event with the given participant removed.
     * This maintains immutability by returning a new Event instance.
     */
    public Event withoutParticipant(Person person) {
        requireAllNonNull(person);
        List<Person> updatedParticipants = new ArrayList<>(participants);
        updatedParticipants.remove(person);
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
