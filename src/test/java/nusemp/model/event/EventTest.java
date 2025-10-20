package nusemp.model.event;

import static nusemp.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nusemp.model.contact.Contact;
import nusemp.model.event.exceptions.DuplicateParticipantException;
import nusemp.testutil.ContactBuilder;

class EventTest {
    @Test
    public void getName_validName_returnsSameName() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");
        Event event = new Event(name, date);
        assertEquals(name, event.getName());
    }

    @Test
    public void getDate_validDate_returnsSameDate() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");
        Event event = new Event(name, date);
        assertEquals(date, event.getDate());
    }

    @Test
    public void getParticipants_noParticipants_returnsEmptySet() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");
        Event event = new Event(name, date);
        assertEquals(0, event.getParticipants().size());
    }

    @Test
    public void getParticipants_validParticipants_returnsSameParticipants() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");

        List<Contact> participants = new ArrayList<>();
        Contact alice = new ContactBuilder().withName("Alice").withEmail("alice@example.com").build();
        Contact bob = new ContactBuilder().withName("Bob").withEmail("bob@example.com").build();

        participants.add(alice);
        participants.add(bob);

        Event event = new Event(name, date, participants);
        assertEquals(participants, event.getParticipants());
    }

    @Test
    public void withParticipants_addContact_returnsSetWithContact() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");

        List<Contact> participants1 = new ArrayList<>();
        Contact bob = new ContactBuilder().withName("Bob").build();
        List<Contact> participants2 = new ArrayList<>();
        participants2.add(bob);

        Event event1 = new Event(name, date, participants1);
        Event event2 = new Event(name, date, participants2);
        assertEquals(event1.withParticipant(bob), event2);
        assertNotEquals(event1, event1.withParticipant(bob)); // should be different instances
    }

    @Test
    public void withParticipants_addContact_keepsOrder() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");

        for (int i = 0; i < 5; i++) { // test multiple times to ensure order is maintained
            List<Contact> participants1 = createParticipantList("Alice", "Charlie");
            Event event = new Event(name, date, participants1);
            Contact bob = new ContactBuilder().withName("Bob").withEmail("bob2@example.com").build();
            List<Contact> expectedParticipants = createParticipantList("Alice", "Charlie", "Bob");
            assertEquals(event.withParticipant(bob).getParticipants().toString(), expectedParticipants.toString());
        }
    }

    @Test
    public void withoutParticipants_removeContact_returnsSetWithoutContact() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");

        List<Contact> participants1 = createParticipantList("Bob");
        Contact bob = new ContactBuilder().withName("Bob").withEmail("bob0@example.com").build();
        List<Contact> participants2 = new ArrayList<>();

        Event event1 = new Event(name, date, participants1);
        Event event2 = new Event(name, date, participants2);
        assertEquals(event1.withoutParticipant(bob), event2);
        assertNotEquals(event1, event1.withoutParticipant(bob)); // should be different instances
    }

    @Test
    public void withoutParticipants_removeContactFromEmptyList_returnsEmptySet() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");
        Event event1 = new Event(name, date);
        Contact bob = new ContactBuilder().withName("Bob").withEmail("bob0@example.com").build();
        assertEquals(event1.withoutParticipant(bob), event1);
    }

    @Test
    public void withoutParticipants_removeContact_keepsOrder() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");

        for (int i = 0; i < 5; i++) { // test multiple times to ensure order is maintained
            List<Contact> participants1 = createParticipantList("Alice", "Bob", "Charlie");
            Event event = new Event(name, date, participants1);
            Contact bob = new ContactBuilder().withName("Bob").withEmail("bob1@example.com").build();
            List<Contact> expectedParticipants = createParticipantList("Alice");
            expectedParticipants.add(new ContactBuilder().withName("Charlie")
                    .withEmail("charlie2@example.com").build());
            assertEquals(event.withoutParticipant(bob).getParticipants().toString(), expectedParticipants.toString());
        }
    }

    @Test
    public void isSameEvent_sameNameDifferentDate_returnsTrue() {
        EventName name = new EventName("Meeting");
        EventDate date1 = new EventDate("01-10-2025 14:00");
        EventDate date2 = new EventDate("02-10-2025 14:00");

        Event event1 = new Event(name, date1);
        Event event2 = new Event(name, date2);
        assertTrue(event1.isSameEvent(event2));
    }

    @Test
    public void isSameEvent_differentNameSameDate_returnsFalse() {
        EventName name1 = new EventName("Meeting");
        EventName name2 = new EventName("Conference");
        EventDate date = new EventDate("01-10-2025 14:00");

        Event event1 = new Event(name1, date);
        Event event2 = new Event(name2, date);
        assertFalse(event1.isSameEvent(event2));
    }

    @Test
    public void isSameEvent_differentNameDifferentDate_returnsFalse() {
        EventName name1 = new EventName("Meeting");
        EventName name2 = new EventName("Conference");
        EventDate date1 = new EventDate("01-10-2025 14:00");
        EventDate date2 = new EventDate("02-10-2025 14:00");

        Event event1 = new Event(name1, date1);
        Event event2 = new Event(name2, date2);
        assertFalse(event1.isSameEvent(event2));
    }

    @Test
    public void isSameEvent_sameNameSameDate_returnsTrue() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");

        Event event1 = new Event(name, date);
        Event event2 = new Event(name, date);
        assertTrue(event1.isSameEvent(event2));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        EventName name1 = new EventName("Meeting");
        EventDate date1 = new EventDate("01-10-2025 14:00");
        List<Contact> participants1 = createParticipantList("Alice", "Bob");

        EventName name2 = new EventName("Meeting");
        EventDate date2 = new EventDate("01-10-2025 14:00");
        List<Contact> participants2 = createParticipantList("Alice", "Bob");

        Event event1 = new Event(name1, date1, participants1);
        Event event2 = new Event(name2, date2, participants2);
        assertEquals(event1, event2);
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");
        Event event = new Event(name, date);
        assertEquals(event, event);
    }

    @Test
    public void equals_null_returnsFalse() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");
        Event event = new Event(name, date);
        assertNotEquals(null, event);
    }

    @Test
    public void equals_differentParticipants_returnsFalse() {
        EventName name1 = new EventName("Meeting");
        EventDate date1 = new EventDate("01-10-2025 14:00");
        List<Contact> participants1 = createParticipantList("Alice", "Bob");

        EventName name2 = new EventName("Meeting");
        EventDate date2 = new EventDate("01-10-2025 14:00");
        List<Contact> participants2 = createParticipantList("Alice", "Charlie");

        Event event1 = new Event(name1, date1, participants1);
        Event event2 = new Event(name2, date2, participants2);
        assertNotEquals(event1, event2);
    }

    @Test
    public void equals_differentName_returnsFalse() {
        EventName name1 = new EventName("Meeting");
        EventName name2 = new EventName("Conference");
        EventDate date = new EventDate("01-10-2025 14:00");

        List<Contact> participants = createParticipantList("Alice", "Bob");

        Event event1 = new Event(name1, date, participants);
        Event event2 = new Event(name2, date, participants);
        assertNotEquals(event1, event2);
    }

    @Test
    public void equals_differentDate_returnsFalse() {
        EventName name = new EventName("Meeting");
        EventDate date1 = new EventDate("01-10-2025 14:00");
        EventDate date2 = new EventDate("02-10-2025 14:00");

        List<Contact> participants = createParticipantList("Alice", "Bob");

        Event event1 = new Event(name, date1, participants);
        Event event2 = new Event(name, date2, participants);
        assertNotEquals(event1, event2);
    }

    @Test
    public void constructor_duplicateEmails_throwsDuplicateParticipantException() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");

        // Create two contacts with the same email but different names
        Contact alice1 = new ContactBuilder().withName("Alice").withEmail("alice@example.com").build();
        Contact alice2 = new ContactBuilder().withName("Alice Smith").withEmail("alice@example.com").build();

        List<Contact> participantsWithDuplicateEmail = new ArrayList<>();
        participantsWithDuplicateEmail.add(alice1);
        participantsWithDuplicateEmail.add(alice2);

        assertThrows(DuplicateParticipantException.class, () -> new Event(name, date, participantsWithDuplicateEmail));
    }

    @Test
    public void withParticipant_duplicateEmail_throwsDuplicateParticipantException() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");

        Contact alice1 = new ContactBuilder().withName("Alice").withEmail("alice@example.com").build();
        Contact alice2 = new ContactBuilder().withName("Alice Smith").withEmail("alice@example.com").build();

        List<Contact> participants = new ArrayList<>();
        participants.add(alice1);
        Event event = new Event(name, date, participants);

        assertThrows(DuplicateParticipantException.class, () -> event.withParticipant(alice2));
    }

    private List<Contact> createParticipantList(String... names) {
        List<Contact> participants = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            participants.add(new ContactBuilder().withName(names[i])
                .withEmail(names[i].toLowerCase() + i + "@example.com").build());
        }
        return participants;
    }
}
