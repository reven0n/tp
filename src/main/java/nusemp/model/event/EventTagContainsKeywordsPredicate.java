package nusemp.model.event;

import java.util.List;
import java.util.function.Predicate;

import nusemp.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Event}'s {@code Tag} matches any of the keywords given.
 */
public class EventTagContainsKeywordsPredicate implements Predicate<Event> {
    private final List<String> keywords;

    public EventTagContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Event event) {
        return keywords.stream()
                .anyMatch(keyword -> event.getTags().stream()
                        .anyMatch(tag -> tag.tagName.toLowerCase().contains(keyword.toLowerCase())));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventTagContainsKeywordsPredicate)) {
            return false;
        }

        EventTagContainsKeywordsPredicate otherEventTagContainsKeywordsPredicate =
                (EventTagContainsKeywordsPredicate) other;
        return keywords.equals(otherEventTagContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
