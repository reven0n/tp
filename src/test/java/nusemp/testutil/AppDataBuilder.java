package nusemp.testutil;

import nusemp.model.AppData;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;

/**
 * A utility class to help with building AppData objects.
 * Example usage: <br>
 *     {@code AppData ab = new AppDataBuilder().withContact("John", "Doe").build();}
 */
public class AppDataBuilder {

    private final AppData appData;

    public AppDataBuilder() {
        appData = new AppData();
    }

    public AppDataBuilder(AppData appData) {
        this.appData = appData;
    }

    /**
     * Adds a new {@code Contact} to the {@code AppData} that we are building.
     */
    public AppDataBuilder withContact(Contact contact) {
        appData.addContact(contact);
        return this;
    }

    /**
     * Adds a new {@code Event} to the {@code AppData} that we are building.
     */
    public AppDataBuilder withEvent(Event event) {
        appData.addEvent(event);
        return this;
    }

    public AppData build() {
        return appData;
    }
}
