package nusemp.model.event;

import java.util.function.Predicate;

/**
 * Tests that a {@code Event}'s status matches the given status.
 */
public class EventStatusPredicate implements Predicate<Event> {
    private final String status;

    /**
     * Creates an EventStatusPredicate with the given status.
     */
    public EventStatusPredicate(String status) {
        if (!EventStatus.isValidEventStatus(status)) {
            throw new IllegalArgumentException(EventStatus.MESSAGE_CONSTRAINTS);
        }
        this.status = status;
    }

    @Override
    public boolean test(Event event) {
        return event.getStatus().name().equalsIgnoreCase(status);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventStatusPredicate)) {
            return false;
        }

        EventStatusPredicate otherPredicate =
                (EventStatusPredicate) other;
        return status.equalsIgnoreCase(otherPredicate.status);
    }
}
