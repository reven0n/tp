package nusemp.model.event;


/**
 * Represents the status of a participant in an event.
 */
public enum Status {
    CANCELLED, ATTENDING, UNKNOWN;

    public static final String MESSAGE_CONSTRAINTS =
            "Status should be either 'attending' or 'cancelled' (case insensitive)";

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    /**
     * Checks if the given status string is a valid Status enum value.
     */
    public static Status fromString(String statusStr) {
        for (Status status : Status.values()) {
            if (status.name().equalsIgnoreCase(statusStr)) {
                return status;
            }
        }

        return Status.UNKNOWN;
    }

    /**
     * Checks if the given status string is a valid Status enum value.
     */
    public static boolean isValidStatus(String test) {
        for (Status status : Status.values()) {
            if (status.name().equalsIgnoreCase(test)) {
                return true;
            }
        }
        return false;
    }
}
