package nusemp.model.event;

import nusemp.model.event.exceptions.InvalidStatusException;

/**
 * Represents the status of a participant in an event.
 */
public enum Status {
    CANCELLED, ATTENDING;

    public static final String MESSAGE_CONSTRAINTS =
            "Status should be either 'attending' or 'cancelled' (case insensitive)";

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    /**
     * Checks if the given status string is a valid Status enum value.
     */
    public static Status convertStringToStatus(String statusStr) throws InvalidStatusException {
        for (Status status : Status.values()) {
            if (status.name().equalsIgnoreCase(statusStr)) {
                return status;
            }
        }
        throw new InvalidStatusException(statusStr);
    }
}
