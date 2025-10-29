package nusemp.model;

import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_ADDRESS_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_TAG_HUSBAND;
import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithoutEvent;
import static nusemp.testutil.TypicalContacts.ALICE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import nusemp.model.contact.Contact;
import nusemp.model.contact.exceptions.DuplicateContactException;
import nusemp.model.event.Event;
import nusemp.model.participant.Participant;
import nusemp.model.participant.ParticipantMap;
import nusemp.model.participant.ReadOnlyParticipantMap;
import nusemp.testutil.ContactBuilder;

public class AppDataTest {

    private final AppData appData = new AppData();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), appData.getContactList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> appData.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAppData_replacesData() {
        AppData newData = getTypicalAppDataWithoutEvent();
        appData.resetData(newData);
        assertEquals(newData, appData);
    }

    @Test
    public void resetData_withDuplicateContacts_throwsDuplicateContactException() {
        // Two contacts with the same identity fields
        Contact editedAlice = new ContactBuilder(ALICE).withAddress(VALID_CONTACT_ADDRESS_BOB)
                .withTags(VALID_CONTACT_TAG_HUSBAND)
                .build();
        List<Contact> newContacts = Arrays.asList(ALICE, editedAlice);
        AppDataStub newData = new AppDataStub(newContacts);

        assertThrows(DuplicateContactException.class, () -> appData.resetData(newData));
    }

    @Test
    public void hasContact_nullContact_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> appData.hasContact(null));
    }

    @Test
    public void hasContact_contactNotInAppData_returnsFalse() {
        assertFalse(appData.hasContact(ALICE));
    }

    @Test
    public void hasContact_contactInAppData_returnsTrue() {
        appData.addContact(ALICE);
        assertTrue(appData.hasContact(ALICE));
    }

    @Test
    public void hasContact_contactWithSameIdentityFieldsInAppData_returnsTrue() {
        appData.addContact(ALICE);
        Contact editedAlice = new ContactBuilder(ALICE).withAddress(VALID_CONTACT_ADDRESS_BOB)
                .withTags(VALID_CONTACT_TAG_HUSBAND)
                .build();
        assertTrue(appData.hasContact(editedAlice));
    }

    @Test
    public void getContactList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> appData.getContactList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AppData.class.getCanonicalName() + "{contacts=" + appData.getContactList()
                + ", events=" + appData.getEventList() + "}";
        assertEquals(expected, appData.toString());
    }

    /**
     * A stub ReadOnlyAppData whose contacts list can violate interface constraints.
     */
    private static class AppDataStub implements ReadOnlyAppData {
        private final ObservableList<Contact> contacts = FXCollections.observableArrayList();
        private final ObservableList<Event> events = FXCollections.observableArrayList();
        private final ParticipantMap participantMap = new ParticipantMap();

        AppDataStub(Collection<Contact> contacts) {
            this.contacts.setAll(contacts);
        }

        @Override
        public ObservableList<Contact> getContactList() {
            return contacts;
        }

        @Override
        public ObservableList<Event> getEventList() {
            return events;
        }

        @Override
        public ReadOnlyParticipantMap getParticipantMap() {
            return participantMap;
        }

        @Override
        public List<Participant> getParticipants(Event event) {
            return List.of();
        }

        @Override
        public List<Participant> getParticipants(Contact contact) {
            return List.of();
        }
    }

}
