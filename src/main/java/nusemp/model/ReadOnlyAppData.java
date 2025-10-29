package nusemp.model;

import java.util.List;

import javafx.collections.ObservableList;

import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.event.ParticipantStatus;

/**
 * Unmodifiable view of the app data.
 */
public interface ReadOnlyAppData {

    /**
     * Returns an unmodifiable view of the contact list.
     * This list will not contain any duplicate contacts.
     */
    ObservableList<Contact> getContactList();

    /**
     * Returns an unmodifiable view of the events list.
     * This list will not contain any duplicate events.
     */
    ObservableList<Event> getEventList();

    /**
     * Retrieves the participation status of a contact in a specific event.
     *
     * @param contact the contact whose status is to be retrieved
     * @param event the event in which the contact's status is to be checked
     */
    ParticipantStatus getParticipantStatus(Contact contact, Event event);

    /**
     * Returns a list of events that the specified contact is participating in.
     *
     * @param contact the contact whose associated events are to be retrieved
     */
    List<Event> getEventsForContact(Contact contact);

    /**
     * Returns a list of contacts participating in a given event.
     *
     * @param event the event whose participants are to be retrieved
     */
    List<Contact> getContactsForEvent(Event event);

}
