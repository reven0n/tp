package nusemp.model.participant;

import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalContacts.ALICE;
import static nusemp.testutil.TypicalEvents.MEETING_FILLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class ParticipantTest {

    @Test
    public void constructor_nullParameters_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new Participant(null, MEETING_FILLED, ParticipantStatus.AVAILABLE));
        assertThrows(NullPointerException.class, () -> new Participant(ALICE, MEETING_FILLED, null));
        assertThrows(NullPointerException.class, () ->
                new Participant(null, MEETING_FILLED, ParticipantStatus.UNAVAILABLE));
    }

    @Test
    public void equals() {
        Participant participant1 = new Participant(ALICE, MEETING_FILLED, ParticipantStatus.AVAILABLE);
        Participant participant2 = new Participant(ALICE, MEETING_FILLED, ParticipantStatus.AVAILABLE);
        Participant participant3 = new Participant(ALICE, MEETING_FILLED, ParticipantStatus.UNAVAILABLE);

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
