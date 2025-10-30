package nusemp.model.event;

import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import nusemp.commons.util.ToStringBuilder;
import nusemp.model.fields.Address;
import nusemp.model.fields.Date;
import nusemp.model.fields.Name;
import nusemp.model.fields.Tag;

/**
 * Represents an Event.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Event {

    // Identity fields
    private final Name name;
    private final Date date;

    // Data fields
    private final Address address;
    private final EventStatus status;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * This value is used to invalidate the event,
     * i.e. force an update to the observable event list for this event.
     * <p>
     * Note: This is a workaround for contact changes not being reflected in the event list.
     */
    private final boolean invalidationToggle;

    /**
     * Every field must be present and not null. {@code Address.empty()} can be used to represent absence of an address.
     */
    public Event(Name name, Date date, Address address, EventStatus status, Set<Tag> tags) {
        this(name, date, address, status, tags, false);
    }

    /**
     * Convenience constructor without participants or tags, with default status STARTING.
     */
    public Event(Name name, Date date, Address address) {
        this(name, date, address, EventStatus.STARTING, new HashSet<>());
    }

    private Event(Name name, Date date, Address address, EventStatus status, Set<Tag> tags,
            boolean invalidationToggle) {
        requireAllNonNull(name, date, address, status, tags, invalidationToggle);
        this.name = name;
        this.date = date;
        this.address = address;
        this.status = status;
        this.tags.addAll(tags);
        this.invalidationToggle = invalidationToggle;
    }

    public Name getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public Address getAddress() {
        return address;
    }

    public boolean hasAddress() {
        return !address.isEmpty();
    }

    public EventStatus getStatus() {
        return status;
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
     * Returns an EventKey that uniquely identifies this event.
     */
    public EventKey getPrimaryKey() {
        return new EventKey(name.value);
    }

    /**
     * Returns true if both events have the same primary key.
     * This defines a weaker notion of equality between two events.
     */
    public boolean isSameEvent(Event otherEvent) {
        if (otherEvent == this) {
            return true;
        }

        return otherEvent != null
                && otherEvent.getPrimaryKey().equals(this.getPrimaryKey());
    }

    /**
     * Returns a new event with the exact same fields.
     * However, the new event would not be considered `equal` to the original event.
     * <p>
     * Note: This is a workaround for contact changes not being reflected in the event list.
     */
    public Event getInvalidatedEvent() {
        return new Event(name, date, address, status, tags, !invalidationToggle);
    }

    /**
     * Returns true if both events have the same identity and data fields.
     */
    public boolean hasSameFields(Event other) {
        return name.equals(other.name)
                && date.equals(other.date)
                && address.equals(other.address)
                && status.equals(other.status)
                && tags.equals(other.tags);
    }

    /**
     * Returns true if both events have the same identity, data fields and invalidation toggle.
     * This defines the strongest notion of equality between two events.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Event otherEvent)) {
            return false;
        }

        return name.equals(otherEvent.name)
                && date.equals(otherEvent.date)
                && address.equals(otherEvent.address)
                && status.equals(otherEvent.status)
                && tags.equals(otherEvent.tags)
                && invalidationToggle == otherEvent.invalidationToggle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, address, status, tags, invalidationToggle);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("date", date)
                .add("address", address)
                .add("status", status)
                .add("tags", tags)
                .toString();
    }
}
