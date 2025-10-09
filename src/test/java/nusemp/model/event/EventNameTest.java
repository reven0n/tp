package nusemp.model.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventNameTest {
    @Test
    void isValidName_validName_success() {
        String validName = "Birthday Party";
        String validName2 = "1 ";

        assertTrue(EventName.isValidEventName(validName));
        assertTrue(EventName.isValidEventName(validName2));
    }

    @Test
    void isValidName_invalidName_failure() {
        String invalidName = "   ";
        assertFalse(EventName.isValidEventName(invalidName));
        assertFalse(EventName.isValidEventName(null));
    }

    @Test
    void constructor_validName_trimsAndStoresValue() {
        EventName eventName = new EventName("  My Event  ");
        assertEquals("My Event", eventName.value);
    }

    @Test
    void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "   ";
        assertThrows(IllegalArgumentException.class, () -> new EventName(invalidName));
    }

    @Test
    void constructor_nullName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EventName(null));
    }

    @Test
    void equals_sameName_returnsTrue() {
        EventName eventName1 = new EventName("Event");
        EventName eventName2 = new EventName("Event");
        assertEquals(eventName1, eventName2);
    }

    @Test
    void equals_sameNameUntrimmed_returnsTrue() {
        EventName eventName1 = new EventName("Event");
        EventName eventName2 = new EventName("  Event  ");
        assertEquals(eventName1, eventName2);
    }

    @Test
    void equals_differentName_returnsFalse() {
        EventName eventName1 = new EventName("Event1");
        EventName eventName2 = new EventName("Event2");
        assertNotEquals(eventName1, eventName2);
    }

    @Test
    void equals_sameObject_returnsTrue() {
        EventName eventName = new EventName("Event");
        EventName eventNameDupe = eventName;
        assertEquals(eventName, eventNameDupe);
    }

    @Test
    void equals_null_returnsFalse() {
        EventName eventName = new EventName("Event");
        assertNotEquals(null, eventName);
    }

    @Test
    void equals_differentType_returnsFalse() {
        EventName eventName = new EventName("Event");
        assertNotEquals("Some String", eventName);
    }
}