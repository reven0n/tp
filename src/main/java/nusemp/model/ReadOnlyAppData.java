package nusemp.model;

import javafx.collections.ObservableList;

import nusemp.model.contact.Contact;
import nusemp.model.event.Event;

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

}
