package nusemp.model.event;

import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalContacts.ALICE;
import static nusemp.testutil.TypicalContacts.BOB;
import static nusemp.testutil.TypicalContacts.CARL;
import static nusemp.testutil.TypicalContacts.DANIEL;
import static nusemp.testutil.TypicalEvents.CONFERENCE_FILLED;
import static nusemp.testutil.TypicalEvents.MEETING_EMPTY;
import static nusemp.testutil.TypicalEvents.MEETING_FILLED;
import static nusemp.testutil.TypicalEvents.MEETING_WITH_TAGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import nusemp.model.contact.Contact;
import nusemp.model.event.exceptions.DuplicateParticipantException;
import nusemp.model.event.exceptions.ParticipantNotFoundException;
import nusemp.model.fields.Address;
import nusemp.model.fields.Date;
import nusemp.model.fields.Name;
import nusemp.model.fields.Tag;
import nusemp.testutil.ContactBuilder;
import nusemp.testutil.EventBuilder;

class EventTest {
    private static final Name VALID_NAME = new Name("Meeting");
    private static final Date VALID_DATE = new Date("01-10-2025 14:00");
    private static final Address VALID_ADDRESS = new Address("123 Main St");
    private static final List<Participant> EMPTY_PARTICIPANT_LIST = new ArrayList<>();
    private static final List<Participant> VALID_PARTICIPANTS = createParticipantList(ALICE, BOB);
    private static final Set<Tag> EMPTY_TAG_SET = new HashSet<>();

    private static List<Participant> createParticipantList(Contact... contacts) {
        List<Participant> participants = new ArrayList<>();
        for (Contact contact : contacts) {
            participants.add(new Participant(contact));
        }
        return participants;
    }

    /**
     * Stricter check for same participants based on all fields.
     */
    private static boolean checkForSameParticipant(Event event1, Event event2) {
        List<Participant> participants1 = event1.getParticipants();
        List<Participant> participants2 = event2.getParticipants();

        if (participants1.size() != participants2.size()) {
            return false;
        }

        for (int i = 0; i < participants1.size(); i++) {
            Participant p1 = participants1.get(i);
            Participant p2 = participants2.get(i);
            if (!p1.equals(p2)) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void constructor_duplicateEmails_throwsDuplicateParticipantException() {
        List<Participant> participantsWithDuplicateEmail = createParticipantList(ALICE,
                new ContactBuilder(BOB).withEmail(ALICE.getEmail().value).build());

        assertThrows(DuplicateParticipantException.class, () ->
                new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS,
                    EventStatus.STARTING, EMPTY_TAG_SET, participantsWithDuplicateEmail));
    }

    @Test
    public void constructor_withTags_success() {
        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag("Music"));
        tags.add(new Tag("Networking"));

        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING, tags,
                EMPTY_PARTICIPANT_LIST);
        assertEquals(tags, event.getTags());
    }

    @Test
    public void getTags_modifyReturnedSet_doesNotModifyEvent() {
        Event event = MEETING_WITH_TAGS;
        Set<Tag> tags = event.getTags();

        assertThrows(UnsupportedOperationException.class, () -> tags.add(new Tag("NewTag")));
    }

    @Test
    public void withUpdatedParticipant_updateParticipantName_returnsEventWithUpdatedParticipant() {
        Contact updatedBob = new ContactBuilder(BOB).withName("Robert").build();
        Participant updatedParticipant = new Participant(updatedBob);
        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING,
                EMPTY_TAG_SET, createParticipantList(BOB, ALICE));
        Event updatedEvent = event.withUpdatedParticipant(updatedParticipant);
        assertFalse(checkForSameParticipant(event, updatedEvent)); //check that other participants are unchanged
        assertEquals(updatedEvent.getParticipants().get(0), updatedParticipant);
        assertEquals(updatedEvent, event);
    }

    @Test
    public void withUpdatedParticipant_updateParticipantStatus_returnsEventWithUpdatedParticipant() {
        Participant updatedParticipant = new Participant(BOB, ParticipantStatus.UNAVAILABLE);
        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING,
                EMPTY_TAG_SET, createParticipantList(BOB, ALICE));
        Event updatedEvent = event.withUpdatedParticipant(updatedParticipant);
        assertFalse(checkForSameParticipant(event, updatedEvent)); //check that other participants are unchanged
        assertEquals(updatedEvent.getParticipants().get(0), updatedParticipant);
        assertEquals(updatedEvent, event);
    }

    @Test
    public void withUpdatedParticipant_participantNotFound_throwsException() {
        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING,
                EMPTY_TAG_SET, createParticipantList(ALICE));

        Participant nonExistentParticipant = new Participant(BOB);

        assertThrows(ParticipantNotFoundException.class, () ->
                event.withUpdatedParticipant(nonExistentParticipant));
    }

    @Test
    public void withContact_addContact_returnsEventWithContact() {
        Event event1 = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING,
                EMPTY_TAG_SET, EMPTY_PARTICIPANT_LIST);
        Event event2 = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING,
                EMPTY_TAG_SET, createParticipantList(BOB));
        assertNotEquals(event1, event1.withContact(BOB)); //check that original event is unchanged
        assertEquals(event1.withContact(BOB), event2);
    }

    @Test
    public void withContact_preservesTags() {
        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag("Music"));
        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING, tags,
                EMPTY_PARTICIPANT_LIST);

        Event eventWithParticipant = event.withContact(BOB);
        assertEquals(tags, eventWithParticipant.getTags());
    }

    @Test
    public void hashCode_sameFields_returnsSameHashCode() {
        Event event1 = new EventBuilder(MEETING_FILLED).withTags("Music", "Networking").build();
        Event event2 = new EventBuilder(MEETING_FILLED).withTags("Music", "Networking").build();

        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    public void withContact_addContact_keepsInsertionOrder() {
        for (int i = 0; i < 5; i++) { // test multiple times to ensure order is maintained
            Event event = new Event(
                    VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING, EMPTY_TAG_SET,
                    createParticipantList(ALICE, BOB));
            List<Participant> expectedParticipants = createParticipantList(ALICE, BOB, CARL, DANIEL);
            assertEquals(expectedParticipants,
                    event.withContact(CARL).withContact(DANIEL).getParticipants());
        }
    }

    @Test
    public void withContact_duplicateEmail_throwsException() {
        Contact contact = new ContactBuilder().withEmail(BOB.getEmail().value).build();
        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING, EMPTY_TAG_SET,
                VALID_PARTICIPANTS);

        assertThrows(DuplicateParticipantException.class, () -> event.withContact(contact));
    }

    @Test
    public void withoutParticipants_removeContact_returnsEventWithoutContact() {
        Event event1 = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING, EMPTY_TAG_SET,
                createParticipantList(BOB));
        Event event2 = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING, EMPTY_TAG_SET,
                EMPTY_PARTICIPANT_LIST);
        assertEquals(event1.withoutContact(BOB), event2);
    }

    @Test
    public void withoutParticipants_removeContactFromEmptyList_doesNotThrowError() {
        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING, EMPTY_TAG_SET,
                EMPTY_PARTICIPANT_LIST);
        assertEquals(event, event.withoutContact(BOB));
    }

    @Test
    public void withoutContact_removeContact_keepsInsertionOrder() {
        for (int i = 0; i < 5; i++) { // test multiple times to ensure order is maintained
            Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING, EMPTY_TAG_SET,
                    createParticipantList(ALICE, BOB, CARL, DANIEL));
            List<Participant> expectedParticipants = createParticipantList(BOB, DANIEL);
            assertEquals(expectedParticipants,
                    event.withoutContact(CARL).withoutContact(ALICE).getParticipants());
        }
    }

    @Test
    public void isSameEvent_sameName_returnsTrue() {
        Name name = VALID_NAME;
        Date date1 = VALID_DATE;
        Date date2 = new Date("02-10-2025 14:00");
        Address address1 = VALID_ADDRESS;
        Address address2 = new Address("456 Another St");

        Event event1 = new Event(name, date1, address1);
        Event event2 = new Event(name, date2, address1);
        Event event3 = new Event(name, date1, address2);
        Event event4 = new Event(name, date2, Address.empty());

        assertTrue(event1.isSameEvent(event1)); // same object
        assertTrue(event1.isSameEvent(event2));
        assertTrue(event1.isSameEvent(event3));
        assertTrue(event1.isSameEvent(event4));
    }

    @Test
    public void isSameEvent_differentName_returnsFalse() {
        Name name1 = new Name("Meeting");
        Name name2 = new Name("Conference");

        Event event1 = new Event(name1, VALID_DATE, VALID_ADDRESS);
        Event event2 = new Event(name2, VALID_DATE, VALID_ADDRESS);
        assertFalse(event1.isSameEvent(event2));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Event event = new Event(MEETING_FILLED.getName(), MEETING_FILLED.getDate(),
                MEETING_FILLED.getAddress(), MEETING_FILLED.getStatus(), MEETING_FILLED.getTags(),
                MEETING_FILLED.getParticipants());
        assertTrue(MEETING_FILLED.equals(event));

        // same object -> returns true
        assertTrue(MEETING_FILLED.equals(MEETING_FILLED));

        // null -> returns false
        assertFalse(MEETING_FILLED.equals(null));

        // different type -> returns false
        assertFalse(MEETING_FILLED.equals(5));

        // different event -> returns false
        assertFalse(MEETING_FILLED.equals(CONFERENCE_FILLED));

        // different name -> returns false
        Event editedEvent = new EventBuilder(MEETING_FILLED).withName("Conference").build();
        assertFalse(MEETING_FILLED.equals(editedEvent));

        // different date -> returns false
        editedEvent = new EventBuilder(MEETING_FILLED).withDate("02-10-2025 14:00").build();
        assertFalse(MEETING_FILLED.equals(editedEvent));

        // different address -> returns false
        editedEvent = new EventBuilder(MEETING_FILLED).withAddress("456 Another St").build();
        assertFalse(MEETING_FILLED.equals(editedEvent));

        // no tags -> returns false
        Event event1 = new EventBuilder(MEETING_FILLED).withTags("Music").build();
        Event event2 = new EventBuilder(MEETING_FILLED).withTags("Networking").build();
        Event event3 = new EventBuilder(MEETING_FILLED).withTags().build();

        assertFalse(event1.equals(event2));
        assertFalse(event1.equals(event3));

        // same tags -> returns true
        Event event4 = new EventBuilder(MEETING_FILLED).withTags("Music", "Networking").build();
        Event event5 = new EventBuilder(MEETING_FILLED).withTags("Music", "Networking").build();

        assertTrue(event4.equals(event5));

        // different participant emails -> returns false
        Contact updatedBobName = new ContactBuilder(BOB).withName("Robert").build();
        Contact updatedBobEmail = new ContactBuilder(BOB).withEmail("bob1234@example.com").build();
        Event event6 = new EventBuilder(MEETING_EMPTY)
                .withParticipants(createParticipantList(ALICE, BOB)).build();
        Event event7 = new EventBuilder(MEETING_EMPTY)
                .withParticipants(createParticipantList(ALICE)).build();
        Event event8 = new EventBuilder(MEETING_EMPTY)
                .withParticipants(createParticipantList(ALICE, updatedBobName)).build();
        Event event9 = new EventBuilder(MEETING_EMPTY)
                .withParticipants(createParticipantList(ALICE, updatedBobEmail)).build();
        Event event10 = new EventBuilder(MEETING_EMPTY)
                .withParticipants(createParticipantList(updatedBobName, ALICE)).build();

        assertNotEquals(event6, event7); // missing participant should not be equal
        assertEquals(event6, event8); // same email, different name should be equal
        assertNotEquals(event6, event9); // different email should not be equal
        assertNotEquals(event6, event10); // different order should not be equal
    }

    @Test
    public void toStringMethod() {
        String expected = Event.class.getCanonicalName() + "{name=" + MEETING_FILLED.getName()
                + ", date=" + MEETING_FILLED.getDate()
                + ", address=" + MEETING_FILLED.getAddress()
                + ", status=" + MEETING_FILLED.getStatus()
                + ", tags=" + MEETING_FILLED.getTags()
                + ", participants=" + MEETING_FILLED.getParticipants() + "}";
        assertEquals(expected, MEETING_FILLED.toString());
    }

    @Test
    public void constructor_withStatus_success() {
        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.ONGOING,
                EMPTY_TAG_SET, EMPTY_PARTICIPANT_LIST);
        assertEquals(EventStatus.ONGOING, event.getStatus());
    }

    @Test
    public void constructor_withoutStatus_defaultsToStarting() {
        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS);
        assertEquals(EventStatus.STARTING, event.getStatus());
    }

    @Test
    public void getStatus_validStatus_returnsCorrectStatus() {
        Event startingEvent = new EventBuilder().withStatus(EventStatus.STARTING).build();
        Event ongoingEvent = new EventBuilder().withStatus(EventStatus.ONGOING).build();
        Event closedEvent = new EventBuilder().withStatus(EventStatus.CLOSED).build();

        assertEquals(EventStatus.STARTING, startingEvent.getStatus());
        assertEquals(EventStatus.ONGOING, ongoingEvent.getStatus());
        assertEquals(EventStatus.CLOSED, closedEvent.getStatus());
    }

    @Test
    public void withContact_preservesStatus() {
        Event event = new EventBuilder().withStatus(EventStatus.ONGOING).build();
        Event updatedEvent = event.withContact(ALICE);

        assertEquals(EventStatus.ONGOING, updatedEvent.getStatus());
        assertTrue(updatedEvent.hasContact(ALICE));
    }

    @Test
    public void withoutContact_preservesStatus() {
        Event event = new EventBuilder()
                .withStatus(EventStatus.CLOSED)
                .withParticipants(VALID_PARTICIPANTS)
                .build();
        Event updatedEvent = event.withoutContact(ALICE);

        assertEquals(EventStatus.CLOSED, updatedEvent.getStatus());
        assertFalse(updatedEvent.hasContact(ALICE));
    }

    @Test
    public void equals_sameStatus_returnsTrue() {
        Event event1 = new EventBuilder().withStatus(EventStatus.ONGOING).build();
        Event event2 = new EventBuilder().withStatus(EventStatus.ONGOING).build();

        assertTrue(event1.equals(event2));
    }

    @Test
    public void equals_differentStatus_returnsFalse() {
        Event event1 = new EventBuilder().withStatus(EventStatus.STARTING).build();
        Event event2 = new EventBuilder().withStatus(EventStatus.ONGOING).build();

        assertFalse(event1.equals(event2));
    }
}
