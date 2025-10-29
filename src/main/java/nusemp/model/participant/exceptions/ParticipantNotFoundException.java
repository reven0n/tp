package nusemp.model.participant.exceptions;

/**
 * Signals that the specified participant could not be found in the event.
 */
public class ParticipantNotFoundException extends RuntimeException {
    public ParticipantNotFoundException() {
        super("Participant not found");
    }
}
