package nusemp.model;

import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.event.ParticipantStatus;
import nusemp.model.event.exceptions.ParticipantNotFoundException;

/**
 * Maps Participants to Events and vise versa.
 */
public class ParticipantMap {

    private final Map<Contact, Map<Event, ParticipantEvent>> byContact = new HashMap<>();
    private final Map<Event, Map<Contact, ParticipantEvent>> byEvent = new HashMap<>();

    /**
     * Adds a participant event link between the given contact and event with the specified status.
     */
    public void addParticipantEvent(Contact contact, Event event, ParticipantStatus status) {
        requireAllNonNull(contact, event, status);

        ParticipantEvent participantEvent = new ParticipantEvent(contact, event, status);
        if (!byContact.containsKey(contact)) {
            byContact.put(contact, new HashMap<>());
        }
        byContact.get(contact).put(event, participantEvent);
        if (!byEvent.containsKey(event)) {
            byEvent.put(event, new HashMap<>());
        }
        byEvent.get(event).put(contact, participantEvent);
    }

    /**
     * Removes all participant event links associated with the given contact.
     */
    public void removeContact(Contact contact) {
        requireAllNonNull(contact);

        Map<Event, ParticipantEvent> events = byContact.remove(contact);
        if (events == null) {
            return;
        }
        for (Event event : events.keySet()) {
            removeContactFromEvent(contact, event);
        }
    }

    private void removeContactFromEvent(Contact contact, Event event) {
        Map<Contact, ParticipantEvent> contacts = byEvent.get(event);
        if (contacts == null) {
            return;
        }
        contacts.remove(contact);
        if (contacts.isEmpty()) {
            byEvent.remove(event);
        }
    }

    /**
     * Removes all participant event links associated with the given event.
     */
    public void removeEvent(Event event) {
        requireAllNonNull(event);

        Map<Contact, ParticipantEvent> contacts = byEvent.remove(event);
        if (contacts == null) {
            return;
        }
        for (Contact contact : contacts.keySet()) {
            removeEventFromContact(contact, event);
        }
    }

    private void removeEventFromContact(Contact contact, Event event) {
        Map<Event, ParticipantEvent> events = byContact.get(contact);
        if (events == null) {
            return;
        }
        events.remove(event);
        if (events.isEmpty()) {
            byContact.remove(contact);
        }
    }

    /**
     * Removes the participant event link between the given contact and event.
     */
    public void removeParticipantEvent(Contact contact, Event event) {
        requireAllNonNull(contact, event);

        Map<Event, ParticipantEvent> events = byContact.get(contact);
        if (events == null) {
            return;
        }

        events.remove(event);
        if (events.isEmpty()) {
            byContact.remove(contact);
        }

        Map<Contact, ParticipantEvent> contacts = byEvent.get(event);
        if (contacts == null) {
            return;
        }

        contacts.remove(contact);
        if (contacts.isEmpty()) {
            byEvent.remove(event);
        }
    }

    /**
     * Edits the participant status for the given contact and event.
     */
    public void editParticipantEventStatus(Contact contact, Event event, ParticipantStatus newStatus) {
        requireAllNonNull(contact, event, newStatus);

        Map<Event, ParticipantEvent> events = byContact.get(contact);
        if (events == null || !events.containsKey(event)) {
            return;
        }

        ParticipantEvent updatedParticipantEvent = new ParticipantEvent(contact, event, newStatus);
        events.put(event, updatedParticipantEvent);

        Map<Contact, ParticipantEvent> contacts = byEvent.get(event);
        if (contacts != null) {
            contacts.put(contact, updatedParticipantEvent);
        }
    }

    /**
     * Updates all participant event links when a contact is edited.
     */
    public void updateContactInParticipantMap(Contact oldContact, Contact newContact) {
        requireAllNonNull(oldContact, newContact);

        Map<Event, ParticipantEvent> events = byContact.remove(oldContact);
        if (events == null) {
            return;
        }

        for (Map.Entry<Event, ParticipantEvent> entry : events.entrySet()) {
            updateContactInEvents(oldContact, newContact, entry, events);
        }

        byContact.put(newContact, events);
    }

    private void updateContactInEvents(Contact oldContact, Contact newContact,
                                       Map.Entry<Event, ParticipantEvent> entry,
                                       Map<Event, ParticipantEvent> events) {
        Event event = entry.getKey();
        ParticipantEvent participantEvent = entry.getValue();

        ParticipantEvent updatedParticipantEvent = new ParticipantEvent(
                newContact, event, participantEvent.getStatus());
        events.put(event, updatedParticipantEvent);

        Map<Contact, ParticipantEvent> contacts = byEvent.get(event);
        if (contacts != null) {
            contacts.remove(oldContact);
            contacts.put(newContact, updatedParticipantEvent);
        }
    }

    /**
     * Updates all participant event links when an event is edited.
     */
    public void updateEventInParticipantMap(Event oldEvent, Event newEvent) {
        requireAllNonNull(oldEvent, newEvent);

        Map<Contact, ParticipantEvent> contacts = byEvent.remove(oldEvent);
        if (contacts == null) {
            return;
        }

        for (Map.Entry<Contact, ParticipantEvent> entry : contacts.entrySet()) {
            updateEventInContacts(oldEvent, newEvent, entry, contacts);
        }

        byEvent.put(newEvent, contacts);
    }

    private void updateEventInContacts(Event oldEvent, Event newEvent,
                                       Map.Entry<Contact, ParticipantEvent> entry,
                                       Map<Contact, ParticipantEvent> contacts) {
        Contact contact = entry.getKey();
        ParticipantEvent participantEvent = entry.getValue();

        ParticipantEvent updatedParticipantEvent = new ParticipantEvent(
                contact, newEvent, participantEvent.getStatus());
        contacts.put(contact, updatedParticipantEvent);

        Map<Event, ParticipantEvent> events = byContact.get(contact);
        if (events != null) {
            events.remove(oldEvent);
            events.put(newEvent, updatedParticipantEvent);
        }
    }

    public ParticipantStatus getParticipantStatus(Contact contact, Event event) {
        requireAllNonNull(contact, event);
        Map<Event, ParticipantEvent> events = byContact.get(contact);
        if (events == null) {
            throw new ParticipantNotFoundException();
        }

        ParticipantEvent participantEvent = events.get(event);
        if (participantEvent == null) {
            throw new ParticipantNotFoundException();
        }

        return participantEvent.getStatus();
    }

    /**
     * Checks if the given contact is a participant in the given event.
     */
    public boolean hasParticipantEvent(Contact contact, Event event) {
        requireAllNonNull(contact, event);
        Map<Event, ParticipantEvent> events = byContact.get(contact);
        return events != null && events.containsKey(event);
    }

    /**
     * Gets all events for the given contact as a List.
     */
    public List<Event> getEventsForContact(Contact contact) {
        requireAllNonNull(contact);
        Map<Event, ParticipantEvent> events = byContact.get(contact);
        if (events == null) {
            return new ArrayList<>();
        }

        List<ParticipantEvent> participantEvents = new ArrayList<>();
        participantEvents.addAll(events.values());
        return participantEvents.stream()
                .map(ParticipantEvent::getEvent).toList();
    }

    /**
     * Gets all contacts for the given event as a List.
     */
    public List<Contact> getContactsForEvent(Event event) {
        requireAllNonNull(event);
        Map<Contact, ParticipantEvent> contacts = byEvent.get(event);
        if (contacts == null) {
            return new ArrayList<>();
        }

        List<ParticipantEvent> participantEvents = new ArrayList<>();
        participantEvents.addAll(contacts.values());
        return participantEvents.stream()
                .map(ParticipantEvent::getContact).toList();
    }
}
