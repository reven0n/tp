package nusemp.model;

import java.util.List;

import javafx.collections.ObservableList;

import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.participant.Participant;
import nusemp.model.participant.ReadOnlyParticipantMap;

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
     * Returns an unmodifiable view of the participant map.
     */
    ReadOnlyParticipantMap getParticipantMap();

    /** Returns the list of participants for the given event. */
    List<Participant> getParticipants(Event event);

    /** Returns the list of participants containing the given contact. */
    List<Participant> getParticipants(Contact contact);
}
