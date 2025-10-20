package nusemp.model.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import nusemp.model.AppData;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.contact.Address;
import nusemp.model.contact.Contact;
import nusemp.model.contact.Email;
import nusemp.model.contact.Name;
import nusemp.model.contact.Phone;
import nusemp.model.event.Event;
import nusemp.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AppData} with sample data.
 */
public class SampleDataUtil {
    public static final List<Event> EMPTY_EVENT_LIST = new ArrayList<>();
    public static Contact[] getSampleContacts() {
        return new Contact[] {
            new Contact(new Name("Alex Yeoh"), new Email("alexyeoh@example.com"), new Phone("87438807"),
                    new Address("Blk 30 Geylang Street 29, #06-40"),
                getTagSet("friends"), EMPTY_EVENT_LIST),
            new Contact(new Name("Bernice Yu"), new Email("berniceyu@example.com"), new Phone("99272758"),
                    new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                getTagSet("colleagues", "friends"), EMPTY_EVENT_LIST),
            new Contact(new Name("Charlotte Oliveiro"), new Email("charlotte@example.com"), new Phone("93210283"),
                    new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                getTagSet("neighbours"), EMPTY_EVENT_LIST),
            new Contact(new Name("David Li"), new Email("lidavid@example.com"), new Phone("91031282"),
                    new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                getTagSet("family"), EMPTY_EVENT_LIST),
            new Contact(new Name("Irfan Ibrahim"), new Email("irfan@example.com"), new Phone("92492021"),
                    new Address("Blk 47 Tampines Street 20, #17-35"),
                getTagSet("classmates"), EMPTY_EVENT_LIST),
            new Contact(new Name("Roy Balakrishnan"), new Email("royb@example.com"), new Phone("92624417"),
                    new Address("Blk 45 Aljunied Street 85, #11-31"),
                getTagSet("colleagues"), EMPTY_EVENT_LIST)
        };
    }

    public static ReadOnlyAppData getSampleAppData() {
        AppData appData = new AppData();
        for (Contact sampleContact : getSampleContacts()) {
            appData.addContact(sampleContact);
        }
        return appData;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
