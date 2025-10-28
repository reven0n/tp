package nusemp.model;

import static java.util.Objects.requireNonNull;
import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;

import nusemp.commons.core.GuiSettings;
import nusemp.commons.core.LogsCenter;
import nusemp.commons.core.index.Index;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.event.Participant;
import nusemp.model.event.ParticipantStatus;

/**
 * Represents the in-memory model of the app data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AppData appData;
    private final UserPrefs userPrefs;
    private final FilteredList<Contact> filteredContacts;
    private final FilteredList<Event> filteredEvents;

    /**
     * Initializes a ModelManager with the given appData and userPrefs.
     */
    public ModelManager(ReadOnlyAppData appData, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(appData, userPrefs);

        logger.fine("Initializing with app data: " + appData + " and user prefs " + userPrefs);

        this.appData = new AppData(appData);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredContacts = new FilteredList<>(this.appData.getContactList());
        filteredEvents = new FilteredList<>(this.appData.getEventList());
    }

    public ModelManager() {
        this(new AppData(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAppDataFilePath() {
        return userPrefs.getAppDataFilePath();
    }

    @Override
    public void setAppDataFilePath(Path appDataFilePath) {
        requireNonNull(appDataFilePath);
        userPrefs.setAppDataFilePath(appDataFilePath);
    }

    //=========== AppData ================================================================================

    @Override
    public void setAppData(ReadOnlyAppData appData) {
        this.appData.resetData(appData);
    }

    @Override
    public ReadOnlyAppData getAppData() {
        return appData;
    }

    @Override
    public boolean hasContact(Contact contact) {
        requireNonNull(contact);
        return appData.hasContact(contact);
    }

    @Override
    public void deleteContact(Contact target) {
//        // Remove contact from all linked events
//        for (Event event : target.getEvents()) {
//            if (hasEvent(event)) {
//                Event updatedEvent = event.withoutContact(target);
//                appData.setEvent(event, updatedEvent);
//            }
//        }
        appData.removeContact(target);
    }

    @Override
    public void addContact(Contact contact) {
        appData.addContact(contact);
        updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
    }

    @Override
    public void setContact(Contact target, Contact editedContact) {
        requireAllNonNull(target, editedContact);
        appData.setContact(target, editedContact);

//        // Update all events that had the old contact as participant
//        if (!target.getEmail().equals(editedContact.getEmail())) {
//            updateEventsForEmailChange(target, editedContact);
//        }
    }

    /**
     * Updates all events that had the old contact as participant when the contact's email is changed.
     * @param target the original contact before edit
     * @param editedContact the edited contact with updated email
     */
    private void updateEventsForEmailChange(Contact target, Contact editedContact) {
        for (Event event : target.getEvents()) {
            if (hasEvent(event) && event.hasContactWithEmail(target.getEmail().value)) {
                Event updatedEvent = createUpdatedEvent(event, target, editedContact);
                appData.setEvent(event, updatedEvent);
            }
        }
    }

    /**
     * Creates an updated event by replacing the target contact with the edited contact.
     * @param event the original event
     * @param target the original contact who is being replaced
     * @param editedContact the contact to be replaced with
     * @return the updated event with the edited contact as participant
     */
    private Event createUpdatedEvent(Event event, Contact target, Contact editedContact) {
        return event.withoutContact(target).withContact(editedContact);
    }

    //=========== Filtered Contact List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the filtered contact list
     */
    @Override
    public ObservableList<Contact> getFilteredContactList() {
        return filteredContacts;
    }

    @Override
    public void updateFilteredContactList(Predicate<Contact> predicate) {
        requireNonNull(predicate);
        filteredContacts.setPredicate(predicate);
    }

    //=========== Event Operations ===========================================================================

    @Override
    public boolean hasEvent(Event event) {
        requireNonNull(event);
        return appData.hasEvent(event);
    }

    @Override
    public void deleteEvent(Event target) {
        // Remove event from all linked contacts
//        for (Participant participant : target.getParticipants()) {
//            Contact contact = participant.getContact();
//            if (hasContact(contact)) {
//                Contact updatedContact = contact.removeEvent(target);
//                appData.setContact(contact, updatedContact);
//            }
//        }
        appData.removeEvent(target);
    }

    @Override
    public void addEvent(Event event) {
        appData.addEvent(event);
        updateFilteredEventList(PREDICATE_SHOW_ALL_EVENTS);
    }

    @Override
    public void setEvent(Event target, Event editedEvent) {
        requireAllNonNull(target, editedEvent);
        // update event in the list
        appData.setEvent(target, editedEvent);
    }

    //=========== Filtered Event List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the filtered event list
     */
    @Override
    public ObservableList<Event> getFilteredEventList() {
        return filteredEvents;
    }

    @Override
    public ObservableMap<Contact, List<ParticipantEvent>> getContactEventMap() {
        return appData.getContactEventMap();
    }

    @Override
    public void updateFilteredEventList(Predicate<Event> predicate) {
        requireNonNull(predicate);
        filteredEvents.setPredicate(predicate);
    }

    //=========== Participant Map Operations ===========================================================
    @Override
    public void addParticipantEvent(Contact contact, Event event, ParticipantStatus status) {
        requireAllNonNull(contact, event, status);
        appData.addParticipantEvent(contact, event, status);
        updateFilteredContactList(PREDICATE_SHOW_ALL_CONTACTS);
    }

    @Override
    public void removeParticipantEvent(Contact contact, Event event) {
        requireAllNonNull(contact, event);
        appData.removeParticipantEvent(contact, event);
    }

    @Override
    public boolean hasParticipantEvent(Contact contact, Event event) {
        requireAllNonNull(contact, event);
        return appData.hasParticipantEvent(contact, event);
    }

    @Override
    public ParticipantStatus getParticipantStatus(Contact contact, Event event) {
        requireAllNonNull(contact, event);
        return appData.getParticipantStatus(contact, event);
    }

    @Override
    public List<Event> getEventsForContact(Contact contact) {
        requireNonNull(contact);
        return appData.getEventsForContact(contact);
    }

    @Override
    public List<Contact> getContactsForEvent(Event event) {
        requireNonNull(event);
        return appData.getContactsForEvent(event);
    }


    //=========== Lookup Helper Methods ========================================================

    @Override
    public Contact getContactByIndex(Index index) {
        return filteredContacts.get(index.getZeroBased());
    }

    @Override
    public Event getEventByIndex(Index index) {
        return filteredEvents.get(index.getZeroBased());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return appData.equals(otherModelManager.appData)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredContacts.equals(otherModelManager.filteredContacts)
                && filteredEvents.equals(otherModelManager.filteredEvents);
    }

}
