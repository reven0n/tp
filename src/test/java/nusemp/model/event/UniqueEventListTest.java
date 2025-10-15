package nusemp.model.event;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UniqueEventListTest {
    @Test
    public void contains_nullEvent_throwsNullPointerException() {
        UniqueEventList uniqueEventList = new UniqueEventList();
        assertThrows(NullPointerException.class, () -> uniqueEventList.contains(null));
    }

    @Test
    public void contains_eventNotInList_returnsFalse() {
        UniqueEventList uniqueEventList = new UniqueEventList();
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");
        Event event = new Event(name, date);

        assertFalse(uniqueEventList.contains(event));
    }

    @Test
    public void contains_eventInList_returnsTrue() {
        UniqueEventList uniqueEventList = new UniqueEventList();
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");
        Event event = new Event(name, date);
        uniqueEventList.add(event);

        assert(uniqueEventList.contains(event));
    }
}
