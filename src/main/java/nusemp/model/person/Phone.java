package nusemp.model.person;

import static nusemp.commons.util.AppUtil.checkArgument;

import java.util.Objects;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers should only contain numbers, and it should be at least 3 digits long";
    public static final String VALIDATION_REGEX = "\\d{3,}";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number. Can be null, which indicates no phone number.
     */
    public Phone(String phone) {
        if (phone != null) {
            checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        }
        value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if there is no phone number.
     */
    public boolean isEmpty() {
        return value == null;
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
        if (!(other instanceof Phone)) {
            return false;
        }

        Phone otherPhone = (Phone) other;
        return Objects.equals(value, otherPhone.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
