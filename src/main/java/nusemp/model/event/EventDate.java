package nusemp.model.event;

import static java.util.Objects.requireNonNull;
import static nusemp.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents an Event's date and time in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEventDate(String)}
 */
public class EventDate {

    public static final String MESSAGE_CONSTRAINTS =
            "Event date and time should follow this format: DD-MM-YYYY HH:mm (24-hour format)";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public final LocalDateTime value;

    /**
     * Constructs an {@code EventDate}.
     *
     * @param date A valid date string in DD-MM-YYYY HH:mm format.
     */
    public EventDate(String date) {
        requireNonNull(date);
        String trimmedDate = date.trim();
        checkArgument(isValidEventDate(trimmedDate), MESSAGE_CONSTRAINTS);
        value = LocalDateTime.parse(trimmedDate, FORMATTER);
    }

    /**
     * Constructs an {@code EventDate} from a LocalDateTime object.
     *
     * @param dateTime A LocalDateTime object.
     */
    public EventDate(LocalDateTime dateTime) {
        requireNonNull(dateTime);
        value = dateTime;
    }

    /**
     * Returns true if a given string is a valid event date.
     */
    public static boolean isValidEventDate(String test) {
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
        if (!(other instanceof EventDate)) {
            return false;
        }

        EventDate otherEventDate = (EventDate) other;
        return value.equals(otherEventDate.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
