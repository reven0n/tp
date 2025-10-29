package nusemp.testutil;

import static nusemp.testutil.TypicalContacts.ALICE;
import static nusemp.testutil.TypicalContacts.BENSON;
import static nusemp.testutil.TypicalContacts.CARL;
import static nusemp.testutil.TypicalContacts.DANIEL;
import static nusemp.testutil.TypicalContacts.ELLE;
import static nusemp.testutil.TypicalContacts.FIONA;
import static nusemp.testutil.TypicalContacts.GEORGE;
import static nusemp.testutil.TypicalContacts.getTypicalContacts;
import static nusemp.testutil.TypicalEvents.PARTY_HALF_FILLED;
import static nusemp.testutil.TypicalEvents.WORKSHOP_FILLED;
import static nusemp.testutil.TypicalEvents.getTypicalEvents;

import nusemp.model.AppData;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.participant.ParticipantStatus;

/**
 * A utility class containing a list of {@code AppData} objects to be used in tests.
 */
public class TypicalAppData {
    /**
     * Returns an {@code AppData} with all the typical contacts without events.
     */
    public static AppData getTypicalAppDataWithoutEvent() {
        AppData appData = new AppData();
        for (Contact contact : getTypicalContacts()) {
            appData.addContact(contact);
        }
        return appData;
    }

    /**
     * Returns an {@code AppData} with all the typical contacts with events.
     */
    public static AppData getTypicalAppDataWithEvents() {
        AppData appData = new AppData();
        for (Contact contact : TypicalContacts.getTypicalContacts()) {
            appData.addContact(contact);
        }
        for (Event event : getTypicalEvents()) {
            appData.addEvent(event);
        }
        return appData;
    }

    /**
     * Returns an {@code AppData} with all the typical contacts, events and participants.
     */
    public static AppData getTypicalAppData() {
        AppData appData = new AppData();
        for (Contact contact : TypicalContacts.getTypicalContacts()) {
            appData.addContact(contact);
        }
        for (Event event : getTypicalEvents()) {
            appData.addEvent(event);
        }
        addParticipants(appData);
        return appData;
    }

    private static void addParticipants(AppData appData) {
        appData.addParticipant(ALICE, WORKSHOP_FILLED, ParticipantStatus.AVAILABLE);
        appData.addParticipant(BENSON, WORKSHOP_FILLED, ParticipantStatus.UNAVAILABLE);
        appData.addParticipant(CARL, WORKSHOP_FILLED, ParticipantStatus.AVAILABLE);
        appData.addParticipant(DANIEL, WORKSHOP_FILLED, ParticipantStatus.AVAILABLE);
        appData.addParticipant(ELLE, WORKSHOP_FILLED, ParticipantStatus.UNKNOWN);
        appData.addParticipant(FIONA, WORKSHOP_FILLED, ParticipantStatus.UNAVAILABLE);
        appData.addParticipant(GEORGE, WORKSHOP_FILLED, ParticipantStatus.AVAILABLE);

        appData.addParticipant(ALICE, PARTY_HALF_FILLED, ParticipantStatus.AVAILABLE);
        appData.addParticipant(BENSON, PARTY_HALF_FILLED, ParticipantStatus.UNAVAILABLE);
        appData.addParticipant(CARL, PARTY_HALF_FILLED, ParticipantStatus.AVAILABLE);
    }
}
