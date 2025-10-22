package nusemp.model.event;

/**
 * Represents the status of a participant in an event.
 */
public enum Status {
    CANCELLED, ATTENDING;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
