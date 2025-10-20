package nusemp.model.fields;

import static java.util.Objects.requireNonNull;
import static nusemp.commons.util.AppUtil.checkArgument;

/**
 * Represents an address.
 * Guarantees: immutable; is valid as declared in {@link #isValidAddress(String)}
 */
public class Address {

    public static final String MESSAGE_CONSTRAINTS = "Addresses can take any values, and it should not be blank";

    public final String value;

    /**
     * Constructs an {@code Address}.
     *
     * @param address A valid address. Can be an empty string, which indicates no address.
     */
    public Address(String address) {
        requireNonNull(address);
        checkArgument(isValidAddress(address), MESSAGE_CONSTRAINTS);
        value = address;
    }

    /**
     * Returns an empty address.
     */
    public static Address empty() {
        return new Address("");
    }

    /**
     * Returns true if a given string is a valid email.
     * Empty string is also considered valid, indicating no address.
     */
    public static boolean isValidAddress(String test) {
        return test.isEmpty() || !test.isBlank();
    }

    /**
     * Returns true if there is no address.
     */
    public boolean isEmpty() {
        return value.isEmpty();
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
        return value.equals(otherAddress.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
