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
     * Constructs a {@code JsonAdaptedParticipantStatus} with the given key and value.
     * @param key
     * @param value
     */
    @JsonCreator
    public JsonAdaptedParticipantStatus(@JsonProperty("key") String key, @JsonProperty("value") String value) {
        this.participantEmail = key;
        this.participantStatus = value;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public String getParticipantStatus() {
        return participantStatus;
    }
}
