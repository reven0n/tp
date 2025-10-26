package nusemp.model.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EventStatusTest {

    @Test
    public void toString_validStatus_returnsLowercase() {
        assertEquals("starting", EventStatus.STARTING.toString());
        assertEquals("ongoing", EventStatus.ONGOING.toString());
        assertEquals("closed", EventStatus.CLOSED.toString());
    }

    @Test
    public void fromString_validStatusUppercase_success() {
        assertEquals(EventStatus.STARTING, EventStatus.fromString("STARTING"));
        assertEquals(EventStatus.ONGOING, EventStatus.fromString("ONGOING"));
        assertEquals(EventStatus.CLOSED, EventStatus.fromString("CLOSED"));
    }

    @Test
    public void fromString_validStatusLowercase_success() {
        assertEquals(EventStatus.STARTING, EventStatus.fromString("starting"));
        assertEquals(EventStatus.ONGOING, EventStatus.fromString("ongoing"));
        assertEquals(EventStatus.CLOSED, EventStatus.fromString("closed"));
    }

    @Test
    public void fromString_validStatusMixedCase_success() {
        assertEquals(EventStatus.STARTING, EventStatus.fromString("StArTiNg"));
        assertEquals(EventStatus.ONGOING, EventStatus.fromString("OnGoInG"));
        assertEquals(EventStatus.CLOSED, EventStatus.fromString("ClOsEd"));
    }

    @Test
    public void fromString_invalidStatus_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> EventStatus.fromString("invalid"));
        assertThrows(IllegalArgumentException.class, () -> EventStatus.fromString(""));
        assertThrows(IllegalArgumentException.class, () -> EventStatus.fromString("PENDING"));
    }

    @Test
    public void fromString_nullStatus_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> EventStatus.fromString(null));
    }

    @Test
    public void isValidEventStatus_validStatus_returnsTrue() {
        assertTrue(EventStatus.isValidEventStatus("STARTING"));
        assertTrue(EventStatus.isValidEventStatus("starting"));
        assertTrue(EventStatus.isValidEventStatus("StArTiNg"));
        assertTrue(EventStatus.isValidEventStatus("ONGOING"));
        assertTrue(EventStatus.isValidEventStatus("ongoing"));
        assertTrue(EventStatus.isValidEventStatus("CLOSED"));
        assertTrue(EventStatus.isValidEventStatus("closed"));
    }

    @Test
    public void isValidEventStatus_invalidStatus_returnsFalse() {
        assertFalse(EventStatus.isValidEventStatus("invalid"));
        assertFalse(EventStatus.isValidEventStatus(""));
        assertFalse(EventStatus.isValidEventStatus("PENDING"));
        assertFalse(EventStatus.isValidEventStatus("ATTENDING"));
        assertFalse(EventStatus.isValidEventStatus(null));
    }

    @Test
    public void isValidEventStatus_whitespace_returnsFalse() {
        assertFalse(EventStatus.isValidEventStatus(" "));
        assertFalse(EventStatus.isValidEventStatus("  STARTING  "));
    }
}
