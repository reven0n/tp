package nusemp.logic.commands;

import static java.util.Objects.requireNonNull;
import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalEvents.CONFERENCE_EMPTY;
import static nusemp.testutil.TypicalEvents.CONFERENCE_FILLED;
import static nusemp.testutil.TypicalEvents.MEETING_EMPTY;
import static nusemp.testutil.TypicalEvents.WORKSHOP_EMPTY;
import static nusemp.testutil.TypicalEvents.WORKSHOP_FILLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import nusemp.commons.core.GuiSettings;
import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.model.AddressBook;
import nusemp.model.Model;
import nusemp.model.ReadOnlyAddressBook;
import nusemp.model.ReadOnlyUserPrefs;
import nusemp.model.event.Event;
import nusemp.model.person.Person;

class EventAddCommandTest {
    @Test
    public void constructor_nullEvent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EventAddCommand(null));
    }

    @Test
    public void execute_eventAcceptedByModel_addSuccessful() throws Exception {
        ModelStubWithAcceptingEventAdded modelStub = new ModelStubWithAcceptingEventAdded();
        CommandResult commandResult1 = new EventAddCommand(MEETING_EMPTY).execute(modelStub);

        assertEquals(String.format(EventAddCommand.MESSAGE_SUCCESS, Messages.format(MEETING_EMPTY)),
                commandResult1.getFeedbackToUser());
        assertEquals(1, modelStub.eventsAdded.size());
        assertEquals(MEETING_EMPTY, modelStub.eventsAdded.get(0));

        // handling of multiple events and event with participants
        CommandResult commandResult2 = new EventAddCommand(CONFERENCE_FILLED).execute(modelStub);
        assertEquals(String.format(EventAddCommand.MESSAGE_SUCCESS, Messages.format(CONFERENCE_FILLED)),
                commandResult2.getFeedbackToUser());
        assertEquals(2, modelStub.eventsAdded.size());
        assertEquals(CONFERENCE_FILLED, modelStub.eventsAdded.get(1));
    }

    @Test
    public void execute_sameEventDate_addSuccessful() throws Exception {
        ModelStubWithAcceptingEventAdded modelStub = new ModelStubWithAcceptingEventAdded();
        modelStub.addEvent(MEETING_EMPTY);
        CommandResult commandResult1 = new EventAddCommand(WORKSHOP_EMPTY).execute(modelStub);

        assertEquals(String.format(EventAddCommand.MESSAGE_SUCCESS, Messages.format(WORKSHOP_EMPTY)),
                commandResult1.getFeedbackToUser());
        assertEquals(2, modelStub.eventsAdded.size());
        assertEquals(WORKSHOP_EMPTY, modelStub.eventsAdded.get(1));

        // handling of multiple events and event with participants
        modelStub.eventsAdded.remove(1);
        CommandResult commandResult2 = new EventAddCommand(WORKSHOP_FILLED).execute(modelStub);
        assertEquals(String.format(EventAddCommand.MESSAGE_SUCCESS, Messages.format(WORKSHOP_FILLED)),
                commandResult2.getFeedbackToUser());
        assertEquals(2, modelStub.eventsAdded.size());
        assertEquals(WORKSHOP_FILLED, modelStub.eventsAdded.get(1));
    }

    @Test
    public void execute_duplicateEvent_throwsCommandException() {
        EventAddCommand eventAddCommand = new EventAddCommand(CONFERENCE_EMPTY);
        ModelStub modelStub = new ModelStubWithEvent(CONFERENCE_EMPTY);
        assertThrows(CommandException.class,
                EventAddCommand.MESSAGE_DUPLICATE_EVENT, () -> eventAddCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        EventAddCommand addMeetingCommand = new EventAddCommand(MEETING_EMPTY);
        EventAddCommand addConferenceCommand = new EventAddCommand(CONFERENCE_EMPTY);
        EventAddCommand addConferenceFullCommand = new EventAddCommand(CONFERENCE_FILLED);

        // same object -> returns true
        assertEquals(addMeetingCommand, addMeetingCommand);

        // same values -> returns true
        EventAddCommand addMeetingCommandCopy = new EventAddCommand(MEETING_EMPTY);
        assertEquals(addMeetingCommand, addMeetingCommandCopy);

        // different types -> returns false
        assertNotEquals(1, addMeetingCommand);

        // null -> returns false
        assertNotEquals(null, addMeetingCommand);

        // different event -> returns false
        assertNotEquals(addMeetingCommand, addConferenceCommand);

        // different participants -> returns false
        assertNotEquals(addConferenceCommand, addConferenceFullCommand);
    }

    /**
     * A default model stub that have all the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasEvent(Event event) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteEvent(Event target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addEvent(Event event) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setEvent(Event target, Event editedEvent) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addParticipantToEvent(Event event, Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Event> getFilteredEventList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredEventList(Predicate<Event> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Person getPersonByIndex(Index index) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Event getEventByIndex(Index index) {
            throw new AssertionError("This method should not be called.");
        }
    }

    private class ModelStubWithEvent extends ModelStub {
        private final Event event;

        ModelStubWithEvent(Event event) {
            requireNonNull(event);
            this.event = event;
        }

        @Override
        public boolean hasEvent(Event event) {
            requireNonNull(event);
            return this.event.isSameEvent(event);
        }
    }

    private class ModelStubWithAcceptingEventAdded extends ModelStub {
        final ArrayList<Event> eventsAdded = new ArrayList<>();

        @Override
        public boolean hasEvent(Event event) {
            requireNonNull(event);
            return eventsAdded.stream().anyMatch(event::isSameEvent);
        }

        @Override
        public void addEvent(Event event) {
            requireNonNull(event);
            eventsAdded.add(event);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }
}
