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
import nusemp.model.event.exceptions.ParticipantNotFoundException;
import nusemp.model.fields.Address;
import nusemp.model.fields.Date;
import nusemp.model.fields.Name;
import nusemp.model.fields.Tag;

/**
 * Represents an Event.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Event {

    // Identity fields
    private final Name name;
    private final Date date;

    // Data fields
    private final Address address;
    private final EventStatus status;
    private final List<Participant> participants = new ArrayList<>();
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Every field must be present and not null. {@code Address.empty()} can be used to represent absence of an address.
     */
    public Event(Name name, Date date, Address address, EventStatus status, Set<Tag> tags,
            List<Participant> participants) {
        requireAllNonNull(name, date, address, status, participants);
        checkForDuplicateParticipant(participants);
        this.name = name;
        this.date = date;
        this.address = address;
        this.status = status;
        this.tags.addAll(tags);
        this.participants.addAll(participants);
    }

    /**
     * Convenience constructor without participants or tags, with default status STARTING.
     */
    public Event(Name name, Date date, Address address) {
        this(name, date, address, EventStatus.STARTING, new HashSet<>(), new ArrayList<>());
    }

    public Name getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public Address getAddress() {
        return address;
    }

    public EventStatus getStatus() {
        return status;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public boolean hasTags() {
        return !tags.isEmpty();
    }

    public boolean hasAddress() {
        return !address.isEmpty();
    }

    /**
     * Returns an immutable participant list, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public List<Participant> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    /**
     * Returns true if the event has the given contact.
     */
    public boolean hasContact(Contact contact) {
        requireAllNonNull(contact);
        return participants.stream().anyMatch(p -> p.getContact().isSameContact(contact));
    }

    /**
     * Returns true if the event has a contact with the given email.
     */
    public boolean hasContactWithEmail(String email) {
        requireAllNonNull(email);
        return participants.stream()
                .anyMatch(p -> p.getContact().getEmail().value.equals(email));
    }

    /**
     * Returns a new Event with the given participant updated if it exists.
     * This maintains immutability by returning a new Event instance.
     */
    public Event withUpdatedParticipant(Participant updatedParticipant) throws ParticipantNotFoundException {
        requireAllNonNull(updatedParticipant);
        if (!hasContactWithEmail(updatedParticipant.getContact().getEmail().value)) {
            throw new ParticipantNotFoundException();
        }

        List<Participant> updatedParticipants = new ArrayList<>(participants);
        for (int i = 0; i < updatedParticipants.size(); i++) {
            Participant currentParticipant = updatedParticipants.get(i);
            if (currentParticipant.hasSameContact(updatedParticipant)) {
                updatedParticipants.set(i, updatedParticipant);
                break;
            }
        }
        return new Event(name, date, address, status, tags, updatedParticipants);
    }

    /**
     * Checks if the given list of participants contains duplicate emails.
     * @throws DuplicateParticipantException if duplicates are found.
     */
    private static void checkForDuplicateParticipant(List<Participant> participants) {
        Set<String> emails = new HashSet<>();
        for (Participant participant : participants) {
            Contact contact = participant.getContact();
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
    public Event withContact(Contact contact) {
        requireAllNonNull(contact);
        if (hasContactWithEmail(contact.getEmail().value)) {
            throw new DuplicateParticipantException();
        }
        List<Participant> updatedParticipants = new ArrayList<>(participants);
        updatedParticipants.add(new Participant(contact));
        return new Event(name, date, address, status, tags, updatedParticipants);
    }


    /**
     * Returns a new Event with the given contact removed by finding contact with the same email.
     * This maintains immutability by returning a new Event instance.
     */
    public Event withoutContact(Contact contact) {
        requireAllNonNull(contact);
        List<Participant> updatedParticipants = new ArrayList<>(participants);
        updatedParticipants.removeIf(p -> p.equalsContact(contact));
        return new Event(name, date, address, status, tags, updatedParticipants);
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
     * Returns true if both of the emails in the lists are the same.
     */
    private boolean isSameParticipantList(List<Participant> otherParticipants) {
        if (participants.size() != otherParticipants.size()) {
            return false;
        }

        for (int i = 0; i < participants.size(); i++) {
            String email1 = participants.get(i).getContact().getEmail().value;
            String email2 = otherParticipants.get(i).getContact().getEmail().value;
            if (!email1.equals(email2)) {
                return false;
            }
        }

        return true;
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
        if (!(other instanceof Event otherEvent)) {
            return false;
        }

        return name.equals(otherEvent.name)
                && date.equals(otherEvent.date)
                && address.equals(otherEvent.address)
                && status.equals(otherEvent.status)
                && tags.equals(otherEvent.tags)
                && isSameParticipantList(otherEvent.participants);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, date, address, status, tags, participants);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("date", date)
                .add("address", address)
                .add("status", status)
                .add("tags", tags)
                .add("participants", participants)
                .toString();
    }
}
