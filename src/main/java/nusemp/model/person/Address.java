package nusemp.model.person;

import static nusemp.commons.util.AppUtil.checkArgument;

import java.util.Objects;

/**
 * Represents a Person's address in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidAddress(String)}
 */
public class Address {

    public static final String MESSAGE_CONSTRAINTS = "Addresses can take any values, and it should not be blank";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String value;

    /**
     * Constructs an {@code Address}.
     *
     * @param address A valid address. Can be null, which indicates no address.
     */
    public Address(String address) {
        if (address != null) {
            checkArgument(isValidAddress(address), MESSAGE_CONSTRAINTS);
        }
        value = address;
    }

    /**
     * Returns true if a given string is a valid email.
     */
    public static boolean isValidAddress(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if there is no address.
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
        if (!(other instanceof Address)) {
            return false;
        }

        Address otherAddress = (Address) other;
        return Objects.equals(value, otherAddress.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
