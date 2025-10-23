package nusemp.testutil;

import static nusemp.testutil.EventUtil.convertToContactStatusList;
import static nusemp.testutil.TypicalContacts.ALICE;
import static nusemp.testutil.TypicalContacts.BENSON;
import static nusemp.testutil.TypicalContacts.BOB;
import static nusemp.testutil.TypicalContacts.CARL;
import static nusemp.testutil.TypicalContacts.DANIEL;
import static nusemp.testutil.TypicalContacts.ELLE;
import static nusemp.testutil.TypicalContacts.FIONA;
import static nusemp.testutil.TypicalContacts.GEORGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nusemp.model.event.Event;
import nusemp.model.fields.Address;
import nusemp.model.fields.Date;
import nusemp.model.fields.Name;

/**
 * A utility class containing a list of {@code Event} objects to be used in tests.
 */
public class TypicalEvents {
    public static final Event MEETING_EMPTY = new Event(
            new Name("MEETING"), new Date("01-10-2025 14:00"), Address.empty());
    public static final Event MEETING_FILLED = new EventBuilder()
            .withName("MEETING")
            .withDate("01-10-2025 14:00")
            .withAddress("Meeting Room")
            .withParticipants(convertToContactStatusList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE))
            .build();
    public static final Event CONFERENCE_EMPTY = new Event(
            new Name("CONFERENCE"), new Date("29-02-2024 09:00"), Address.empty());
    public static final Event CONFERENCE_FILLED = new EventBuilder()
            .withName("CONFERENCE")
            .withDate("29-02-2024 09:00")
            .withAddress("Conference Room")
            .withParticipants(convertToContactStatusList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE))
            .build();
    public static final Event WORKSHOP_EMPTY = new Event(
            new Name("WORKSHOP"), new Date("01-10-2025 14:00"), Address.empty());
    public static final Event WORKSHOP_FILLED = new EventBuilder()
            .withName("WORKSHOP")
            .withDate("01-10-2025 14:00")
            .withAddress("SR2")
            .withParticipants(convertToContactStatusList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE))
            .build();
    public static final Event PARTY_EMPTY = new Event(
            new Name("PARTY"), new Date("31-12-2024 20:00"), Address.empty());
    public static final Event PARTY_HALF_FILLED = new EventBuilder()
            .withName("PARTY")
            .withDate("31-12-2024 20:00")
            .withAddress(ALICE.getAddress().toString())
            .withParticipants(convertToContactStatusList(ALICE, BOB, CARL))
            .build();
    public static final Event MEETING_WITH_TAGS = new EventBuilder()
            .withName("MEETING")
            .withDate("01-10-2025 14:00")
            .withAddress("Meeting Room A")
            .withTags("Music", "Networking")
            .build();
    public static final Event MEETING_WITH_TAGS_FILLED = new EventBuilder()
            .withName("MEETING")
            .withDate("01-10-2025 14:00")
            .withAddress("Meeting Room A")
            .withTags("Music", "Networking")
            .withParticipants(convertToContactStatusList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE))
            .build();;

    public static List<Event> getTypicalEvents() {
        return new ArrayList<>(Arrays.asList(MEETING_EMPTY, CONFERENCE_EMPTY,
                WORKSHOP_FILLED, PARTY_HALF_FILLED));
    }

}
