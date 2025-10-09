package nusemp.model.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EventDateTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EventDate((String) null));
        assertThrows(NullPointerException.class, () -> new EventDate((LocalDateTime) null));
    }

    @Test
    public void constructor_validInput_eventDateCreated() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 1, 14, 0);
        EventDate eventDate1 = new EventDate(dateTime);
        assertEquals(dateTime, eventDate1.value);

        EventDate eventDate2 = new EventDate("01-01-2025 00:00");
        EventDate eventDate3 = new EventDate("01-01-2025 00:00      ");
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), eventDate2.value);
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), eventDate3.value);
    }

    @Test
    public void isValidEventDate_null_returnsFalse() {
        assertFalse(EventDate.isValidEventDate(null));
    }

    @Test
    public void isValidEventDate_emptyString_returnsFalse() {
        assertFalse(EventDate.isValidEventDate(""));
        assertFalse(EventDate.isValidEventDate("   "));
    }

    @Test
    public void isValidEventDate_invalidFormat_returnsFalse() {
        assertFalse(EventDate.isValidEventDate("2025-10-01 14:00")); // Wrong format
        assertFalse(EventDate.isValidEventDate("01/10/2025 14:00")); // Wrong format
        assertFalse(EventDate.isValidEventDate("01-10-2025 1400")); // Wrong format
        assertFalse(EventDate.isValidEventDate("01-10-25 14:00")); // Wrong format
        assertFalse(EventDate.isValidEventDate("01-10-2025")); // Missing time
        assertFalse(EventDate.isValidEventDate("14:00 01-10-2025")); // Wrong order
        assertFalse(EventDate.isValidEventDate("01-10-2025 2 PM")); // not 24-hour format
        assertFalse(EventDate.isValidEventDate("32-10-2025 14:00")); // Invalid day
        assertFalse(EventDate.isValidEventDate("01-13-2025 14:00")); // Invalid month
        assertFalse(EventDate.isValidEventDate("00-13-2025 14:00")); // Invalid day
    }

    @Test
    public void isValidEventDate_validFormat_returnsTrue() {
        assertTrue(EventDate.isValidEventDate("01-10-2025 14:00"));
        assertTrue(EventDate.isValidEventDate("31-12-2025 23:59"));
        assertTrue(EventDate.isValidEventDate("01-01-2025 00:00"));
        assertTrue(EventDate.isValidEventDate("      01-10-2025 14:00    "));
    }

    @Test
    public void getFormattedDate_validDate_returnsDateFormat() {
        EventDate eventDate = new EventDate("01-10-2025 14:00");
        assertEquals("01-10-2025 14:00", eventDate.getFormattedDate());

        EventDate eventDateWithLeadingSpaces = new EventDate("   01-10-2025 14:00   ");
        assertEquals("01-10-2025 14:00", eventDateWithLeadingSpaces.getFormattedDate());
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        EventDate eventDate1 = new EventDate("01-10-2025 14:00");
        EventDate eventDate2 = new EventDate("01-10-2025 14:00         ");
        assertEquals(eventDate1, eventDate2);
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        EventDate eventDate1 = new EventDate("01-10-2025 14:00");
        EventDate eventDate2 = new EventDate("02-10-2025 14:00");
        assertNotEquals(eventDate1, eventDate2);
    }

    @Test
    public void equals_differentObjectTypes_returnsFalse() {
        EventDate eventDate = new EventDate("01-10-2025 14:00");
        assertNotEquals("HI", eventDate);
    }

    @Test
    public void equals_null_returnsFalse() {
        EventDate eventDate = new EventDate("01-10-2025 14:00");
        assertNotEquals(null, eventDate);
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        EventDate eventDate = new EventDate("01-10-2025 14:00");
        EventDate eventDateDupe = eventDate;
        assertEquals(eventDateDupe, eventDate);
    }
}
