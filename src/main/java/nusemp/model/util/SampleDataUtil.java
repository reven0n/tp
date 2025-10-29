package nusemp.model.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import nusemp.model.AppData;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.event.EventStatus;
import nusemp.model.fields.Address;
import nusemp.model.fields.Date;
import nusemp.model.fields.Email;
import nusemp.model.fields.Name;
import nusemp.model.fields.Phone;
import nusemp.model.fields.Tag;
import nusemp.model.participant.ParticipantStatus;

/**
 * Contains utility methods for populating {@code AppData} with sample data.
 */
public class SampleDataUtil {
    public static final List<Event> EMPTY_EVENT_LIST = new ArrayList<>();
    public static Contact[] getSampleContacts() {
        return new Contact[] {
            new Contact(new Name("Alex Yeoh"), new Email("alexyeoh@example.com"), new Phone("87438807"),
                    new Address("Blk 30 Geylang Street 29, #06-40"),
                getTagSet("friends")),
            new Contact(new Name("Bernice Yu"), new Email("berniceyu@example.com"), new Phone("99272758"),
                    new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                getTagSet("colleagues", "friends")),
            new Contact(new Name("Charlotte Oliveiro"), new Email("charlotte@example.com"), new Phone("93210283"),
                    new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                getTagSet("neighbours")),
            new Contact(new Name("David Li"), new Email("lidavid@example.com"), new Phone("91031282"),
                    new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                getTagSet("family")),
            new Contact(new Name("Irfan Ibrahim"), new Email("irfan@example.com"), new Phone("92492021"),
                    new Address("Blk 47 Tampines Street 20, #17-35"),
                getTagSet("classmates")),
            new Contact(new Name("Roy Balakrishnan"), new Email("royb@example.com"), new Phone("92624417"),
                    new Address("Blk 45 Aljunied Street 85, #11-31"),
                getTagSet("colleagues"))
        };
    }

    public static Event[] getSampleEvents() {
        return new Event[] {
            new Event(new Name("Project Meeting"), new Date("01-06-2025 13:00"),
                    new Address("NUS School of Computing"), EventStatus.ONGOING,
                    getTagSet("work")),
            new Event(new Name("Birthday Party"), new Date("29-10-2025 12:00"),
                    new Address("123 Party Ave")),
            new Event(new Name("Conference"), new Date("30-10-2025 10:00"),
                    new Address("Convention Center"), EventStatus.CLOSED,
                    getTagSet("NUS"))
        };
    }

    private static void initializeSampleParticipants(AppData appData) {
        List<Contact> contacts = appData.getContactList();
        List<Event> events = appData.getEventList();

        // Alex Yeoh in Project Meeting
        appData.addParticipant(contacts.get(0), events.get(0), ParticipantStatus.AVAILABLE);

        // Bernice Yu in Project Meeting and Birthday Party
        appData.addParticipant(contacts.get(1), events.get(0), ParticipantStatus.AVAILABLE);
        appData.addParticipant(contacts.get(1), events.get(1), ParticipantStatus.AVAILABLE);
    }

    public static ReadOnlyAppData getSampleAppData() {
        AppData appData = new AppData();
        for (Contact sampleContact : getSampleContacts()) {
            appData.addContact(sampleContact);
        }

        for (Event sampleEvent : getSampleEvents()) {
            appData.addEvent(sampleEvent);
        }

        initializeSampleParticipants(appData);
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
