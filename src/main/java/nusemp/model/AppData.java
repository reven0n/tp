package nusemp.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import nusemp.commons.util.ToStringBuilder;
import nusemp.model.contact.Contact;
import nusemp.model.contact.UniqueContactList;
import nusemp.model.event.Event;
import nusemp.model.event.ParticipantStatus;
import nusemp.model.event.UniqueEventList;

/**
 * Wraps all data at the app level.
 * Duplicates are not allowed (by .isSameContact and .isSameEvent comparisons).
 */
public class AppData implements ReadOnlyAppData {

    private final UniqueContactList contacts;
    private final UniqueEventList events;
    private final ParticipantMap participantMap;

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
        participantMap = new ParticipantMap();
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
        participantMap.updateContactInParticipantMap(target, editedContact);
        System.out.println(participantMap.getEventsForContact(editedContact).toString());
    }

    /**
     * Removes {@code contact} from this {@code AppData}.
     * Also removes {@code contact} from all events in this {@code AppData}.
     * {@code contact} must exist in the contact list.
     */
    public void removeContact(Contact contact) {
        contacts.remove(contact);
        participantMap.removeContact(contact);
        //removeContactFromEvents(contact);
    }

    /**
     * Removes {@code contact} from all events in this {@code AppData}.
     * {@code contact} must exist in the contact list.
     */
    private void removeContactFromEvents(Contact contact) {
        for (Event event : events) {
            if (event.hasContact(contact)) {
                Event updatedEvent = event.withoutContact(contact);
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
        participantMap.updateEventInParticipantMap(target, editedEvent);
    }

    /**
     * Removes {@code event} from this {@code AppData}.
     * {@code event} must exist in the event list.
     */
    public void removeEvent(Event event) {
        participantMap.removeEvent(event);
        events.remove(event);
    }

    //// participant map operations

    public void addParticipantEvent(Contact contact, Event event, ParticipantStatus status) {
        requireNonNull(contact);
        requireNonNull(event);
        requireNonNull(status);
        participantMap.addParticipantEvent(contact, event, status);
        Logger logger = Logger.getLogger(AppData.class.getName());
        logger.log(java.util.logging.Level.INFO, participantMap.getEventsForContact(contact).toString());
    }

    public void removeParticipantEvent(Contact contact, Event event) {
        requireNonNull(contact);
        requireNonNull(event);
        participantMap.removeParticipantEvent(contact, event);
    }

    public boolean hasParticipantEvent(Contact contact, Event event) {
        requireNonNull(contact);
        requireNonNull(event);
        return participantMap.hasParticipantEvent(contact, event);
    }

    public ParticipantStatus getParticipantStatus(Contact contact, Event event) {
        requireNonNull(contact);
        requireNonNull(event);
        return participantMap.getParticipantStatus(contact, event);
    }

    public List<Event> getEventsForContact(Contact contact) {
        requireNonNull(contact);
        return participantMap.getEventsForContact(contact);
    }

    public List<Contact> getContactsForEvent(Event event) {
        requireNonNull(event);
        return participantMap.getContactsForEvent(event);
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

    public ObservableMap<Contact, List<ParticipantEvent>> getContactEventMap() {
        return participantMap.getEventsForAllContacts();
    }

    public ParticipantMap getParticipantMap() {
        return participantMap;
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
