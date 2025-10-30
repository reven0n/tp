package nusemp.model.participant;

import static nusemp.testutil.TypicalContacts.ALICE;
import static nusemp.testutil.TypicalContacts.BOB;
import static nusemp.testutil.TypicalEvents.CONFERENCE_FILLED;
import static nusemp.testutil.TypicalEvents.MEETING_FILLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.participant.exceptions.DuplicateParticipantException;
import nusemp.model.participant.exceptions.ParticipantNotFoundException;

class ParticipantMapTest {
    @Test
    public void removeContact_noExistingContact_returns() {
        ParticipantMap participantMap = new ParticipantMap();
        participantMap.removeContact(ALICE);
        assertTrue(participantMap.getAllParticipants().isEmpty());
    }

    @Test
    public void removeContact_withExistingContact_removesAllLinks() {
        ParticipantMap participantMap = new ParticipantMap();
        Contact contact = ALICE;
        Event event1 = MEETING_FILLED;
        Event event2 = CONFERENCE_FILLED;
        ParticipantStatus status = ParticipantStatus.AVAILABLE;

        participantMap.addParticipant(contact, event1, status);
        participantMap.addParticipant(contact, event2, status);
        participantMap.removeContact(contact);

        assertTrue(participantMap.getAllParticipants().isEmpty());
    }

    @Test
    public void setContact_noExistingContact_returns() {
        ParticipantMap participantMap = new ParticipantMap();
        participantMap.setContact(ALICE, BOB);
        assertTrue(participantMap.getAllParticipants().isEmpty());
    }

    @Test
    public void setContact_withExistingContact_updatesAllLinks() {
        ParticipantMap participantMap = new ParticipantMap();
        Contact oldContact = ALICE;
        Contact newContact = BOB;
        Event event1 = MEETING_FILLED;
        Event event2 = CONFERENCE_FILLED;
        ParticipantStatus status = ParticipantStatus.AVAILABLE;

        participantMap.addParticipant(oldContact, event1, status);
        participantMap.addParticipant(oldContact, event2, status);
        participantMap.setContact(oldContact, newContact);

        assertFalse(participantMap.hasParticipant(oldContact, event1));
        assertFalse(participantMap.hasParticipant(oldContact, event2));
        assertTrue(participantMap.hasParticipant(newContact, event1));
        assertTrue(participantMap.hasParticipant(newContact, event2));
    }

    @Test
    public void removeEvent_noExistingEvent_returns() {
        ParticipantMap participantMap = new ParticipantMap();
        participantMap.removeEvent(MEETING_FILLED);
        assertTrue(participantMap.getAllParticipants().isEmpty());
    }

    @Test
    public void removeEvent_withExistingEvent_removesAllLinks() {
        ParticipantMap participantMap = new ParticipantMap();
        Contact contact1 = ALICE;
        Contact contact2 = BOB;
        Event event = MEETING_FILLED;
        ParticipantStatus status = ParticipantStatus.AVAILABLE;

        participantMap.addParticipant(contact1, event, status);
        participantMap.addParticipant(contact2, event, status);
        participantMap.removeEvent(event);

        assertTrue(participantMap.getAllParticipants().isEmpty());
    }

    @Test
    public void setEvent_noExistingEvent_returns() {
        ParticipantMap participantMap = new ParticipantMap();
        participantMap.setEvent(MEETING_FILLED, CONFERENCE_FILLED);
        assertTrue(participantMap.getAllParticipants().isEmpty());
    }

    @Test
    public void setEvent_withExistingEvent_updatesAllLinks() {
        ParticipantMap participantMap = new ParticipantMap();
        Contact contact1 = ALICE;
        Contact contact2 = BOB;
        Event oldEvent = MEETING_FILLED;
        Event newEvent = CONFERENCE_FILLED;
        ParticipantStatus status = ParticipantStatus.AVAILABLE;

        participantMap.addParticipant(contact1, oldEvent, status);
        participantMap.addParticipant(contact2, oldEvent, status);
        participantMap.setEvent(oldEvent, newEvent);

        assertFalse(participantMap.hasParticipant(contact1, oldEvent));
        assertFalse(participantMap.hasParticipant(contact2, oldEvent));
        assertTrue(participantMap.hasParticipant(contact1, newEvent));
        assertTrue(participantMap.hasParticipant(contact2, newEvent));
    }

    @Test
    public void addParticipant_withValidContactAndEvent_addsSuccessfully() {
        ParticipantMap participantMap = new ParticipantMap();
        Contact contact = ALICE;
        Event event = MEETING_FILLED;
        ParticipantStatus status = ParticipantStatus.AVAILABLE;

        participantMap.addParticipant(contact, event, status);

        assertTrue(participantMap.hasParticipant(contact, event));
    }

    @Test
    public void addParticipant_withDuplicateParticipant_throwsException() {
        ParticipantMap participantMap = new ParticipantMap();
        Contact contact = ALICE;
        Event event = MEETING_FILLED;
        ParticipantStatus status = ParticipantStatus.AVAILABLE;

        participantMap.addParticipant(contact, event, status);

        assertThrows(DuplicateParticipantException.class, () ->
                participantMap.addParticipant(contact, event, ParticipantStatus.UNAVAILABLE));
    }

    @Test
    public void removeParticipant_withExistingParticipant_removesSuccessfully() {
        ParticipantMap participantMap = new ParticipantMap();
        Contact contact = ALICE;
        Event event = MEETING_FILLED;
        ParticipantStatus status = ParticipantStatus.AVAILABLE;

        participantMap.addParticipant(contact, event, status);
        participantMap.removeParticipant(contact, event);

        assertFalse(participantMap.hasParticipant(contact, event));
    }

    @Test
    public void removeParticipant_withNonExistentParticipant_throwsException() {
        ParticipantMap participantMap = new ParticipantMap();
        assertThrows(ParticipantNotFoundException.class, () -> participantMap.removeParticipant(ALICE, MEETING_FILLED));
    }

    @Test
    public void setParticipant_withExistingParticipant_updatesSuccessfully() {
        ParticipantMap participantMap = new ParticipantMap();
        Contact contact = ALICE;
        Event event = MEETING_FILLED;
        ParticipantStatus oldStatus = ParticipantStatus.AVAILABLE;
        ParticipantStatus newStatus = ParticipantStatus.UNAVAILABLE;

        participantMap.addParticipant(contact, event, oldStatus);
        participantMap.setParticipant(contact, event, newStatus);

        assertEquals(participantMap.getParticipants(contact), List.of(new Participant(contact, event, newStatus)));
        assertEquals(participantMap.getParticipants(event), List.of(new Participant(contact, event, newStatus)));
    }

    @Test
    public void setParticipant_withNonExistentParticipant_throwsException() {
        ParticipantMap participantMap = new ParticipantMap();
        assertThrows(ParticipantNotFoundException.class, () ->
                participantMap.setParticipant(ALICE, MEETING_FILLED, ParticipantStatus.AVAILABLE));
    }

    @Test
    public void hasParticipant_withNonExistentParticipant_returnsFalse() {
        ParticipantMap participantMap = new ParticipantMap();
        assertFalse(participantMap.hasParticipant(ALICE, MEETING_FILLED));
    }

    @Test
    public void hasParticipant_withExistingParticipant_returnsTrue() {
        ParticipantMap participantMap = new ParticipantMap();
        Contact contact = ALICE;
        Event event = MEETING_FILLED;
        ParticipantStatus status = ParticipantStatus.AVAILABLE;

        participantMap.addParticipant(contact, event, status);

        assertTrue(participantMap.hasParticipant(contact, event));
    }

    @Test
    public void getParticipants_getContactParticipants_returnsCorrectParticipants() {
        ParticipantMap participantMap = new ParticipantMap();
        Contact contact1 = ALICE;
        Contact contact2 = BOB;
        Event event1 = MEETING_FILLED;
        Event event2 = CONFERENCE_FILLED;

        participantMap.addParticipant(contact1, event1, ParticipantStatus.AVAILABLE);
        participantMap.addParticipant(contact1, event2, ParticipantStatus.UNAVAILABLE);
        participantMap.addParticipant(contact2, event1, ParticipantStatus.UNKNOWN);

        List<Participant> participants = participantMap.getParticipants(contact1);
        assertEquals(2, participants.size());
        assertTrue(participants.stream().anyMatch(p -> p.getEvent().hasSameFields(event1)));
        assertTrue(participants.stream().anyMatch(p -> p.getEvent().hasSameFields(event2)));

        participants = participantMap.getParticipants(contact2);
        assertEquals(1, participants.size());
        assertTrue(participants.stream().anyMatch(p -> p.getEvent().hasSameFields(event1)));
    }

    @Test
    public void getParticipants_getEventParticipants_returnsCorrectParticipants() {
        ParticipantMap participantMap = new ParticipantMap();
        Contact contact1 = ALICE;
        Contact contact2 = BOB;
        Event event1 = MEETING_FILLED;
        Event event2 = CONFERENCE_FILLED;

        participantMap.addParticipant(contact1, event1, ParticipantStatus.AVAILABLE);
        participantMap.addParticipant(contact1, event2, ParticipantStatus.UNAVAILABLE);
        participantMap.addParticipant(contact2, event1, ParticipantStatus.UNKNOWN);

        List<Participant> participants = participantMap.getParticipants(event1);
        assertEquals(2, participants.size());
        assertTrue(participants.stream().anyMatch(p -> p.getContact().hasSameFields(contact1)));
        assertTrue(participants.stream().anyMatch(p -> p.getContact().hasSameFields(contact2)));

        participants = participantMap.getParticipants(event2);
        assertEquals(1, participants.size());
        assertTrue(participants.stream().anyMatch(p -> p.getContact().hasSameFields(contact1)));
    }

    @Test
    public void getAllParticipants_withMultipleLinks_returnsAllParticipants() {
        ParticipantMap participantMap = new ParticipantMap();
        Contact contact1 = ALICE;
        Contact contact2 = BOB;
        Event event1 = MEETING_FILLED;
        Event event2 = CONFERENCE_FILLED;
        ParticipantStatus status = ParticipantStatus.AVAILABLE;

        participantMap.addParticipant(contact1, event1, status);
        participantMap.addParticipant(contact2, event2, status);
        participantMap.addParticipant(contact1, event2, status);

        List<Participant> participants = participantMap.getAllParticipants();

        assertEquals(3, participants.size());
        assertTrue(participants.stream().anyMatch(p ->
                p.getContact().hasSameFields(contact1) && p.getEvent().hasSameFields(event1)));
        assertTrue(participants.stream().anyMatch(p ->
                p.getContact().hasSameFields(contact2) && p.getEvent().hasSameFields(event2)));
        assertTrue(participants.stream().anyMatch(p ->
                p.getContact().hasSameFields(contact1) && p.getEvent().hasSameFields(event2)));
    }

    @Test
    public void setFrom_withAnotherParticipantMap_copiesAllParticipants() {
        ParticipantMap sourceMap = new ParticipantMap();
        Contact contact1 = ALICE;
        Contact contact2 = BOB;
        Event event1 = MEETING_FILLED;
        Event event2 = CONFERENCE_FILLED;
        ParticipantStatus status = ParticipantStatus.AVAILABLE;

        sourceMap.addParticipant(contact1, event1, status);
        sourceMap.addParticipant(contact2, event2, status);
        sourceMap.addParticipant(contact1, event2, status);

        ParticipantMap targetMap = new ParticipantMap();
        targetMap.setFrom(sourceMap);

        assertEquals(3, targetMap.getAllParticipants().size());
        assertTrue(targetMap.hasParticipant(contact1, event1));
        assertTrue(targetMap.hasParticipant(contact2, event2));
        assertTrue(targetMap.hasParticipant(contact1, event2));
    }

    @Test
    public void equals_sameParticipantMaps_returnTrue() {
        ParticipantMap map1 = new ParticipantMap();
        ParticipantMap map2 = new ParticipantMap();
        assertTrue(map1.equals(map1));
        assertTrue(map1.equals(map2));

        Contact contact1 = ALICE;
        Contact contact2 = BOB;
        Event event1 = MEETING_FILLED;
        Event event2 = CONFERENCE_FILLED;
        ParticipantStatus status = ParticipantStatus.AVAILABLE;

        map1.addParticipant(contact1, event1, status);
        map1.addParticipant(contact2, event2, status);
        map1.addParticipant(contact1, event2, status);

        // Add in different order
        map2.addParticipant(contact2, event2, status);
        map2.addParticipant(contact1, event2, status);
        map2.addParticipant(contact1, event1, status);

        assertTrue(map1.equals(map2));
    }

    @Test
    public void equals_differentParticipantMaps_returnFalse() {
        ParticipantMap map1 = new ParticipantMap();
        ParticipantMap map2 = new ParticipantMap();

        Contact contact1 = ALICE;
        Contact contact2 = BOB;
        Event event1 = MEETING_FILLED;
        Event event2 = CONFERENCE_FILLED;
        ParticipantStatus status = ParticipantStatus.AVAILABLE;

        map1.addParticipant(contact1, event1, status);

        map2.addParticipant(contact2, event2, status);

        assertFalse(map1.equals(map2));
    }

    @Test
    public void equals_differentTypes_returnFalse() {
        ParticipantMap map1 = new ParticipantMap();
        assertFalse(map1.equals(null));
        assertFalse(map1.equals("Some String"));
    }
}
