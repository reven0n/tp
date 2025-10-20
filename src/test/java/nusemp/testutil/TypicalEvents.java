package nusemp.testutil;

import static nusemp.testutil.TypicalContacts.ALICE;
import static nusemp.testutil.TypicalContacts.BOB;
import static nusemp.testutil.TypicalContacts.CARL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nusemp.model.AppData;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.fields.Date;
import nusemp.model.fields.Name;

/**
 * A utility class containing a list of {@code Event} objects to be used in tests.
 */
public class TypicalEvents {
    public static final Event MEETING_EMPTY = new Event(
            new Name("MEETING"), new Date("01-10-2025 14:00"));
    public static final Event MEETING_FILLED = new Event(
            new Name("MEETING"), new Date("01-10-2025 14:00"), TypicalContacts.getTypicalContacts());
    public static final Event CONFERENCE_EMPTY = new Event(
            new Name("CONFERENCE"), new Date("29-02-2024 09:00"));
    public static final Event CONFERENCE_FILLED = new Event(
            new Name("CONFERENCE"), new Date("29-02-2024 09:00"), TypicalContacts.getTypicalContacts());
    public static final Event WORKSHOP_EMPTY = new Event(
            new Name("WORKSHOP"), new Date("01-10-2025 14:00"));
    public static final Event WORKSHOP_FILLED = new Event(
            new Name("WORKSHOP"), new Date("01-10-2025 14:00"), TypicalContacts.getTypicalContacts());
    public static final Event PARTY_EMPTY = new Event(
            new Name("PARTY"), new Date("31-12-2024 20:00"));
    public static final Event PARTY_HALF_FILLED = new Event(
            new Name("PARTY"), new Date("31-12-2024 20:00"),
            new ArrayList<>(Arrays.asList(ALICE, BOB, CARL)));

    public static AppData getTypicalAppDataWithEvents() {
        AppData ab = new AppData();
        for (Contact contact : TypicalContacts.getTypicalContacts()) {
            ab.addContact(contact);
        }
        for (Event event : getTypicalEvents()) {
            ab.addEvent(event);
        }

        return ab;
    }

    public static List<Event> getTypicalEvents() {
        return new ArrayList<>(Arrays.asList(MEETING_EMPTY, CONFERENCE_EMPTY,
                WORKSHOP_FILLED, PARTY_HALF_FILLED));
    }
}
