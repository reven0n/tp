package nusemp.model.contact;

import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import nusemp.commons.util.ToStringBuilder;
import nusemp.model.event.Event;
import nusemp.model.event.exceptions.DuplicateEventException;
import nusemp.model.fields.Address;
import nusemp.model.fields.Email;
import nusemp.model.fields.Name;
import nusemp.model.fields.Phone;
import nusemp.model.fields.Tag;

/**
 * Represents a Contact.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Contact {

    // Identity fields
    private final Name name;
    private final Email email;

    // Data fields
    private final Phone phone;
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final List<Event> events = new ArrayList<>();

    /**
     * Every field must be present and not null.
     * {@code Phone.empty()} or {@code Address.empty()} can be used to represent absence of a phone number or address
     * respectively.
     */
    public Contact(Name name, Email email, Phone phone, Address address, Set<Tag> tags, List<Event> events) {
        requireAllNonNull(name, email, phone, address, tags, events);
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.tags.addAll(tags);
        this.events.addAll(events);
    }

    /**
     * Convenience constructor without linked events.
     */
    public Contact(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(name, email, phone, address, tags, events);
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public Phone getPhone() {
        return phone;
    }

    public boolean hasPhone() {
        return !phone.isEmpty();
    }

    public Address getAddress() {
        return address;
    }

    public boolean hasAddress() {
        return !address.isEmpty();
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

    /**
     * Returns an immutable event list, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    /**
     * Returns true if the contact has the given event.
     */
    public boolean hasEvent(Event event) {
        requireAllNonNull(event);
        return events.contains(event);
    }

    /**
     * Returns true if the contact has an event with the given name.
     */
    public boolean hasEventWithName(String eventName) {
        requireAllNonNull(eventName);
        return events.stream()
                .anyMatch(event -> event.getName().value.equals(eventName));
    }

    private static void checkForDuplicateEvents(List<Event> events)
            throws DuplicateEventException {
        Set<String> eventNames = new HashSet<>();
        for (Event event : events) {
            String eventName = event.getName().value;
            if (!eventNames.add(eventName)) {
                throw new DuplicateEventException();
            }
        }
    }

    /**
     * Returns a new Contact with the given event added.
     * This maintains immutability by returning a new Contact instance.
     */
    public Contact addEvent(Event event) throws DuplicateEventException {
        requireAllNonNull(event);
        if (hasEventWithName(event.getName().value)) {
            throw new DuplicateEventException();
        }
        List<Event> updatedEvents = new ArrayList<>(events);
        updatedEvents.add(event);
        return new Contact(name, email, phone, address, tags, updatedEvents);
    }

    /**
     * Returns a new Contact with the given event removed.
     * This maintains immutability by returning a new Contact instance.
     */
    public Contact removeEvent(Event event) {
        requireAllNonNull(event);
        List<Event> updatedEvents = new ArrayList<>(events);
        updatedEvents.remove(event);
        return new Contact(name, email, phone, address, tags, updatedEvents);
    }

    /**
     * Returns true if both contacts have the same email.
     * This defines a weaker notion of equality between two contacts.
     */
    public boolean isSameContact(Contact otherContact) {
        if (otherContact == this) {
            return true;
        }

        return otherContact != null
                && otherContact.getEmail().isSameEmail(getEmail());
    }

    private boolean hasSameEvents(Contact otherContact) {
        if (otherContact.events.size() != this.events.size()) {
            return false;
        }

        for (Event event : otherContact.events) {
            if (!this.hasEventWithName(event.getName().value)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if both contacts have the same identity and data fields.
     * This defines a stronger notion of equality between two contacts.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Contact)) {
            return false;
        }

        Contact otherContact = (Contact) other;
        return name.equals(otherContact.name)
                && email.equals(otherContact.email)
                && phone.equals(otherContact.phone)
                && address.equals(otherContact.address)
                && tags.equals(otherContact.tags)
                && hasSameEvents(otherContact);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, email, phone, address, tags, events);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("email", email)
                .add("phone", phone)
                .add("address", address)
                .add("tags", tags)
                .add("events", events)
                .toString();
    }

}
