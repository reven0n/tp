package nusemp.model;

import static java.util.Objects.requireNonNull;
import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;

import nusemp.commons.util.ToStringBuilder;
import nusemp.model.contact.Contact;
import nusemp.model.contact.UniqueContactList;
import nusemp.model.event.Event;
import nusemp.model.event.UniqueEventList;
import nusemp.model.participant.Participant;
import nusemp.model.participant.ParticipantMap;
import nusemp.model.participant.ParticipantStatus;
import nusemp.model.participant.ReadOnlyParticipantMap;

// @@author
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
     * Replaces the contents of the participant map with {@code participantMap}.
     */
    public void setParticipantMap(ReadOnlyParticipantMap participantMap) {
        this.participantMap.setFrom(participantMap);
    }

    /**
     * Resets the existing data of this {@code AppData} with {@code newData}.
     */
    public void resetData(ReadOnlyAppData newData) {
        requireNonNull(newData);

        setContacts(newData.getContactList());
        setEvents(newData.getEventList());
        // @@author CZX123
        setParticipantMap(newData.getParticipantMap());
        // @@author
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
        // @@author CZX123
        participantMap.setContact(target, editedContact);
        for (Participant p : getParticipants(editedContact)) {
            refreshEvent(p.getEvent());
        }
        // @@author
    }

    /**
     * Removes {@code contact} from this {@code AppData}.
     * Also removes {@code contact} from all events in this {@code AppData}.
     * {@code contact} must exist in the contact list.
     */
    public void removeContact(Contact contact) {
        List<Participant> participants = getParticipants(contact);
        contacts.remove(contact);
        // @@author reven0n
        participantMap.removeContact(contact);
        for (Participant p : participants) {
            refreshEvent(p.getEvent());
        }
    }

    // @@author CZX123
    /**
     * Refreshes the given contact in the contact list.
     * <p>
     * Note: This is a workaround for event changes not being reflected in the contact list.
     */
    private void refreshContact(Contact contact) {
        contacts.setContact(contact, contact.getInvalidatedContact());
    }

    // @@author asaiyume
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
        // @@author CZX123
        participantMap.setEvent(target, editedEvent);
        for (Participant p : getParticipants(editedEvent)) {
            refreshContact(p.getContact());
        }
        // @@author asaiyume
    }

    /**
     * Removes {@code event} from this {@code AppData}.
     * {@code event} must exist in the event list.
     */
    public void removeEvent(Event event) {
        List<Participant> participants = getParticipants(event);
        // @@author reven0n
        participantMap.removeEvent(event);
        // @@author asaiyume
        events.remove(event);
        for (Participant p : participants) {
            refreshContact(p.getContact());
        }
    }

    // @@author CZX123
    /**
     * Refreshes the given event in the event list.
     * <p>
     * Note: This is a workaround for contact changes not being reflected in the event list.
     */
    private void refreshEvent(Event event) {
        events.setEvent(event, event.getInvalidatedEvent());
    }

    // @@author reven0n
    //// participant map operations

    /**
     * Adds a participant with a specified participation status.
     *
     * @param contact the contact to associate with the event
     * @param event the event to which the contact is being added
     * @param status the participation status of the contact in the event
     * @throws NullPointerException if {@code contact}, {@code event}, or {@code status} is {@code null}
     */
    public void addParticipant(Contact contact, Event event, ParticipantStatus status) {
        requireAllNonNull(contact, event, status);
        participantMap.addParticipant(contact, event, status);
        refreshContact(contact);
        refreshEvent(event);
    }

    /**
     * Removes the association between a contact and an event.
     *
     * @param contact the contact to remove from the event
     * @param event the event from which the contact is being removed
     * @throws NullPointerException if {@code contact} or {@code event} is {@code null}
     */
    public void removeParticipant(Contact contact, Event event) {
        requireAllNonNull(contact, event);
        participantMap.removeParticipant(contact, event);
        refreshContact(contact);
        refreshEvent(event);
    }

    /**
     * Checks whether a contact is linked as a participant in a given event.
     *
     * @param contact the contact to check for
     * @param event the event to check within
     * @return {@code true} if the contact is a participant in the event; {@code false} otherwise
     * @throws NullPointerException if {@code contact} or {@code event} is {@code null}
     */
    public boolean hasParticipant(Contact contact, Event event) {
        requireAllNonNull(contact, event);
        return participantMap.hasParticipant(contact, event);
    }

    /**
     * Sets the participant with a new participation status.
     *
     * @param contact the contact that is associated with the event
     * @param event the event that is associated with the contact
     * @param status the new participation status of the contact in the event
     * @throws NullPointerException if {@code contact}, {@code event}, or {@code status} is {@code null}
     */
    public void setParticipant(Contact contact, Event event, ParticipantStatus status) {
        requireAllNonNull(contact, event, status);
        participantMap.setParticipant(contact, event, status);
        refreshContact(contact);
        refreshEvent(event);
    }

    /**
     * Returns a list of participants that the specified contact is in.
     *
     * @param contact the contact whose associated events are to be retrieved
     * @return a list of {@link Participant} objects that contain the contact;
     *         an empty list if the contact is not associated with any events
     * @throws NullPointerException if {@code contact} is {@code null}
     */
    @Override
    public List<Participant> getParticipants(Contact contact) {
        requireNonNull(contact);
        return participantMap.getParticipants(contact);
    }

    /**
     * Returns a list of participants that the specified event has.
     *
     * @param event the event whose participants are to be retrieved
     * @return a list of {@link Participant} objects part of the event;
     *         an empty list if no participants are associated with the event
     * @throws NullPointerException if {@code event} is {@code null}
     */
    @Override
    public List<Participant> getParticipants(Event event) {
        requireNonNull(event);
        return participantMap.getParticipants(event);
    }
    // @@author


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
    public ReadOnlyParticipantMap getParticipantMap() {
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
                && events.equals(otherAppData.events)
                && participantMap.equals(otherAppData.participantMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contacts, events);
    }
}
