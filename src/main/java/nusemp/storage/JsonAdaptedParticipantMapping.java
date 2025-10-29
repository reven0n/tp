package nusemp.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson-friendly version of participant-event mapping.
 */
class JsonAdaptedParticipantMapping {
    private final String contactEmail;
    private final String eventName;
    private final String status;

    @JsonCreator
    public JsonAdaptedParticipantMapping(
            @JsonProperty("contactEmail") String contactEmail,
            @JsonProperty("eventName") String eventName,
            @JsonProperty("status") String status) {
        this.contactEmail = contactEmail;
        this.eventName = eventName;
        this.status = status;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getEventName() {
        return eventName;
    }

    public String getStatus() {
        return status;
    }
}
