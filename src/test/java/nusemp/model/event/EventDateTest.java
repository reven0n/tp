package nusemp.model.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import nusemp.model.fields.Date;

class EventDateTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Date((String) null));
        assertThrows(NullPointerException.class, () -> new Date((LocalDateTime) null));
    }

    @Test
    public void constructor_validInput_eventDateCreated() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 1, 14, 0);
        Date date1 = new Date(dateTime);
        assertEquals(dateTime, date1.value);

        Date date2 = new Date("01-01-2025 00:00");
        Date date3 = new Date("01-01-2025 00:00      "); // untrimmed input
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), date2.value);
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), date3.value);
    }

    @Test
    public void isValidEventDate_null_returnsFalse() {
        assertFalse(Date.isValidEventDate(null));
    }

    @Test
    public void isValidEventDate_emptyString_returnsFalse() {
        assertFalse(Date.isValidEventDate(""));
        assertFalse(Date.isValidEventDate("   "));
    }

    @Test
    public void isValidEventDate_invalidFormat_returnsFalse() {
        assertFalse(Date.isValidEventDate("2025-10-01 14:00")); // Wrong format
        assertFalse(Date.isValidEventDate("01/10/2025 14:00")); // Wrong format
        assertFalse(Date.isValidEventDate("01-10-2025 1400")); // Wrong format
        assertFalse(Date.isValidEventDate("01-10-25 14:00")); // Wrong format
        assertFalse(Date.isValidEventDate("01-10-2025")); // Missing time
        assertFalse(Date.isValidEventDate("14:00 01-10-2025")); // Wrong order
        assertFalse(Date.isValidEventDate("01-10-2025 2 PM")); // not 24-hour format
        assertFalse(Date.isValidEventDate("32-10-2025 14:00")); // Invalid day
        assertFalse(Date.isValidEventDate("01-13-2025 14:00")); // Invalid month
        assertFalse(Date.isValidEventDate("00-13-2025 14:00")); // Invalid day
    }

    @Test
    public void isValidEventDate_validFormat_returnsTrue() {
        assertTrue(Date.isValidEventDate("01-10-2025 14:00"));
        assertTrue(Date.isValidEventDate("31-12-2025 23:59"));
        assertTrue(Date.isValidEventDate("01-01-2025 00:00"));
        assertTrue(Date.isValidEventDate("      01-10-2025 14:00    "));
    }

    @Test
    public void getFormattedDate_validDate_returnsDateFormat() {
        Date date = new Date("01-10-2025 14:00");
        assertEquals("01-10-2025 14:00", date.getFormattedDate());

        Date dateWithLeadingSpaces = new Date("   01-10-2025 14:00   ");
        assertEquals("01-10-2025 14:00", dateWithLeadingSpaces.getFormattedDate());
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        Date date1 = new Date("01-10-2025 14:00");
        Date date2 = new Date("01-10-2025 14:00         ");
        assertEquals(date1, date2);
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        Date date1 = new Date("01-10-2025 14:00");
        Date date2 = new Date("02-10-2025 14:00");
        assertNotEquals(date1, date2);
    }

    @Test
    public void equals_differentObjectTypes_returnsFalse() {
        Date date = new Date("01-10-2025 14:00");
        assertNotEquals("HI", date);
    }

    @Test
    public void equals_null_returnsFalse() {
        Date date = new Date("01-10-2025 14:00");
        assertNotEquals(null, date);
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        Date date = new Date("01-10-2025 14:00");
        Date dateDupe = date;
        assertEquals(dateDupe, date);
    }
}
