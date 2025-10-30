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


class EventLinkCommandTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());
    }

    @Test
    public void constructor_nullParameters_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EventLinkCommand(null, Index.fromOneBased(1)));
        assertThrows(NullPointerException.class, () -> new EventLinkCommand(Index.fromOneBased(1), null));
        assertThrows(NullPointerException.class, () -> new EventLinkCommand(null));
    }

    @Test
    public void execute_invalidIndex_failure() {
        Index outOfBoundEventIndex = Index.fromOneBased(model.getFilteredEventList().size() + 1);
        Index validContactIndex = Index.fromOneBased(1);
        EventLinkCommand eventLinkCommand1 = new EventLinkCommand(outOfBoundEventIndex, validContactIndex);
        assertCommandFailure(eventLinkCommand1, model, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);

        Index validEventIndex = INDEX_FIRST_EVENT;
        Index outOfBoundContactIndex = Index.fromOneBased(model.getFilteredContactList().size() + 1);
        EventLinkCommand eventLinkCommand2 = new EventLinkCommand(validEventIndex, outOfBoundContactIndex);
        assertCommandFailure(eventLinkCommand2, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndices_success() throws Exception {
        Index validEventIndex = INDEX_FIRST_EVENT;
        Index validContactIndex = INDEX_FIRST_CONTACT;

        Event eventToLink = model.getFilteredEventList().get(validEventIndex.getZeroBased());
        Contact contactToLink = model.getFilteredContactList().get(validContactIndex.getZeroBased());

        EventLinkCommand linkCommand = new EventLinkCommand(validEventIndex, validContactIndex);

        String expectedMessage = String.format(EventLinkCommand.MESSAGE_SUCCESS,
                contactToLink.getName(), eventToLink.getName());

        Model expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        expectedModel.addParticipant(contactToLink, eventToLink, ParticipantStatus.UNKNOWN);

        assertCommandSuccess(linkCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateParticipant_throwsCommandException() {
        Index validEventIndex = INDEX_FIRST_EVENT;
        Index validContactIndex = INDEX_FIRST_CONTACT;

        Event event = model.getFilteredEventList().get(validEventIndex.getZeroBased());
        Contact contact = model.getFilteredContactList().get(validContactIndex.getZeroBased());

        // Link first
        model.addParticipant(contact, event, ParticipantStatus.UNKNOWN);

        EventLinkCommand linkCommand = new EventLinkCommand(validEventIndex, validContactIndex);

        assertCommandFailure(linkCommand, model,
                String.format(EventLinkCommand.MESSAGE_DUPLICATE_PARTICIPANT, contact.getEmail(), event.getName()));
    }

    @Test
    public void execute_linkAll_success() throws Exception {
        Index validEventIndex = INDEX_FIRST_EVENT;
        Event eventToLink = model.getFilteredEventList().get(validEventIndex.getZeroBased());

        EventLinkCommand linkCommand = new EventLinkCommand(validEventIndex);

        Model expectedModel = new ModelManager(model.getAppData(), new UserPrefs());

        for (Contact contact : model.getFilteredContactList()) {
            if (!expectedModel.hasParticipant(contact, eventToLink)) {
                expectedModel.addParticipant(contact, eventToLink, ParticipantStatus.UNKNOWN);
            }
        }

        CommandResult result = linkCommand.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Successfully linked"));
        assertTrue(result.getFeedbackToUser().contains(eventToLink.getName().toString()));
        assertTrue(result.getFeedbackToUser().contains("contact(s)"));
    }

    @Test
    public void execute_linkAllNoContacts_throwsCommandException() {
        model.updateFilteredContactList(contact -> false); // Filter out all contacts
        Index validEventIndex = INDEX_FIRST_EVENT;

        EventLinkCommand linkCommand = new EventLinkCommand(validEventIndex);

        assertCommandFailure(linkCommand, model, EventLinkCommand.MESSAGE_NO_CONTACTS_TO_LINK);
    }

    @Test
    public void execute_linkAllAlreadyLinked_throwsCommandException() {
        Index validEventIndex = INDEX_FIRST_EVENT;
        Event event = model.getFilteredEventList().get(validEventIndex.getZeroBased());

        // Link all contacts first
        for (Contact contact : model.getFilteredContactList()) {
            if (!model.hasParticipant(contact, event)) {
                model.addParticipant(contact, event, ParticipantStatus.UNKNOWN);
            }
        }

        EventLinkCommand linkCommand = new EventLinkCommand(validEventIndex);

        assertCommandFailure(linkCommand, model, "All contacts are already linked to the event");
    }

    @Test
    public void equals() {
        EventLinkCommand linkFirstCommand = new EventLinkCommand(INDEX_FIRST_EVENT, INDEX_FIRST_CONTACT);
        EventLinkCommand linkSecondCommand = new EventLinkCommand(INDEX_SECOND_EVENT, INDEX_SECOND_CONTACT);
        EventLinkCommand linkAllFirstCommand = new EventLinkCommand(INDEX_FIRST_EVENT);
        EventLinkCommand linkAllSecondCommand = new EventLinkCommand(INDEX_SECOND_EVENT);

        // same object -> returns true
        assertTrue(linkFirstCommand.equals(linkFirstCommand));

        // same values -> returns true
        EventLinkCommand linkFirstCommandCopy = new EventLinkCommand(INDEX_FIRST_EVENT, INDEX_FIRST_CONTACT);
        assertTrue(linkFirstCommand.equals(linkFirstCommandCopy));

        // same event index for linkAll -> returns true
        EventLinkCommand linkAllFirstCommandCopy = new EventLinkCommand(INDEX_FIRST_EVENT);
        assertTrue(linkAllFirstCommand.equals(linkAllFirstCommandCopy));

        // different types -> returns false
        assertFalse(linkFirstCommand.equals(1));

        // null -> returns false
        assertFalse(linkFirstCommand.equals(null));

        // different indexes -> returns false
        assertFalse(linkFirstCommand.equals(linkSecondCommand));

        // different event indexes for linkAll -> returns false
        assertFalse(linkAllFirstCommand.equals(linkAllSecondCommand));

        // linkAll vs single link -> returns false
        assertFalse(linkFirstCommand.equals(linkAllFirstCommand));
    }

    @Test
    public void toStringMethod() {
        EventLinkCommand linkCommand = new EventLinkCommand(INDEX_FIRST_EVENT, INDEX_FIRST_CONTACT);
        String expected = EventLinkCommand.class.getCanonicalName()
                + "{eventIndex=" + INDEX_FIRST_EVENT
                + ", contactIndex=" + INDEX_FIRST_CONTACT
                + ", unlinkAll=false" + "}";
        assertEquals(expected, linkCommand.toString());

        EventLinkCommand linkAllCommand = new EventLinkCommand(INDEX_FIRST_EVENT);
        String expectedAll = EventLinkCommand.class.getCanonicalName()
                + "{eventIndex=" + INDEX_FIRST_EVENT
                + ", contactIndex=null"
                + ", unlinkAll=true" + "}";
        assertEquals(expectedAll, linkAllCommand.toString());
    }

}
