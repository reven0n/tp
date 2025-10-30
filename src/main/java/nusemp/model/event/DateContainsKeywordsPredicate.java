package nusemp.model.event;

import java.util.function.Predicate;

import nusemp.commons.util.ToStringBuilder;
import nusemp.model.fields.Date;

/**
 * Tests that a {@code Event}'s {@code Date} matches any of the date given.
 */
public class DateContainsKeywordsPredicate implements Predicate<Event> {
    private final Date date;

    public DateContainsKeywordsPredicate(Date dates) {
        this.date = dates;
    }

    @Override
    public boolean test(Event event) {
        return date.equals(event.getDate());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DateContainsKeywordsPredicate)) {
            return false;
        }

        DateContainsKeywordsPredicate otherDateContainsKeywordsPredicate =
                (DateContainsKeywordsPredicate) other;
        return date.equals(otherDateContainsKeywordsPredicate.date);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("date", date).toString();
    }
}
