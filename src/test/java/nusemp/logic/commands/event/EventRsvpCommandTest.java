package nusemp.logic.commands.event;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.model.event.ParticipantStatus;

class EventRsvpCommandTest {
    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EventRsvpCommand(Index.fromOneBased(1), null, null));
        assertThrows(NullPointerException.class, () -> new EventRsvpCommand(null, Index.fromOneBased(1), null));
        assertThrows(NullPointerException.class, () -> new EventRsvpCommand(null, null, ParticipantStatus.ATTENDING));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        EventRsvpCommand command = new EventRsvpCommand(Index.fromOneBased(1),
                Index.fromOneBased(1), ParticipantStatus.ATTENDING);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }
}
