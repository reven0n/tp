package nusemp.model.participant;

import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import nusemp.model.contact.Contact;
import nusemp.model.contact.ContactKey;
import nusemp.model.event.Event;
import nusemp.model.event.EventKey;

/**
 * Maps contacts and event to their respective Participant links.
 */
public class ParticipantMap implements ReadOnlyParticipantMap {

    private static final String ASSERTION_MISSING_CONTACT = "Contact does not exist in ParticipantMap!";
    private static final String ASSERTION_MISSING_EVENT = "Event does not exist in ParticipantMap!";
    private static final String ASSERTION_MISMATCH_MAPS = "Mismatch between byContact and byEvent maps!";

    private Map<ContactKey, Map<EventKey, Participant>> byContact = new HashMap<>();
    private Map<EventKey, Map<ContactKey, Participant>> byEvent = new HashMap<>();

    private void removeContactFromEvent(ContactKey c, EventKey e) {
        Map<ContactKey, Participant> contactMap = byEvent.get(e);
        assert contactMap != null && contactMap.containsKey(c) : ASSERTION_MISMATCH_MAPS;

        contactMap.remove(c);
        if (contactMap.isEmpty()) {
            byEvent.remove(e);
        }
    }

    private void removeEventFromContact(EventKey e, ContactKey c) {
        Map<EventKey, Participant> eventMap = byContact.get(c);
        assert eventMap != null && eventMap.containsKey(e) : ASSERTION_MISMATCH_MAPS;

        eventMap.remove(e);
        if (eventMap.isEmpty()) {
            byContact.remove(c);
        }
    }

    /**
     * Adds a participant link between the given contact and event with the specified status.
     */
    public void addParticipant(Contact contact, Event event, ParticipantStatus status) {
        requireAllNonNull(contact, event, status);
        ContactKey c = contact.getPrimaryKey();
        EventKey e = event.getPrimaryKey();
        Participant participant = new Participant(contact, event, status);

        if (!byContact.containsKey(c)) {
            byContact.put(c, new HashMap<>());
        }
        byContact.get(c).put(e, participant);

        if (!byEvent.containsKey(e)) {
            byEvent.put(e, new HashMap<>());
        }
        byEvent.get(e).put(c, participant);
    }

    /**
     * Removes the participant link between the given contact and event with the specified status.
     */
    public void removeParticipant(Contact contact, Event event) {
        requireAllNonNull(contact, event);
        if (!hasParticipant(contact, event)) {
            throw new ParticipantNotFoundException();
        }
        ContactKey c = contact.getPrimaryKey();
        EventKey e = event.getPrimaryKey();
        Map<ContactKey, Participant> contactMap = byEvent.get(e);
        Map<EventKey, Participant> eventMap = byContact.get(c);
        assert contactMap != null && contactMap.containsKey(c) : ASSERTION_MISSING_CONTACT;
        assert eventMap != null && eventMap.containsKey(e) : ASSERTION_MISSING_EVENT;

        removeContactFromEvent(c, e);
        removeEventFromContact(e, c);
    }

    /**
     * Updates the participant link between the given contact and event with the new status.
     */
    public void setParticipant(Contact contact, Event event, ParticipantStatus newStatus) {
        removeParticipant(contact, event);
        addParticipant(contact, event, newStatus);
    }

    /**
     * Removes the given contact, and also removes the same contact from all linked events.
     */
    public void removeContact(Contact contact) {
        requireAllNonNull(contact);
        ContactKey c = contact.getPrimaryKey();
        if (!byContact.containsKey(c)) {
            return;
        }

        Map<EventKey, Participant> eventMap = byContact.remove(c);
        if (eventMap != null) {
            for (EventKey e : eventMap.keySet()) {
                removeContactFromEvent(c, e);
            }
        }
    }

    /**
     * Updates the given contact, and also updates the same contact across all linked events.
     */
    public void setContact(Contact oldContact, Contact newContact) {
        requireAllNonNull(oldContact, newContact);
        ContactKey c1 = oldContact.getPrimaryKey();
        ContactKey c2 = newContact.getPrimaryKey();
        Map<EventKey, Participant> oldEventMap = byContact.remove(c1);
        if (oldEventMap == null) {
            return;
        }

        Map<EventKey, Participant> newEventMap = new HashMap<>();
        for (Map.Entry<EventKey, Participant> entry : oldEventMap.entrySet()) {
            EventKey e = entry.getKey();
            Participant oldParticipant = entry.getValue();
            Participant newParticipant = new Participant(newContact,
                    oldParticipant.getEvent(), oldParticipant.getStatus());

            newEventMap.put(e, newParticipant);
            Map<ContactKey, Participant> contactMap = byEvent.get(e);
            assert contactMap != null && contactMap.containsKey(c1) : ASSERTION_MISMATCH_MAPS;
            contactMap.remove(c1);
            contactMap.put(c2, newParticipant);
        }
        byContact.put(c2, newEventMap);
    }

    /**
     * Removes all participant event links associated with the given event.
     */
    public void removeEvent(Event event) {
        requireAllNonNull(event);
        EventKey e = event.getPrimaryKey();
        if (!byEvent.containsKey(e)) {
            return;
        }

        Map<ContactKey, Participant> contactMap = byEvent.remove(e);
        if (contactMap != null) {
            for (ContactKey c : contactMap.keySet()) {
                removeEventFromContact(e, c);
            }
        }
    }

    /**
     * Updates the given event, and also updates the same event across all linked contacts.
     */
    public void setEvent(Event oldEvent, Event newEvent) {
        requireAllNonNull(oldEvent, newEvent);
        EventKey e1 = oldEvent.getPrimaryKey();
        EventKey e2 = newEvent.getPrimaryKey();
        Map<ContactKey, Participant> oldContactMap = byEvent.remove(e1);
        if (oldContactMap == null) {
            return;
        }

        Map<ContactKey, Participant> newContactMap = new HashMap<>();
        for (Map.Entry<ContactKey, Participant> entry : oldContactMap.entrySet()) {
            ContactKey c = entry.getKey();
            Participant oldParticipant = entry.getValue();
            Participant newParticipant = new Participant(oldParticipant.getContact(), newEvent,
                    oldParticipant.getStatus());

            newContactMap.put(c, newParticipant);
            Map<EventKey, Participant> eventMap = byContact.get(c);
            assert eventMap != null && eventMap.containsKey(e1) : ASSERTION_MISMATCH_MAPS;
            eventMap.remove(e1);
            eventMap.put(e2, newParticipant);
        }
        byEvent.put(e2, newContactMap);
    }

    /**
     * Checks if the given contact is a participant in the given event.
     */
    public boolean hasParticipant(Contact contact, Event event) {
        requireAllNonNull(contact, event);
        ContactKey c = contact.getPrimaryKey();
        EventKey e = event.getPrimaryKey();

        boolean inByContact = byContact.get(c) != null && byContact.get(c).containsKey(e);
        assert inByContact == (byEvent.get(e) != null && byEvent.get(e).containsKey(c)) : ASSERTION_MISMATCH_MAPS;

        return inByContact;
    }

    @Override
    public List<Participant> getParticipants(Contact contact) {
        requireAllNonNull(contact);
        Map<EventKey, Participant> events = byContact.get(contact.getPrimaryKey());
        if (events == null) {
            return List.of();
        }
        return events.values().stream().toList();
    }

    @Override
    public List<Participant> getParticipants(Event event) {
        requireAllNonNull(event);
        Map<ContactKey, Participant> contacts = byEvent.get(event.getPrimaryKey());
        if (contacts == null) {
            return List.of();
        }
        return contacts.values().stream().toList();
    }

    @Override
    public List<Participant> getAllParticipants() {
        return byEvent.values().stream()
                .flatMap(contactMap -> contactMap.values().stream())
                .toList();
    }

    /**
     * Sets all participants from the {@code source} ReadOnlyParticipantMap
     */
    public void setFrom(ReadOnlyParticipantMap source) {
        this.byEvent.clear();
        this.byContact.clear();
        for (Participant participant : source.getAllParticipants()) {
            this.addParticipant(participant.getContact(),
                    participant.getEvent(), participant.getStatus());
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ParticipantMap)) {
            return false;
        }
        ParticipantMap otherMap = (ParticipantMap) other;
        return this.byContact.equals(otherMap.byContact)
                && this.byEvent.equals(otherMap.byEvent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(byContact, byEvent);
    }
}
