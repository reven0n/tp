package nusemp.model.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;
import nusemp.model.person.Person;
import nusemp.testutil.PersonBuilder;
import org.junit.jupiter.api.Test;


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

    public void withoutParticipants_removePerson_returnsSetWithoutPerson() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");

        Set<Person> participants1 = new HashSet<Person>();
        Person bob = new PersonBuilder().withName("Bob").build();
        participants1.add(bob);
        Set<Person> participants2 = new HashSet<Person>();

        Event event1 = new Event(name, date, participants1);
        Event event2 = new Event(name, date, participants2);
        assertEquals(event1.withoutParticipant(bob), event2);
    }
}
