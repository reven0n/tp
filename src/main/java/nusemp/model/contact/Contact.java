package nusemp.model.contact;

import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import nusemp.commons.util.ToStringBuilder;
import nusemp.model.fields.Address;
import nusemp.model.fields.Email;
import nusemp.model.fields.Name;
import nusemp.model.fields.Phone;
import nusemp.model.fields.Tag;

/**
 * Represents a Contact.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Contact {

    // Identity fields
    private final Name name;
    private final Email email;

    // Data fields
    private final Phone phone;
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * This value is used to invalidate the contact,
     * i.e. force an update to the observable contact list for this contact.
     * <p>
     * Note: This is a workaround for event changes not being reflected in the contact list.
     */
    private final boolean invalidationToggle;

    /**
     * Every field must be present and not null.
     * {@code Phone.empty()} or {@code Address.empty()} can be used to represent absence of a phone number or address
     * respectively.
     */
    public Contact(Name name, Email email, Phone phone, Address address, Set<Tag> tags) {
        this(name, email, phone, address, tags, false);
    }

    private Contact(Name name, Email email, Phone phone, Address address, Set<Tag> tags, boolean invalidationToggle) {
        requireAllNonNull(name, email, phone, address, tags, invalidationToggle);
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.tags.addAll(tags);
        this.invalidationToggle = invalidationToggle;
    }

    public Name getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public Phone getPhone() {
        return phone;
    }

    public boolean hasPhone() {
        return !phone.isEmpty();
    }

    public Address getAddress() {
        return address;
    }

    public boolean hasAddress() {
        return !address.isEmpty();
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public boolean hasTags() {
        return !tags.isEmpty();
    }

    /**
     * Returns a ContactKey uniquely identifies this contact.
     */
    public ContactKey getPrimaryKey() {
        return new ContactKey(email.value.toLowerCase());
    }

    /**
     * Returns true if both contacts have the same primary key.
     * This defines a weaker notion of equality between two contacts.
     */
    public boolean isSameContact(Contact otherContact) {
        if (otherContact == this) {
            return true;
        }

        return otherContact != null
                && otherContact.getPrimaryKey().equals(this.getPrimaryKey());
    }

    /**
     * Returns a new contact with the exact same fields.
     * However, the new contact would not be considered `equal` to the original contact.
     * <p>
     * Note: This is a workaround for event changes not being reflected in the contact list.
     */
    public Contact getInvalidatedContact() {
        return new Contact(name, email, phone, address, tags, !invalidationToggle);
    }

    /**
     * Returns true if both contacts have the same identity and data fields.
     */
    public boolean hasSameFields(Contact other) {
        return name.equals(other.name)
                && email.equals(other.email)
                && phone.equals(other.phone)
                && address.equals(other.address)
                && tags.equals(other.tags);
    }

    /**
     * Returns true if both contacts have the same identity, data fields, and invalidation toggle.
     * This defines the strongest notion of equality between two contacts.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Contact otherContact)) {
            return false;
        }

        return name.equals(otherContact.name)
                && email.equals(otherContact.email)
                && phone.equals(otherContact.phone)
                && address.equals(otherContact.address)
                && tags.equals(otherContact.tags)
                && invalidationToggle == otherContact.invalidationToggle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, phone, address, tags, invalidationToggle);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("email", email)
                .add("phone", phone)
                .add("address", address)
                .add("tags", tags)
                .toString();
    }
}
