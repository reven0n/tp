package nusemp.model.event;


/**
 * Represents the status of a participant in an event.
 */
public enum ParticipantStatus {
    CANCELLED, ATTENDING, UNKNOWN;

    public static final String MESSAGE_CONSTRAINTS =
            "Status should be either 'attending' or 'cancelled' (case insensitive)";

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    /**
     * Checks if the given status string is a valid ParticipantStatus enum value.
     */
    public static ParticipantStatus fromString(String statusStr) {
        for (ParticipantStatus status : ParticipantStatus.values()) {
            if (status.name().equalsIgnoreCase(statusStr)) {
                return status;
            }
        }

        return ParticipantStatus.UNKNOWN;
    }

    /**
     * Checks if the given status string is not a valid Status enum value.
     */
    public static boolean isNotValidStatus(String test) {
        for (ParticipantStatus status : ParticipantStatus.values()) {
            if (status.name().equalsIgnoreCase(test)) {
                return false;
            }
        }
        return true;
    }
}
