package nusemp.model.event.exceptions;

/**
 * Signals that the provided contact status is invalid.
 */
public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(String status) {
        super(status + " is not a valid contact status");
    }
}
