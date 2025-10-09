package nusemp.model.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import nusemp.model.person.Person;
import nusemp.testutil.PersonBuilder;

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

        Set<Person> participants = new HashSet<Person>();
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder().withName("Bob").build();

        participants.add(alice);
        participants.add(bob);

        Event event = new Event(name, date, participants);
        assertEquals(participants, event.getParticipants());
    }

    @Test
    public void withParticipants_addPerson_returnsSetWithPerson() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");

        Set<Person> participants1 = new HashSet<Person>();
        Person bob = new PersonBuilder().withName("Bob").build();
        Set<Person> participants2 = new HashSet<Person>();
        participants2.add(bob);

        Event event1 = new Event(name, date, participants1);
        Event event2 = new Event(name, date, participants2);
        assertEquals(event1.withParticipant(bob), event2);
    }

    @Test
    public void withoutParticipants_removePerson_returnsSetWithoutPerson() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");

        Set<Person> participants1 = createParticipantSet("Bob");
        Person bob = new PersonBuilder().withName("Bob").build();
        Set<Person> participants2 = new HashSet<Person>();

        Event event1 = new Event(name, date, participants1);
        Event event2 = new Event(name, date, participants2);
        assertEquals(event1.withoutParticipant(bob), event2);
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
        Set<Person> participants1 = createParticipantSet("Alice", "Bob");

        EventName name2 = new EventName("Meeting");
        EventDate date2 = new EventDate("01-10-2025 14:00");
        Set<Person> participants2 = createParticipantSet("Alice", "Bob");

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
        Set<Person> participants1 = createParticipantSet("Alice", "Bob");

        EventName name2 = new EventName("Meeting");
        EventDate date2 = new EventDate("01-10-2025 14:00");
        Set<Person> participants2 = createParticipantSet("Alice", "Charlie");

        Event event1 = new Event(name1, date1, participants1);
        Event event2 = new Event(name2, date2, participants2);
        assertNotEquals(event1, event2);
    }

    @Test
    public void equals_differentName_returnsFalse() {
        EventName name1 = new EventName("Meeting");
        EventName name2 = new EventName("Conference");
        EventDate date = new EventDate("01-10-2025 14:00");

        Set<Person> participants = createParticipantSet("Alice", "Bob");

        Event event1 = new Event(name1, date, participants);
        Event event2 = new Event(name2, date, participants);
        assertNotEquals(event1, event2);
    }

    @Test
    public void equals_differentDate_returnsFalse() {
        EventName name = new EventName("Meeting");
        EventDate date1 = new EventDate("01-10-2025 14:00");
        EventDate date2 = new EventDate("02-10-2025 14:00");

        Set<Person> participants = createParticipantSet("Alice", "Bob");

        Event event1 = new Event(name, date1, participants);
        Event event2 = new Event(name, date2, participants);
        assertNotEquals(event1, event2);
    }

    private Set<Person> createParticipantSet(String... names) {
        Set<Person> participants = new HashSet<>();
        for (String name : names) {
            participants.add(new PersonBuilder().withName(name).build());
        }
        return participants;
    }
}
