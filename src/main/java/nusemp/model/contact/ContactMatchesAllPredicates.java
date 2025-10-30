package nusemp.model.contact;

import java.util.List;
import java.util.function.Predicate;

import nusemp.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Contact} matches all of the predicates given.
 * This combines multiple predicates with AND logic.
 */
public class ContactMatchesAllPredicates implements Predicate<Contact> {
    private final List<Predicate<Contact>> predicates;

    public ContactMatchesAllPredicates(List<Predicate<Contact>> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean test(Contact contact) {
        return predicates.stream()
                .allMatch(predicate -> predicate.test(contact));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ContactMatchesAllPredicates)) {
            return false;
        }

        ContactMatchesAllPredicates otherContactMatchesAllPredicates =
                (ContactMatchesAllPredicates) other;
        return predicates.equals(otherContactMatchesAllPredicates.predicates);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicates", predicates).toString();
    }
}
