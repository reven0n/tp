package nusemp.testutil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nusemp.model.event.Event;
import nusemp.model.event.EventStatus;
import nusemp.model.event.Participant;
import nusemp.model.fields.Address;
import nusemp.model.fields.Date;
import nusemp.model.fields.Name;
import nusemp.model.fields.Tag;
import nusemp.model.util.SampleDataUtil;

/**
 * A utility class to help with building Event objects.
 */
public class EventBuilder {

    public static final String DEFAULT_NAME = "Default Event";
    public static final String DEFAULT_DATE = "01-01-2025 10:00";
    public static final String DEFAULT_ADDRESS = "123 Default St";
    public static final EventStatus DEFAULT_STATUS = EventStatus.STARTING;

    private Name name;
    private Date date;
    private Address address;
    private EventStatus status;
    private List<Participant> participants;
    private Set<Tag> tags;

    /**
     * Creates an EventBuilder with the default details.
     */
    public EventBuilder() {
        name = new Name(DEFAULT_NAME);
        date = new Date(DEFAULT_DATE);
        address = new Address(DEFAULT_ADDRESS);
        status = DEFAULT_STATUS;
        participants = new ArrayList<>();
        tags = new HashSet<>();
    }

    /**
     * Initializes the EventBuilder with the data of {@code eventToCopy}.
     */
    public EventBuilder(Event eventToCopy) {
        name = eventToCopy.getName();
        date = eventToCopy.getDate();
        address = eventToCopy.getAddress();
        status = eventToCopy.getStatus();
        participants = new ArrayList<>(eventToCopy.getParticipants());
        tags = new HashSet<>(eventToCopy.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code Event} that we are building.
     */
    public EventBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@code Date} of the {@code Event} that we are building.
     */
    public EventBuilder withDate(String date) {
        this.date = new Date(date);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Event} that we are building.
     */
    public EventBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Removes the {@code Address} from the {@code Event} that we are building.
     */
    public EventBuilder withoutAddress() {
        this.address = Address.empty();
        return this;
    }

    /**
     * Sets the {@code Tags} of the {@code Event} that we are building.
     */
    public EventBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code EventStatus} of the {@code Event} that we are building.
     */
    public EventBuilder withStatus(EventStatus status) {
        this.status = status;
        return this;
    }

    /**
     * Sets the {@code EventStatus} of the {@code Event} that we are building.
     */
    public EventBuilder withStatus(String status) {
        this.status = EventStatus.fromString(status);
        return this;
    }

    /**
     * Replaces the participants of the {@code Event} that we are building.
     */
    public EventBuilder withParticipants(Participant... participants) {
        this.participants = new ArrayList<>(List.of(participants));
        return this;
    }

    /**
     * Replaces the participants of the {@code Event} that we are building.
     */
    public EventBuilder withParticipants(List<Participant> participants) {
        this.participants = new ArrayList<>(participants);
        return this;
    }

    public Event build() {
        return new Event(name, date, address, status, tags, participants);
    }
}
