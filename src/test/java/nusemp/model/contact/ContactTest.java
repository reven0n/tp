package nusemp.model.contact;

import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_ADDRESS_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_EMAIL_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_NAME_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_PHONE_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_TAG_HUSBAND;
import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalContacts.ALICE;
import static nusemp.testutil.TypicalContacts.BOB;
import static nusemp.testutil.TypicalEvents.MEETING_EMPTY;
import static nusemp.testutil.TypicalEvents.MEETING_WITH_TAGS_FILLED;
import static nusemp.testutil.TypicalEvents.WORKSHOP_FILLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import nusemp.model.event.Event;
import nusemp.testutil.ContactBuilder;
import nusemp.testutil.EventBuilder;

public class ContactTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Contact contact = new ContactBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> contact.getTags().remove(0));
    }

    @Test
    public void isSameContact() {
        // same object -> returns true
        assertTrue(ALICE.isSameContact(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSameContact(null));

        // same email, all other attributes different -> returns true
        Contact editedAlice = new ContactBuilder(ALICE).withPhone(VALID_CONTACT_PHONE_BOB)
                .withName(VALID_CONTACT_NAME_BOB)
                .withAddress(VALID_CONTACT_ADDRESS_BOB).withTags(VALID_CONTACT_TAG_HUSBAND).build();
        assertTrue(ALICE.isSameContact(editedAlice));

        // different email, all other attributes same -> returns false
        editedAlice = new ContactBuilder(ALICE).withEmail(VALID_CONTACT_EMAIL_BOB).build();
        assertFalse(ALICE.isSameContact(editedAlice));

        // email differs in case, all other attributes same -> returns true
        Contact editedBob = new ContactBuilder(BOB).withEmail(VALID_CONTACT_EMAIL_BOB.toUpperCase()).build();
        assertTrue(BOB.isSameContact(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Contact aliceCopy = new ContactBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different contact -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Contact editedAlice = new ContactBuilder(ALICE).withName(VALID_CONTACT_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new ContactBuilder(ALICE).withPhone(VALID_CONTACT_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new ContactBuilder(ALICE).withEmail(VALID_CONTACT_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new ContactBuilder(ALICE).withAddress(VALID_CONTACT_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new ContactBuilder(ALICE).withTags(VALID_CONTACT_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));

        Event editedMeetingDate = new EventBuilder(MEETING_WITH_TAGS_FILLED).withDate("13-01-2013 13:00").build();
        Contact aliceWithMeeting = new ContactBuilder(ALICE).withEvents(List.of(MEETING_EMPTY)).build();
        Contact aliceWithEditedMeetingDate = new ContactBuilder(ALICE)
                .withEvents(List.of(editedMeetingDate)).build();
        Contact aliceWithEditedMeetingName = new ContactBuilder(ALICE)
                .withEvents(List.of(new EventBuilder(MEETING_EMPTY).withName("Project Discussion").build())).build();

        assertNotEquals(ALICE, aliceWithMeeting);
        assertNotEquals(aliceWithEditedMeetingName, aliceWithMeeting);
        assertEquals(aliceWithEditedMeetingDate, aliceWithMeeting);

        // same events but different order -> returns true
        Contact alice1 = new ContactBuilder(ALICE)
                .withEvents(List.of(MEETING_EMPTY, WORKSHOP_FILLED)).build();
        Contact alice2 = new ContactBuilder(ALICE)
                .withEvents(List.of(WORKSHOP_FILLED, MEETING_EMPTY)).build();
        assertEquals(alice1, alice2);
    }

    @Test
    public void toStringMethod() {
        String expected = Contact.class.getCanonicalName() + "{name=" + ALICE.getName() + ", email=" + ALICE.getEmail()
                + ", phone=" + ALICE.getPhone() + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags()
                + ", events=" + ALICE.getEvents() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
