package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;
import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_CONTACT;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import nusemp.commons.core.GuiSettings;
import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.model.AppData;
import nusemp.model.Model;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.ReadOnlyUserPrefs;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.event.ParticipantStatus;
import nusemp.testutil.ContactBuilder;
import nusemp.testutil.EventBuilder;

public class EventUnlinkCommandTest {
    @Test
    public void constructor_nullIndexes_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EventUnlinkCommand(null, INDEX_FIRST_CONTACT));
        assertThrows(NullPointerException.class, () -> new EventUnlinkCommand(INDEX_FIRST_EVENT, null));
    }

    /*
    @Test
    public void execute_validIndexesUnfilteredList_success() throws Exception {
        Contact validContact = new ContactBuilder().build();
        Event validEvent = new EventBuilder().build();

        ModelStubWithEventAndContact modelStub = new ModelStubWithEventAndContact(validEvent, validContact);

        // First link the contact to the event
        modelStub.addParticipantEvent(validContact, validEvent, ParticipantStatus.UNKNOWN);

        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(INDEX_FIRST_EVENT, INDEX_FIRST_CONTACT);

        CommandResult commandResult = unlinkCommand.execute(modelStub);

        assertEquals(String.format(EventUnlinkCommand.MESSAGE_SUCCESS,
                        validContact.getName().toString()),
                commandResult.getFeedbackToUser());
    }
     */

    @Test
    public void execute_invalidEventIndex_throwsCommandException() {
        ModelStubWithEventAndContact modelStub = new ModelStubWithEventAndContact(
                new EventBuilder().build(), new ContactBuilder().build());
        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(Index.fromZeroBased(5), INDEX_FIRST_CONTACT);

        assertThrows(CommandException.class,
                Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX, () -> unlinkCommand.execute(modelStub));
    }

    @Test
    public void execute_invalidContactIndex_throwsCommandException() {
        ModelStubWithEventAndContact modelStub = new ModelStubWithEventAndContact(
                new EventBuilder().build(), new ContactBuilder().build());
        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(INDEX_FIRST_EVENT, Index.fromZeroBased(5));

        assertThrows(CommandException.class,
                Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, () -> unlinkCommand.execute(modelStub));
    }

    /*
    @Test
    public void execute_contactNotInEvent_throwsCommandException() {
        Contact contact = new ContactBuilder().build();
        Event event = new EventBuilder().build();
        ModelStubWithEventAndContact modelStub = new ModelStubWithEventAndContact(event, contact);
        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(INDEX_FIRST_EVENT, INDEX_FIRST_CONTACT);

        assertThrows(CommandException.class,
                EventUnlinkCommand.MESSAGE_CONTACT_NOT_FOUND, () -> unlinkCommand.execute(modelStub));
    }
     */

    @Test
    public void equals() {
        EventUnlinkCommand unlinkFirstCommand = new EventUnlinkCommand(INDEX_FIRST_EVENT, INDEX_FIRST_CONTACT);
        EventUnlinkCommand unlinkSecondCommand = new EventUnlinkCommand(INDEX_SECOND_EVENT, INDEX_SECOND_CONTACT);

        // same object -> returns true
        assertTrue(unlinkFirstCommand.equals(unlinkFirstCommand));

        // same values -> returns true
        EventUnlinkCommand unlinkFirstCommandCopy = new EventUnlinkCommand(INDEX_FIRST_EVENT, INDEX_FIRST_CONTACT);
        assertTrue(unlinkFirstCommand.equals(unlinkFirstCommandCopy));

        // different types -> returns false
        assertFalse(unlinkFirstCommand.equals(1));
        // null -> returns false
        assertFalse(unlinkFirstCommand.equals(null));

        // different indexes -> returns false
        assertFalse(unlinkFirstCommand.equals(unlinkSecondCommand));
    }

    @Test
    public void toStringMethod() {
        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(INDEX_FIRST_EVENT, INDEX_FIRST_CONTACT);
        String expected = EventUnlinkCommand.class.getCanonicalName()
                + "{eventIndex=" + INDEX_FIRST_EVENT
                + ", contactIndex=" + INDEX_FIRST_CONTACT + "}";
        assertEquals(expected, unlinkCommand.toString());
    }

    /**
     * A default model stub that have all methods failing.
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
        public Path getAppDataFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAppDataFilePath(Path appDataFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addContact(Contact contact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAppData(ReadOnlyAppData appData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAppData getAppData() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasContact(Contact contact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteContact(Contact target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setContact(Contact target, Contact editedContact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredContactList(Predicate<Contact> predicate) {
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
        public ObservableList<Event> getFilteredEventList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredEventList(Predicate<Event> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Contact getContactByIndex(Index index) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Event getEventByIndex(Index index) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addParticipantEvent(Contact contact, Event event, ParticipantStatus status) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void removeParticipantEvent(Contact contact, Event event) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasParticipantEvent(Contact contact, Event event) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ParticipantStatus getParticipantStatus(Contact contact, Event event) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public List<Event> getEventsForContact(Contact contact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public List<Contact> getContactsForEvent(Event event) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single event and contact.
     */
    private class ModelStubWithEventAndContact extends ModelStub {
        private final Event event;
        private final Contact contact;
        private Event updatedEvent;
        private Contact updatedContact;
        private boolean isLinked = false;

        ModelStubWithEventAndContact(Event event, Contact contact) {
            requireNonNull(event);
            requireNonNull(contact);
            this.event = event;
            this.contact = contact;
        }

        @Override
        public ObservableList<Event> getFilteredEventList() {
            return FXCollections.observableArrayList(event);
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            return FXCollections.observableArrayList(contact);
        }

        @Override
        public void setEvent(Event target, Event editedEvent) {
            requireNonNull(target);
            requireNonNull(editedEvent);
            this.updatedEvent = editedEvent;
        }

        @Override
        public void setContact(Contact target, Contact editedContact) {
            requireNonNull(target);
            requireNonNull(editedContact);
            this.updatedContact = editedContact;
        }

        @Override
        public ReadOnlyAppData getAppData() {
            return new AppData();
        }

        @Override
        public void addParticipantEvent(Contact contact, Event event, ParticipantStatus status) {
            requireNonNull(contact);
            requireNonNull(event);
            requireNonNull(status);
            isLinked = true;
        }

        @Override
        public void removeParticipantEvent(Contact contact, Event event) {
            requireNonNull(contact);
            requireNonNull(event);
            isLinked = false;
        }

        @Override
        public void updateFilteredContactList(Predicate<Contact> predicate) {
            // Do nothing for this stub
        }
    }

}
