package nusemp.model.event;

import static java.util.Objects.requireNonNull;
import static nusemp.commons.util.AppUtil.checkArgument;

/**
 * Represents an Event's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEventName(String)}
 */
public class EventName {

    public static final String MESSAGE_CONSTRAINTS = "Event names should not be blank";

    public final String value;

    /**
     * Constructs an {@code EventName}.
     *
     * @param name A valid event name.
     */
    public EventName(String name) {
        requireNonNull(name);
        String trimmedName = name.trim();
        checkArgument(isValidEventName(trimmedName), MESSAGE_CONSTRAINTS);
        value = trimmedName;
    }

    /**
     * Returns true if a given string is a valid event name.
     */
    public static boolean isValidEventName(String test) {
        return !test.isBlank();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventName)) {
            return false;
        }

        EventName otherEventName = (EventName) other;
        return value.equals(otherEventName.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
