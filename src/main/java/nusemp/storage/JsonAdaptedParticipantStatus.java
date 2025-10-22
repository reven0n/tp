package nusemp.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson-friendly DTO for a participant status entry (serialized as { "key": ..., "value": ... }).
 */
public class JsonAdaptedParticipantStatus {

    private final String participantEmail;
    private final String participantStatus;

    /**
     * Constructs a {@code JsonAdaptedParticipantStatus} with the given email and status.
     * @param email Email of the participant.
     * @param status Status of the participant for the event.
     */
    @JsonCreator
    public JsonAdaptedParticipantStatus(@JsonProperty("email") String email, @JsonProperty("status") String status) {
        this.participantEmail = email;
        this.participantStatus = status;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public String getParticipantStatus() {
        return participantStatus;
    }
}
