package nusemp.model.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EventStatusTest {

    @Test
    public void toString_validStatus_returnsLowercase() {
        assertEquals("pending", EventStatus.PENDING.toString());
        assertEquals("ongoing", EventStatus.ONGOING.toString());
        assertEquals("done", EventStatus.DONE.toString());
    }

    @Test
    public void fromString_validStatusUppercase_success() {
        assertEquals(EventStatus.PENDING, EventStatus.fromString("PENDING"));
        assertEquals(EventStatus.ONGOING, EventStatus.fromString("ONGOING"));
        assertEquals(EventStatus.DONE, EventStatus.fromString("DONE"));
    }

    @Test
    public void fromString_validStatusLowercase_success() {
        assertEquals(EventStatus.PENDING, EventStatus.fromString("pending"));
        assertEquals(EventStatus.ONGOING, EventStatus.fromString("ongoing"));
        assertEquals(EventStatus.DONE, EventStatus.fromString("done"));
    }

    @Test
    public void fromString_validStatusMixedCase_success() {
        assertEquals(EventStatus.PENDING, EventStatus.fromString("PeNdInG"));
        assertEquals(EventStatus.ONGOING, EventStatus.fromString("OnGoInG"));
        assertEquals(EventStatus.DONE, EventStatus.fromString("DoNe"));
    }

    @Test
    public void fromString_invalidStatus_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> EventStatus.fromString("invalid"));
        assertThrows(IllegalArgumentException.class, () -> EventStatus.fromString(""));
        assertThrows(IllegalArgumentException.class, () -> EventStatus.fromString("STARTING"));
    }

    @Test
    public void fromString_nullStatus_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> EventStatus.fromString(null));
    }

    @Test
    public void isValidEventStatus_validStatus_returnsTrue() {
        assertTrue(EventStatus.isValidEventStatus("PENDING"));
        assertTrue(EventStatus.isValidEventStatus("pending"));
        assertTrue(EventStatus.isValidEventStatus("PeNdInG"));
        assertTrue(EventStatus.isValidEventStatus("ONGOING"));
        assertTrue(EventStatus.isValidEventStatus("ongoing"));
        assertTrue(EventStatus.isValidEventStatus("DONE"));
        assertTrue(EventStatus.isValidEventStatus("done"));
    }

    @Test
    public void isValidEventStatus_invalidStatus_returnsFalse() {
        assertFalse(EventStatus.isValidEventStatus("invalid"));
        assertFalse(EventStatus.isValidEventStatus(""));
        assertFalse(EventStatus.isValidEventStatus("STARTING"));
        assertFalse(EventStatus.isValidEventStatus("ATTENDING"));
        assertFalse(EventStatus.isValidEventStatus(null));
    }

    @Test
    public void isValidEventStatus_whitespace_returnsFalse() {
        assertFalse(EventStatus.isValidEventStatus(" "));
        assertFalse(EventStatus.isValidEventStatus("  PENDING  "));
    }
}
