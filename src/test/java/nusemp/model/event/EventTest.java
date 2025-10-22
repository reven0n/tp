package nusemp.model.event;

import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalContacts.ALICE;
import static nusemp.testutil.TypicalContacts.BOB;
import static nusemp.testutil.TypicalContacts.CARL;
import static nusemp.testutil.TypicalContacts.DANIEL;
import static nusemp.testutil.TypicalEvents.CONFERENCE_FILLED;
import static nusemp.testutil.TypicalEvents.MEETING_FILLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nusemp.model.contact.Contact;
import nusemp.model.event.exceptions.DuplicateParticipantException;
import nusemp.model.fields.Address;
import nusemp.model.fields.Date;
import nusemp.model.fields.Name;
import nusemp.testutil.ContactBuilder;
import nusemp.testutil.EventBuilder;

class EventTest {
    private static final Name VALID_NAME = new Name("Meeting");
    private static final Date VALID_DATE = new Date("01-10-2025 14:00");
    private static final Address VALID_ADDRESS = new Address("123 Main St");
    private static final List<ContactStatus> EMPTY_PARTICIPANT_LIST = new ArrayList<>();
    private static final List<ContactStatus> VALID_PARTICIPANTS = createParticipantStatusList(ALICE, BOB);

    private static List<Contact> createParticipantList(Contact... contacts) {
        return new ArrayList<>(List.of(contacts));
    }

    private static List<ContactStatus> createParticipantStatusList(Contact... contacts) {
        List<ContactStatus> participantStatuses = new ArrayList<>();
        for (Contact contact : contacts) {
            participantStatuses.add(new ContactStatus(contact));
        }
        return participantStatuses;
    }

    @Test
    public void constructor_duplicateEmails_throwsDuplicateParticipantException() {
        List<ContactStatus> participantsWithDuplicateEmail = createParticipantStatusList(ALICE,
                new ContactBuilder(BOB).withEmail(ALICE.getEmail().value).build());

        assertThrows(DuplicateParticipantException.class, () ->
                new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, participantsWithDuplicateEmail));
    }

    @Test
    public void withParticipants_addContact_returnsEventWithContact() {
        Event event1 = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EMPTY_PARTICIPANT_LIST);
        Event event2 = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, createParticipantStatusList(BOB));
        assertEquals(event1.withParticipantStatus(BOB), event2);
        assertNotEquals(event1, event1.withParticipantStatus(BOB)); // should be different instances
    }

    @Test
    public void withParticipants_addContact_keepsInsertionOrder() {
        for (int i = 0; i < 5; i++) { // test multiple times to ensure order is maintained
            Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, createParticipantStatusList(ALICE, BOB));
            List<ContactStatus> expectedParticipants = createParticipantStatusList(ALICE, BOB, CARL, DANIEL);
            assertEquals(expectedParticipants,
                    event.withParticipantStatus(CARL).withParticipantStatus(DANIEL).getParticipants());
        }
    }

    @Test
    public void withParticipant_duplicateEmail_throwsException() {
        Contact contact = new ContactBuilder().withEmail(BOB.getEmail().value).build();
        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, VALID_PARTICIPANTS);

        assertThrows(DuplicateParticipantException.class, () -> event.withParticipantStatus(contact));
    }

    @Test
    public void withoutParticipants_removeContact_returnsEventWithoutContact() {
        Event event1 = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, createParticipantStatusList(BOB));
        Event event2 = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EMPTY_PARTICIPANT_LIST);
        assertEquals(event1.withoutParticipantStatus(BOB), event2);
        assertNotEquals(event1, event1.withoutParticipantStatus(BOB)); // should be different instances
    }

    @Test
    public void withoutParticipants_removeContactFromEmptyList_doesNotThrowError() {
        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EMPTY_PARTICIPANT_LIST);
        assertEquals(event, event.withoutParticipantStatus(BOB));
    }

    @Test
    public void withoutParticipants_removeContact_keepsInsertionOrder() {
        for (int i = 0; i < 5; i++) { // test multiple times to ensure order is maintained
            Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS,
                    createParticipantStatusList(ALICE, BOB, CARL, DANIEL));
            List<ContactStatus> expectedParticipants = createParticipantStatusList(BOB, DANIEL);
            assertEquals(expectedParticipants,
                    event.withoutParticipantStatus(CARL).withoutParticipantStatus(ALICE).getParticipants());
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
                MEETING_FILLED.getAddress(), MEETING_FILLED.getParticipants());
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

        // different participants -> returns false
        editedEvent = new EventBuilder(MEETING_FILLED)
                .withParticipants(VALID_PARTICIPANTS.toArray(ContactStatus[]::new)).build();
        assertFalse(MEETING_FILLED.equals(editedEvent));
    }

    @Test
    public void toStringMethod() {
        String expected = Event.class.getCanonicalName() + "{name=" + MEETING_FILLED.getName()
                + ", date=" + MEETING_FILLED.getDate()
                + ", address=" + MEETING_FILLED.getAddress()
                + ", participants=" + MEETING_FILLED.getParticipants() + "}";
        assertEquals(expected, MEETING_FILLED.toString());
    }
}
