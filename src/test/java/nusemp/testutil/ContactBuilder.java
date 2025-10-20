package nusemp.testutil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nusemp.model.contact.Address;
import nusemp.model.contact.Contact;
import nusemp.model.contact.Email;
import nusemp.model.contact.Name;
import nusemp.model.contact.Phone;
import nusemp.model.event.Event;
import nusemp.model.tag.Tag;
import nusemp.model.util.SampleDataUtil;

/**
 * A utility class to help with building Contact objects.
 */
public class ContactBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Set<Tag> tags;
    private List<Event> events;

    /**
     * Creates a {@code ContactBuilder} with the default details.
     */
    public ContactBuilder() {
        name = new Name(DEFAULT_NAME);
        email = new Email(DEFAULT_EMAIL);
        phone = new Phone(DEFAULT_PHONE);
        address = new Address(DEFAULT_ADDRESS);
        tags = new HashSet<>();
        events = new ArrayList<>();
    }

    /**
     * Initializes the ContactBuilder with the data of {@code contactToCopy}.
     */
    public ContactBuilder(Contact contactToCopy) {
        name = contactToCopy.getName();
        email = contactToCopy.getEmail();
        phone = contactToCopy.getPhone();
        address = contactToCopy.getAddress();
        tags = new HashSet<>(contactToCopy.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code Contact} that we are building.
     */
    public ContactBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Contact} that we are building.
     */
    public ContactBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Contact} that we are building.
     */
    public ContactBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Contact} that we are building.
     */
    public ContactBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Removes the {@code Phone} from the {@code Contact} that we are building.
     */
    public ContactBuilder withoutPhone() {
        this.phone = Phone.empty();
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Contact} that we are building.
     */
    public ContactBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Removes the {@code Address} from the {@code Contact} that we are building.
     */
    public ContactBuilder withoutAddress() {
        this.address = Address.empty();
        return this;
    }

    /**
     * Sets the {@code List<Event>} of the {@code Contact} that we are building.
     */
    public ContactBuilder withEvents(List<Event> events) {
        this.events = events;
        return this;
    }

    /**
     * Adds an {@code Event} to the {@code Contact} that we are building.
     */
    public ContactBuilder addEvent(Event event) {
        this.events.add(event);
        return this;
    }

    /**
     * Clears the {@code List<Event>} of the {@code Contact} that we are building.
     */
    public ContactBuilder clearEvents() {
        this.events.clear();
        return this;
    }

    public Contact build() {
        return new Contact(name, email, phone, address, tags, events);
    }

}
