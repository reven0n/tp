package nusemp.logic.commands.event;

import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithEvents;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_EVENT;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_CONTACT;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.CommandResult;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.participant.ParticipantStatus;

public class EventUnlinkCommandTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());
    }

    @Test
    public void constructor_nullIndexes_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EventUnlinkCommand(null, INDEX_FIRST_CONTACT));
        assertThrows(NullPointerException.class, () -> new EventUnlinkCommand(INDEX_FIRST_EVENT, null));
        assertThrows(NullPointerException.class, () -> new EventUnlinkCommand(null));
    }

    @Test
    public void execute_validIndexesUnfilteredList_success() throws Exception {
        Index validEventIndex = INDEX_FIRST_EVENT;
        Index validContactIndex = INDEX_FIRST_CONTACT;

        Event eventToUnlink = model.getFilteredEventList().get(validEventIndex.getZeroBased());
        Contact contactToUnlink = model.getFilteredContactList().get(validContactIndex.getZeroBased());

        // First link the contact to the event
        model.addParticipant(contactToUnlink, eventToUnlink, ParticipantStatus.UNKNOWN);

        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(validEventIndex, validContactIndex);

        String expectedMessage = String.format(EventUnlinkCommand.MESSAGE_SUCCESS,
                contactToUnlink.getName(), eventToUnlink.getName());

        Model expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        expectedModel.removeParticipant(contactToUnlink, eventToUnlink);

        assertCommandSuccess(unlinkCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidEventIndex_throwsCommandException() {
        Index outOfBoundEventIndex = Index.fromOneBased(model.getFilteredEventList().size() + 1);
        Index validContactIndex = INDEX_FIRST_CONTACT;
        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(outOfBoundEventIndex, validContactIndex);

        assertCommandFailure(unlinkCommand, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidContactIndex_throwsCommandException() {
        Index validEventIndex = INDEX_FIRST_EVENT;
        Index outOfBoundContactIndex = Index.fromOneBased(model.getFilteredContactList().size() + 1);
        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(validEventIndex, outOfBoundContactIndex);

        assertCommandFailure(unlinkCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_contactNotInEvent_throwsCommandException() {
        Index validEventIndex = INDEX_FIRST_EVENT;
        Index validContactIndex = INDEX_FIRST_CONTACT;

        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(validEventIndex, validContactIndex);

        assertCommandFailure(unlinkCommand, model, String.format(EventUnlinkCommand.MESSAGE_CONTACT_NOT_FOUND,
                model.getFilteredContactList().get(validContactIndex.getZeroBased()).getName(),
                model.getFilteredEventList().get(validEventIndex.getZeroBased()).getName()));
    }

    @Test
    public void execute_unlinkAll_success() throws Exception {
        Index validEventIndex = INDEX_FIRST_EVENT;
        Event eventToUnlink = model.getFilteredEventList().get(validEventIndex.getZeroBased());

        // Link all contacts first
        for (Contact contact : model.getFilteredContactList()) {
            if (!model.hasParticipant(contact, eventToUnlink)) {
                model.addParticipant(contact, eventToUnlink, ParticipantStatus.UNKNOWN);
            }
        }

        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(validEventIndex);

        CommandResult result = unlinkCommand.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Successfully unlinked"));
        assertTrue(result.getFeedbackToUser().contains(eventToUnlink.getName().toString()));
        assertTrue(result.getFeedbackToUser().contains("contact(s)"));
    }

    @Test
    public void execute_unlinkAllNoContacts_throwsCommandException() {
        model.updateFilteredContactList(contact -> false); // Filter out all contacts
        Index validEventIndex = INDEX_FIRST_EVENT;

        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(validEventIndex);

        assertCommandFailure(unlinkCommand, model, String.format(EventUnlinkCommand.MESSAGE_NO_CONTACTS_TO_UNLINK,
                model.getFilteredEventList().get(validEventIndex.getZeroBased()).getName()));
    }

    @Test
    public void execute_unlinkAllNoLinkedContacts_throwsCommandException() {
        Index validEventIndex = INDEX_FIRST_EVENT;

        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(validEventIndex);

        assertCommandFailure(unlinkCommand, model, "No contacts were unlinked from the event");
    }

    @Test
    public void equals() {
        EventUnlinkCommand unlinkFirstCommand = new EventUnlinkCommand(INDEX_FIRST_EVENT, INDEX_FIRST_CONTACT);
        EventUnlinkCommand unlinkSecondCommand = new EventUnlinkCommand(INDEX_SECOND_EVENT, INDEX_SECOND_CONTACT);
        EventUnlinkCommand unlinkAllFirstCommand = new EventUnlinkCommand(INDEX_FIRST_EVENT);
        EventUnlinkCommand unlinkAllSecondCommand = new EventUnlinkCommand(INDEX_SECOND_EVENT);

        // same object -> returns true
        assertTrue(unlinkFirstCommand.equals(unlinkFirstCommand));

        // same values -> returns true
        EventUnlinkCommand unlinkFirstCommandCopy = new EventUnlinkCommand(INDEX_FIRST_EVENT, INDEX_FIRST_CONTACT);
        assertTrue(unlinkFirstCommand.equals(unlinkFirstCommandCopy));

        // same event index for unlinkAll -> returns true
        EventUnlinkCommand unlinkAllFirstCommandCopy = new EventUnlinkCommand(INDEX_FIRST_EVENT);
        assertTrue(unlinkAllFirstCommand.equals(unlinkAllFirstCommandCopy));

        // different types -> returns false
        assertFalse(unlinkFirstCommand.equals(1));

        // null -> returns false
        assertFalse(unlinkFirstCommand.equals(null));

        // different indexes -> returns false
        assertFalse(unlinkFirstCommand.equals(unlinkSecondCommand));

        // different event indexes for unlinkAll -> returns false
        assertFalse(unlinkAllFirstCommand.equals(unlinkAllSecondCommand));

        // unlinkAll vs single unlink -> returns false
        assertFalse(unlinkFirstCommand.equals(unlinkAllFirstCommand));
    }

    @Test
    public void toStringMethod() {
        EventUnlinkCommand unlinkCommand = new EventUnlinkCommand(INDEX_FIRST_EVENT, INDEX_FIRST_CONTACT);
        String expected = EventUnlinkCommand.class.getCanonicalName()
                + "{eventIndex=" + INDEX_FIRST_EVENT
                + ", contactIndex=" + INDEX_FIRST_CONTACT
                + ", isUnlinkAll=false" + "}";
        assertEquals(expected, unlinkCommand.toString());

        EventUnlinkCommand unlinkAllCommand = new EventUnlinkCommand(INDEX_FIRST_EVENT);
        String expectedAll = EventUnlinkCommand.class.getCanonicalName()
                + "{eventIndex=" + INDEX_FIRST_EVENT
                + ", contactIndex=null"
                + ", isUnlinkAll=true" + "}";
        assertEquals(expectedAll, unlinkAllCommand.toString());
    }
}
