package nusemp.model.event;

import java.util.List;
import java.util.function.Predicate;

import nusemp.commons.util.ToStringBuilder;

/**
 * Tests that an {@code Event} matches any of the given predicates.
 */
public class EventMatchesAllPredicates implements Predicate<Event> {
    private final List<Predicate<Event>> predicates;

    public EventMatchesAllPredicates(List<Predicate<Event>> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean test(Event event) {
        return predicates.stream()
                .allMatch(predicate -> predicate.test(event));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventMatchesAllPredicates)) {
            return false;
        }

        EventMatchesAllPredicates otherEventMatchesAnyPredicatePredicate =
                (EventMatchesAllPredicates) other;
        return predicates.equals(otherEventMatchesAnyPredicatePredicate.predicates);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicates", predicates).toString();
    }
}
