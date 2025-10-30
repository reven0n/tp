package nusemp.model.event;

import java.util.List;
import java.util.function.Predicate;

/**
 * Tests that a {@code Event}'s status matches the given status.
 */
public class EventStatusPredicate implements Predicate<Event> {
    private final List<String> statuses;

    /**
     * Creates an EventStatusPredicate with the given status.
     */
    public EventStatusPredicate(List<String> statuses) {
        boolean isInvalidList = statuses.stream().anyMatch(status -> !EventStatus.isValidEventStatus(status));
        if (isInvalidList) {
            throw new IllegalArgumentException(EventStatus.MESSAGE_CONSTRAINTS);
        }
        this.statuses = statuses;
    }

    @Override
    public boolean test(Event event) {
        return statuses.stream().anyMatch(status -> event.getStatus().toString().equalsIgnoreCase(status));
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
        return statuses.equals(statuses);
    }
}
