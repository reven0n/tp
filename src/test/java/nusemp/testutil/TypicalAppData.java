package nusemp.testutil;

import static nusemp.testutil.TypicalContacts.getTypicalContacts;
import static nusemp.testutil.TypicalEvents.getTypicalEvents;

import nusemp.model.AppData;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;

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
        // Add linked events to each contact
        for (Contact contact : TypicalContacts.getTypicalContacts()) {
            Contact updatedContact = contact;
            for (Event event : appData.getEventList()) {
                if (event.hasContact(contact)) {
                    updatedContact = updatedContact.addEvent(event);
                }
            }
            appData.setContact(contact, updatedContact);
        }
        return appData;
    }
}
