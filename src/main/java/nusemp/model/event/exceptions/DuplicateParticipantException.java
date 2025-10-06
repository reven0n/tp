package nusemp.model.event.exceptions;

/**
 * Signals that the operation will result in duplicate participants in an event.
 */
public class DuplicateParticipantException extends RuntimeException {
    public DuplicateParticipantException() {
        super("Operation would result in duplicate participants");
    }
}
