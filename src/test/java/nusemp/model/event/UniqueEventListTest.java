package nusemp.model.event;

import static nusemp.testutil.TypicalEvents.CONFERENCE_EMPTY;
import static nusemp.testutil.TypicalEvents.MEETING_EMPTY;
import static nusemp.testutil.TypicalEvents.MEETING_FILLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nusemp.model.event.exceptions.DuplicateEventException;
import nusemp.model.event.exceptions.EventNotFoundException;

class UniqueEventListTest {
    private UniqueEventList uniqueEventList;

    @BeforeEach
    public void setUp() {
        uniqueEventList = new UniqueEventList();
    }

    @Test
    public void contains_nullEvent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueEventList.contains(null));
    }

    @Test
    public void contains_eventNotInList_returnsFalse() {
        assertFalse(uniqueEventList.contains(MEETING_EMPTY));
    }

    @Test
    public void contains_eventInList_returnsTrue() {
        uniqueEventList.add(MEETING_EMPTY);
        assertTrue(uniqueEventList.contains(MEETING_EMPTY));
    }

    @Test
    public void contains_eventWithSameIdentityFieldsInList_returnsTrue() {
        uniqueEventList.add(MEETING_EMPTY);
        assertTrue(uniqueEventList.contains(MEETING_FILLED));
    }

    @Test
    public void add_nullEvent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueEventList.add(null));
    }

    @Test
    public void add_duplicateEvent_throwsDuplicateEventException() {
        uniqueEventList.add(MEETING_EMPTY);
        assertThrows(DuplicateEventException.class, () -> uniqueEventList.add(MEETING_EMPTY));
    }

    @Test
    public void add_duplicateEventWithSameIdentityFields_throwsDuplicateEventException() {
        uniqueEventList.add(MEETING_EMPTY);
        assertThrows(DuplicateEventException.class, () -> uniqueEventList.add(MEETING_FILLED));
    }

    @Test
    public void setEvent_nullParameters_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueEventList.setEvent(null, MEETING_EMPTY));
        assertThrows(NullPointerException.class, () -> uniqueEventList.setEvent(MEETING_EMPTY, null));
    }

    @Test
    public void setEvent_targetEventNotInList_throwsEventNotFoundException() {
        assertThrows(EventNotFoundException.class, () -> uniqueEventList.setEvent(MEETING_EMPTY, MEETING_FILLED));
    }

    @Test
    public void setEvent_setEditedEventHasNonUniqueIdentity_throwsDuplicateEventException() {
        uniqueEventList.add(MEETING_EMPTY);
        uniqueEventList.add(CONFERENCE_EMPTY);
        assertThrows(DuplicateEventException.class, () -> uniqueEventList.setEvent(MEETING_EMPTY, CONFERENCE_EMPTY));
    }

    @Test
    public void setEvent_setEditedEventIsSameEvent_success() {
        uniqueEventList.add(MEETING_EMPTY);
        uniqueEventList.setEvent(MEETING_EMPTY, MEETING_EMPTY);
        UniqueEventList expectedUniqueEventList = new UniqueEventList();
        expectedUniqueEventList.add(MEETING_EMPTY);
        assertEquals(expectedUniqueEventList, uniqueEventList);
    }

    @Test
    public void setEvent_setEditedEventHasDifferentIdentity_success() {
        uniqueEventList.add(MEETING_EMPTY);
        uniqueEventList.setEvent(MEETING_EMPTY, CONFERENCE_EMPTY);
        UniqueEventList expectedUniqueEventList = new UniqueEventList();
        expectedUniqueEventList.add(CONFERENCE_EMPTY);
        assertEquals(expectedUniqueEventList, uniqueEventList);
    }

    @Test
    public void setEvent_setEditedEventHasSameIdentity_success() {
        uniqueEventList.add(MEETING_EMPTY);
        uniqueEventList.setEvent(MEETING_EMPTY, MEETING_FILLED);
        UniqueEventList expectedUniqueEventList = new UniqueEventList();
        expectedUniqueEventList.add(MEETING_FILLED);
        assertEquals(expectedUniqueEventList, uniqueEventList);
    }

    @Test
    public void remove_nullEvent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueEventList.remove(null));
    }

    @Test
    public void remove_eventDoesNotExist_throwsEventNotFoundException() {
        assertThrows(EventNotFoundException.class, () -> uniqueEventList.remove(MEETING_EMPTY));
    }

    @Test
    public void remove_eventWithSameIdentityFields_removesEvent() {
        uniqueEventList.add(MEETING_EMPTY);
        uniqueEventList.remove(MEETING_FILLED);
        UniqueEventList expectedUniqueEventList = new UniqueEventList();
        assertEquals(expectedUniqueEventList, uniqueEventList);
    }

    @Test
    public void remove_existingEvent_removesEvent() {
        uniqueEventList.add(MEETING_EMPTY);
        uniqueEventList.remove(MEETING_EMPTY);
        UniqueEventList expectedUniqueEventList = new UniqueEventList();
        assertEquals(expectedUniqueEventList, uniqueEventList);
    }

    @Test
    public void setEvents_nullUniqueEventList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueEventList.setEvents((UniqueEventList) null));
    }

    @Test
    public void setEvents_uniqueEventList_replacesOwnListWithProvidedUniqueEventList() {
        uniqueEventList.add(MEETING_EMPTY);
        UniqueEventList expectedUniqueEventList = new UniqueEventList();
        expectedUniqueEventList.add(CONFERENCE_EMPTY);
        uniqueEventList.setEvents(expectedUniqueEventList);
        assertEquals(expectedUniqueEventList, uniqueEventList);
    }

    @Test
    public void setEvents_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueEventList.setEvents((java.util.List<Event>) null));
    }

    @Test
    public void setEvents_list_replacesOwnListWithProvidedList() {
        uniqueEventList.add(MEETING_EMPTY);
        java.util.List<Event> eventList = java.util.Arrays.asList(CONFERENCE_EMPTY);
        uniqueEventList.setEvents(eventList);
        UniqueEventList expectedUniqueEventList = new UniqueEventList();
        expectedUniqueEventList.add(CONFERENCE_EMPTY);
        assertEquals(expectedUniqueEventList, uniqueEventList);
    }

    @Test
    public void setEvents_emptyList_clearsOwnList() {
        uniqueEventList.add(MEETING_EMPTY);
        uniqueEventList.setEvents(new java.util.ArrayList<>());
        UniqueEventList expectedUniqueEventList = new UniqueEventList();
        assertEquals(expectedUniqueEventList, uniqueEventList);
    }

    @Test
    public void setEvents_nonUniqueEventList_throwsDuplicateEventException() {
        assertThrows(DuplicateEventException.class, () -> uniqueEventList.setEvents(
                java.util.Arrays.asList(MEETING_EMPTY, MEETING_EMPTY)));
    }

    @Test
    public void equals() {
        uniqueEventList.add(MEETING_EMPTY);
        UniqueEventList anotherList = new UniqueEventList();
        anotherList.add(MEETING_EMPTY);
        assertEquals(uniqueEventList, anotherList);
        assertEquals(uniqueEventList, uniqueEventList);
        assertNotEquals(null, uniqueEventList);
        assertNotEquals("Some String", uniqueEventList);
        anotherList.add(CONFERENCE_EMPTY);
        assertNotEquals(uniqueEventList, anotherList);
    }
}
