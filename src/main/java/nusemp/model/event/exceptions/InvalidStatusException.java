package nusemp.model.event.exceptions;

/**
 * Signals that the provided status is invalid.
 */
public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(String status) {
        super(status + " is not a valid rsvp status");
    }
}
