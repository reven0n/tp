package nusemp.model.event;

import java.util.List;
import java.util.function.Predicate;

import nusemp.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Event}'s {@code Address} matches any of the keywords given.
 */
public class EventAddressContainsKeywordsPredicate implements Predicate<Event> {
    private final List<String> keywords;

    public EventAddressContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Event event) {
        return keywords.stream()
                .anyMatch(keyword -> event.getAddress().value.toLowerCase().contains(keyword.toLowerCase()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventAddressContainsKeywordsPredicate)) {
            return false;
        }

        EventAddressContainsKeywordsPredicate otherEventAddressContainsKeywordsPredicate =
                (EventAddressContainsKeywordsPredicate) other;
        return keywords.equals(otherEventAddressContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
