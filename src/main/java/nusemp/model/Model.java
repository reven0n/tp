package nusemp.model;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import javafx.collections.ObservableList;

import nusemp.commons.core.GuiSettings;
import nusemp.commons.core.index.Index;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.event.ParticipantStatus;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Contact> PREDICATE_SHOW_ALL_CONTACTS = unused -> true;

    /** {@code Predicate} that always evaluate to true */
    Predicate<Event> PREDICATE_SHOW_ALL_EVENTS = unused -> true;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' app data file path.
     */
    Path getAppDataFilePath();

    /**
     * Sets the user prefs' app data file path.
     */
    void setAppDataFilePath(Path appDataFilePath);

    /**
     * Replaces app data with the data in {@code appData}.
     */
    void setAppData(ReadOnlyAppData appData);

    /** Returns the AppData */
    ReadOnlyAppData getAppData();

    /**
     * Returns true if a contact with the same identity as {@code contact} exists in the contact list.
     */
    boolean hasContact(Contact contact);

    /**
     * Deletes the given contact.
     * The contact must exist in the contact list.
     */
    void deleteContact(Contact target);

    /**
     * Adds the given contact.
     * {@code contact} must not already exist in the contact list.
     */
    void addContact(Contact contact);

    /**
     * Replaces the given contact {@code target} with {@code editedContact}.
     * {@code target} must exist in the contact list.
     * The contact identity of {@code editedContact} must not be the same as another existing contact.
     */
    void setContact(Contact target, Contact editedContact);

    /** Returns an unmodifiable view of the filtered contact list */
    ObservableList<Contact> getFilteredContactList();

    /**
     * Updates the filter of the filtered contact list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredContactList(Predicate<Contact> predicate);

    //=========== Event Operations =============================================================

    /**
     * Returns true if an event with the same identity as {@code event} exists in the event list.
     */
    boolean hasEvent(Event event);

    /**
     * Deletes the given event.
     * The event must exist in the event list.
     */
    void deleteEvent(Event target);

    /**
     * Adds the given event.
     * {@code event} must not already exist in the event list.
     */
    void addEvent(Event event);

    /**
     * Replaces the given event {@code target} with {@code editedEvent}.
     * {@code target} must exist in the event list.
     * The event identity of {@code editedEvent} must not be the same as another existing event.
     */
    void setEvent(Event target, Event editedEvent);

    /** Returns an unmodifiable view of the filtered event list */
    ObservableList<Event> getFilteredEventList();

    /**
     * Updates the filter of the filtered event list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredEventList(Predicate<Event> predicate);

    //=========== Lookup Helper Methods ========================================================

    /**
     * Returns the contact at the specified index in the filtered contact list.
     * @param index The 1-based index in the filtered contact list.
     * @return The contact at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    Contact getContactByIndex(Index index);

    /**
     * Returns the event at the specified index in the filtered event list.
     * @param index The 1-based index in the filtered event list.
     * @return The event at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    Event getEventByIndex(Index index);

    /**
     * Adds a participant event link between the given contact and event.
     */
    void addParticipantEvent(Contact contact, Event event, ParticipantStatus status);

    /**
     * Removes the participant event link between the given contact and event.
     */
    void removeParticipantEvent(Contact contact, Event event);

    /**
     * Returns true if the given contact is linked to the given event.
     */
    boolean hasParticipantEvent(Contact contact, Event event);

    /**
     * Gets the participant status for the given contact and event.
     */
    ParticipantStatus getParticipantStatus(Contact contact, Event event);

    /**
     * Gets all events for the given contact.
     */
    List<Event> getEventsForContact(Contact contact);

    /**
     * Gets all contacts for the given event.
     */
    List<Contact> getContactsForEvent(Event event);

}
