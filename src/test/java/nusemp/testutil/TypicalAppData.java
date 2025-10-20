package nusemp.testutil;

import static nusemp.testutil.TypicalContacts.getTypicalContacts;

import nusemp.model.AppData;
import nusemp.model.contact.Contact;

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
}
