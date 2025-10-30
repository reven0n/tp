package nusemp.logic;

import java.nio.file.Path;
import java.util.List;

import javafx.collections.ObservableList;

import nusemp.commons.core.GuiSettings;
import nusemp.logic.commands.CommandResult;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.logic.parser.exceptions.ParseException;
import nusemp.model.Model;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.participant.Participant;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /**
     * Returns the AppData.
     *
     * @see Model#getAppData()
     */
    ReadOnlyAppData getAppData();

    /** Returns an unmodifiable view of the filtered list of contacts */
    ObservableList<Contact> getFilteredContactList();

    /** Returns an unmodifiable view of the filtered list of events */
    ObservableList<Event> getFilteredEventList();

    /** Returns the list of participants for the given event */
    List<Participant> getParticipants(Event event);

    /** Returns the list of participants containing the given contact */
    List<Participant> getParticipants(Contact contact);

    /**
     * Returns the user prefs' app data file path.
     */
    Path getAppDataFilePath();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}
