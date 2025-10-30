package nusemp.model;

import static java.util.Objects.requireNonNull;
import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import nusemp.commons.core.GuiSettings;
import nusemp.commons.core.LogsCenter;
import nusemp.commons.core.index.Index;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.participant.Participant;
import nusemp.model.participant.ParticipantStatus;

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
    public void updateFilteredEventList(Predicate<Event> predicate) {
        requireNonNull(predicate);
        filteredEvents.setPredicate(predicate);
    }

    //=========== Participant Map Operations ===========================================================
    @Override
    public void addParticipant(Contact contact, Event event, ParticipantStatus status) {
        requireAllNonNull(contact, event, status);
        appData.addParticipant(contact, event, status);
    }

    @Override
    public void removeParticipant(Contact contact, Event event) {
        requireAllNonNull(contact, event);
        appData.removeParticipant(contact, event);
    }

    @Override
    public boolean hasParticipant(Contact contact, Event event) {
        requireAllNonNull(contact, event);
        return appData.hasParticipant(contact, event);
    }

    @Override
    public void setParticipant(Contact contact, Event event, ParticipantStatus status) {
        requireAllNonNull(contact, event, status);
        appData.setParticipant(contact, event, status);
    }

    @Override
    public List<Participant> getParticipants(Contact contact) {
        requireNonNull(contact);
        return appData.getParticipants(contact);
    }

    @Override
    public List<Participant> getParticipants(Event event) {
        requireNonNull(event);
        return appData.getParticipants(event);
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
                && filteredContactsHaveSameFields(otherModelManager)
                && filteredEventsHaveSameFields(otherModelManager);
    }

    private boolean filteredContactsHaveSameFields(ModelManager other) {
        if (filteredContacts.size() != other.filteredContacts.size()) {
            return false;
        }
        for (int i = 0; i < filteredContacts.size(); i++) {
            if (!filteredContacts.get(i).hasSameFields(other.filteredContacts.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean filteredEventsHaveSameFields(ModelManager other) {
        if (filteredEvents.size() != other.filteredEvents.size()) {
            return false;
        }
        for (int i = 0; i < filteredEvents.size(); i++) {
            if (!filteredEvents.get(i).hasSameFields(other.filteredEvents.get(i))) {
                return false;
            }
        }
        return true;
    }

}
