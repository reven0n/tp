package nusemp.model.event;

import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalEvents.CONFERENCE_FILLED;
import static nusemp.testutil.TypicalEvents.MEETING_FILLED;
import static nusemp.testutil.TypicalEvents.MEETING_WITH_TAGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import nusemp.model.fields.Address;
import nusemp.model.fields.Date;
import nusemp.model.fields.Name;
import nusemp.model.fields.Tag;
import nusemp.testutil.EventBuilder;

class EventTest {
    private static final Name VALID_NAME = new Name("Meeting");
    private static final Date VALID_DATE = new Date("01-10-2025 14:00");
    private static final Address VALID_ADDRESS = new Address("123 Main St");
    private static final Set<Tag> EMPTY_TAG_SET = new HashSet<>();

    @Test
    public void constructor_withTags_success() {
        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag("Music"));
        tags.add(new Tag("Networking"));

        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.STARTING, tags);
        assertEquals(tags, event.getTags());
    }

    @Test
    public void getTags_modifyReturnedSet_doesNotModifyEvent() {
        Event event = MEETING_WITH_TAGS;
        Set<Tag> tags = event.getTags();

        assertThrows(UnsupportedOperationException.class, () -> tags.add(new Tag("NewTag")));
    }

    @Test
    public void hashCode_sameFields_returnsSameHashCode() {
        Event event1 = new EventBuilder(MEETING_FILLED).withTags("Music", "Networking").build();
        Event event2 = new EventBuilder(MEETING_FILLED).withTags("Music", "Networking").build();

        assertEquals(event1.hashCode(), event2.hashCode());
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
                MEETING_FILLED.getAddress(), MEETING_FILLED.getStatus(), MEETING_FILLED.getTags());
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
    }

    @Test
    public void toStringMethod() {
        String expected = Event.class.getCanonicalName() + "{name=" + MEETING_FILLED.getName()
                + ", date=" + MEETING_FILLED.getDate()
                + ", address=" + MEETING_FILLED.getAddress()
                + ", status=" + MEETING_FILLED.getStatus()
                + ", tags=" + MEETING_FILLED.getTags() + "}";
        assertEquals(expected, MEETING_FILLED.toString());
    }

    @Test
    public void constructor_withStatus_success() {
        Event event = new Event(VALID_NAME, VALID_DATE, VALID_ADDRESS, EventStatus.ONGOING, EMPTY_TAG_SET);
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
