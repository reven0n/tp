package nusemp.model.event;

import java.util.Arrays;

/**
 * Represents the status of an Event.
 */
public enum EventStatus {
    STARTING,
    ONGOING,
    CLOSED;

    public static final String MESSAGE_CONSTRAINTS =
            "Event status must be one of: STARTING, ONGOING, CLOSED (case-insensitive)";

    /**
     * Returns the lowercase string representation of the status.
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }

    /**
     * Converts a string to an EventStatus enum value.
     *
     * @param statusString The string to convert.
     * @return The corresponding EventStatus.
     * @throws IllegalArgumentException if the string is not a valid status.
     */
    public static EventStatus fromString(String statusString) {
        if (statusString == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        try {
            return EventStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
    }

    /**
     * Validates if the given string is a valid event status.
     *
     * @param test The string to test.
     * @return True if the string is a valid event status, false otherwise.
     */
    public static boolean isValidEventStatus(String test) {
        if (test == null) {
            return false;
        }
        return Arrays.stream(EventStatus.values())
                .anyMatch(status -> status.name().equalsIgnoreCase(test));
    }
}
