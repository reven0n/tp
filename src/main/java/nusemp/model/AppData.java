package nusemp.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;

import nusemp.commons.util.ToStringBuilder;
import nusemp.model.fields.Contact;
import nusemp.model.contact.UniqueContactList;
import nusemp.model.event.Event;
import nusemp.model.event.UniqueEventList;

/**
 * Wraps all data at the app level.
 * Duplicates are not allowed (by .isSameContact and .isSameEvent comparisons).
 */
public class AppData implements ReadOnlyAppData {

    private final UniqueContactList contacts;
    private final UniqueEventList events;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        contacts = new UniqueContactList();
        events = new UniqueEventList();
    }

    public AppData() {}

    /**
     * Creates an AppData using the data in {@code toBeCopied}
     */
    public AppData(ReadOnlyAppData toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the contact list with {@code contacts}.
     * {@code contacts} must not contain duplicate contacts.
     */
    public void setContacts(List<Contact> contacts) {
        this.contacts.setContacts(contacts);
    }

    /**
     * Replaces the contents of the event list with {@code events}.
     * {@code events} must not contain duplicate events.
     */
    public void setEvents(List<Event> events) {
        this.events.setEvents(events);
    }

    /**
     * Resets the existing data of this {@code AppData} with {@code newData}.
     */
    public void resetData(ReadOnlyAppData newData) {
        requireNonNull(newData);

        setContacts(newData.getContactList());
        setEvents(newData.getEventList());
    }

    //// contact-level operations

    /**
     * Returns true if a contact with the same identity as {@code contact} exists in the contact list.
     */
    public boolean hasContact(Contact contact) {
        requireNonNull(contact);
        return contacts.contains(contact);
    }

    /**
     * Adds a contact.
     * The contact must not already exist in the contact list.
     */
    public void addContact(Contact c) {
        contacts.add(c);
    }

    /**
     * Replaces the given contact {@code target} in the list with {@code editedContact}.
     * {@code target} must exist in the contact list.
     * The contact identity of {@code editedContact} must not be the same as another existing contact.
     */
    public void setContact(Contact target, Contact editedContact) {
        requireNonNull(editedContact);

        contacts.setContact(target, editedContact);
    }

    /**
     * Removes {@code contact} from this {@code AppData}.
     * Also removes {@code contact} from all events in this {@code AppData}.
     * {@code contact} must exist in the contact list.
     */
    public void removeContact(Contact contact) {
        contacts.remove(contact);
        removeContactFromEvents(contact);
    }

    /**
     * Removes {@code contact} from all events in this {@code AppData}.
     * {@code contact} must exist in the contact list.
     */
    private void removeContactFromEvents(Contact contact) {
        for (Event event : events) {
            if (event.hasParticipant(contact)) {
                Event updatedEvent = event.withoutParticipant(contact);
                events.setEvent(event, updatedEvent);
            }
        }
    }

    //// event-level operations

    /**
     * Returns true if an event with the same identity as {@code event} exists in the event list.
     */
    public boolean hasEvent(Event event) {
        requireNonNull(event);
        return events.contains(event);
    }

    /**
     * Adds an event to the event list.
     * The event must not already exist in the event list.
     */
    public void addEvent(Event e) {
        events.add(e);
    }

    /**
     * Replaces the given event {@code target} in the list with {@code editedEvent}.
     * {@code target} must exist in the event list.
     * The event identity of {@code editedEvent} must not be the same as another existing event in the event list.
     */
    public void setEvent(Event target, Event editedEvent) {
        requireNonNull(editedEvent);

        events.setEvent(target, editedEvent);
    }

    /**
     * Removes {@code event} from this {@code AppData}.
     * {@code event} must exist in the event list.
     */
    public void removeEvent(Event event) {
        events.remove(event);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("contacts", contacts)
                .add("events", events)
                .toString();
    }

    @Override
    public ObservableList<Contact> getContactList() {
        return contacts.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Event> getEventList() {
        return events.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AppData)) {
            return false;
        }

        AppData otherAppData = (AppData) other;
        return contacts.equals(otherAppData.contacts)
                && events.equals(otherAppData.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contacts, events);
    }
}
