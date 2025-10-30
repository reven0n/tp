package nusemp.model.event;

import java.util.List;
import java.util.function.Predicate;

import nusemp.commons.util.ToStringBuilder;

public class EventMatchesAnyPredicatePredicate implements Predicate<Event> {
    private final List<Predicate<Event>> predicates;

    public EventMatchesAnyPredicatePredicate(List<Predicate<Event>> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean test(Event event) {
        return predicates.stream()
                .anyMatch(predicate -> predicate.test(event));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventMatchesAnyPredicatePredicate)) {
            return false;
        }

        EventMatchesAnyPredicatePredicate otherEventMatchesAnyPredicatePredicate =
                (EventMatchesAnyPredicatePredicate) other;
        return predicates.equals(otherEventMatchesAnyPredicatePredicate.predicates);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicates", predicates).toString();
    }
}
