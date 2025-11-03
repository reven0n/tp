package nusemp.model.fields;

import static java.util.Objects.requireNonNull;
import static nusemp.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents a date and time.
 * Guarantees: immutable; is valid as declared in {@link #isValidDate(String)}
 */
public class Date {

    public static final String MESSAGE_CONSTRAINTS =
            "Date and time should follow this format: DD-MM-YYYY HH:mm (24-hour format)";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    public final LocalDateTime value;

    /**
     * Constructs an {@code Date}.
     *
     * @param date A valid date string in DD-MM-YYYY HH:mm format.
     */
    public Date(String date) {
        requireNonNull(date);
        String trimmedDate = date.trim();
        checkArgument(isValidDate(trimmedDate), MESSAGE_CONSTRAINTS);
        value = LocalDateTime.parse(trimmedDate, FORMATTER);
    }

    /**
     * Constructs an {@code Date} from a LocalDateTime object.
     *
     * @param dateTime A LocalDateTime object.
     */
    public Date(LocalDateTime dateTime) {
        requireNonNull(dateTime);
        value = dateTime;
    }

    /**
     * Returns true if a given string is a valid date.
     */
    public static boolean isValidDate(String test) {
        if (test == null || test.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDateTime.parse(test.trim(), FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns the formatted date string.
     */
    public String getFormattedDate() {
        return value.format(FORMATTER);
    }

    @Override
    public String toString() {
        return getFormattedDate();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Date)) {
            return false;
        }

        Date otherDate = (Date) other;
        return value.equals(otherDate.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
