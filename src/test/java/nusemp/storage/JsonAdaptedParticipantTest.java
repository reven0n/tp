package nusemp.storage;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class JsonAdaptedParticipantTest {
    private final String validEmail = "123@example.com";
    private final String validStatus = "attending";

    @Test
    public void constructor_validArgs_success() {
        JsonAdaptedParticipant participant = new JsonAdaptedParticipant(validEmail, validStatus);
        assertSame(validEmail, participant.getEmail());
        assertSame(validStatus, participant.getStatus());
    }
}
