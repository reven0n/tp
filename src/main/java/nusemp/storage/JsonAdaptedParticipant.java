package nusemp.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson-friendly DTO for a participant status entry.
 */
public class JsonAdaptedParticipant {

    private final String email;
    private final String status;

    /**
     * Constructs a {@code JsonAdaptedParticipant} with the given email and status.
     * @param email Email of the participant.
     * @param status Status of the participant for the event.
     */
    @JsonCreator
    public JsonAdaptedParticipant(@JsonProperty("email") String email, @JsonProperty("status") String status) {
        this.email = email;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }
}
