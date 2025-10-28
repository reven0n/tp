package nusemp.model.event;

import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalContacts.ALICE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ParticipantTest {
    @Test
    public void constructor_withoutStatus_success() {
        Participant participant = new Participant(ALICE);
        assertEquals(ParticipantStatus.AVAILABLE, participant.getStatus());
        assertEquals(ALICE, participant.getContact());
    }

    @Test
    public void constructor_nullParameters_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Participant(null, ParticipantStatus.AVAILABLE));
        assertThrows(NullPointerException.class, () -> new Participant(ALICE, null));
        assertThrows(NullPointerException.class, () -> new Participant(null));
    }

    @Test
    public void equalsContact_sameContact_returnsTrue() {
        Participant participant = new Participant(ALICE, ParticipantStatus.AVAILABLE);
        assertTrue(participant.equalsContact(ALICE));
    }

    @Test
    public void equalsContact_differentContact_returnsFalse() {
        Participant participant = new Participant(ALICE, ParticipantStatus.AVAILABLE);
        assertFalse(participant.equalsContact(null));
    }

    @Test
    public void toStringMethod() {
        Participant participant = new Participant(ALICE, ParticipantStatus.UNAVAILABLE);
        String expectedString = "Contact: " + ALICE.toString() + ", Status: unavailable";
        assertEquals(expectedString, participant.toString());
    }

    @Test
    public void equals() {
        Participant participant1 = new Participant(ALICE, ParticipantStatus.AVAILABLE);
        Participant participant2 = new Participant(ALICE, ParticipantStatus.AVAILABLE);
        Participant participant3 = new Participant(ALICE, ParticipantStatus.UNAVAILABLE);

        // same object -> returns true
        assertEquals(participant1, participant1);

        // same values -> returns true
        assertEquals(participant1, participant2);

        // different status -> returns false
        assertNotEquals(participant1, participant3);

        // different type -> returns false
        assertNotEquals("some string", participant1);

        // null -> returns false
        assertNotEquals(null, participant1);
    }
}
