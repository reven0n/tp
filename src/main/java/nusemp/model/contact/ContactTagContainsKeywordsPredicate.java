package nusemp.model.contact;

import java.util.List;
import java.util.function.Predicate;

import nusemp.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Contact}'s {@code Tag} matches any of the keywords given.
 */
public class ContactTagContainsKeywordsPredicate implements Predicate<Contact> {
    private final List<String> keywords;

    public ContactTagContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Contact contact) {
        return keywords.stream()
                .anyMatch(keyword -> contact.getTags().stream()
                        .anyMatch(tag -> tag.tagName.toLowerCase().contains(keyword.toLowerCase())));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ContactTagContainsKeywordsPredicate)) {
            return false;
        }

        ContactTagContainsKeywordsPredicate otherTagContainsKeywordsPredicate =
        (ContactTagContainsKeywordsPredicate) other;
        return keywords.equals(otherTagContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
