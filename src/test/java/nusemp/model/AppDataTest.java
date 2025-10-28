package nusemp.model;

import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_ADDRESS_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_TAG_HUSBAND;
import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithoutEvent;
import static nusemp.testutil.TypicalContacts.ALICE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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
import nusemp.model.event.Participant;
import nusemp.testutil.ContactBuilder;
import nusemp.testutil.EventBuilder;

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

    @Test
    public void setContact_validContact_success() {
        appData.addContact(ALICE);
        Contact editedAlice = new ContactBuilder(ALICE)
                .withAddress(VALID_CONTACT_ADDRESS_BOB).build();
        appData.setContact(ALICE, editedAlice);
        assertTrue(appData.hasContact(editedAlice));
        // this is true because contact equality is checked through email
        assertTrue(appData.hasContact(ALICE));
    }

    @Test
    public void removeContact_contactWithEvents_removesFromBothSides() {
        Contact contact = new ContactBuilder().build();
        List<Participant> participants = new ArrayList<>();
        participants.add(new Participant(contact)); // contact is participating twice
        Event event = new EventBuilder().withParticipants(participants).build();
        appData.addContact(contact);
        appData.addEvent(event);

        appData.removeContact(contact);

        assertFalse(appData.hasContact(contact));
        Event updatedEvent = appData.getEventList().get(0);
        assertFalse(updatedEvent.hasContact(contact));
    }

    @Test
    public void setEvent_validEvent_success() {
        Event event = new EventBuilder().build();
        appData.addEvent(event);
        Event editedEvent = new EventBuilder().withName("Edited Event").build();
        appData.setEvent(event, editedEvent);
        assertTrue(appData.hasEvent(editedEvent));
    }

    @Test
    public void removeEvent_existingEvent_success() {
        Event event = new EventBuilder().build();
        appData.addEvent(event);
        assertTrue(appData.hasEvent(event));

        appData.removeEvent(event);
        assertFalse(appData.hasEvent(event));
    }

    @Test
    public void removeContactFromEvents_contactInMultipleEvents_removesFromAll() {
        Contact contact = new ContactBuilder().build();
        List<Participant> participants1 = new ArrayList<>();
        participants1.add(new Participant(contact));
        Event event1 = new EventBuilder().withName("Event 1").withParticipants(participants1).build();

        List<Participant> participants2 = new ArrayList<>();
        participants2.add(new Participant(contact));
        Event event2 = new EventBuilder().withName("Event 2").withParticipants(participants2).build();

        appData.addContact(contact);
        appData.addEvent(event1);
        appData.addEvent(event2);

        appData.removeContact(contact);

        Event updatedEvent1 = appData.getEventList().get(0);
        Event updatedEvent2 = appData.getEventList().get(1);
        assertFalse(updatedEvent1.hasContact(contact));
        assertFalse(updatedEvent2.hasContact(contact));
    }

    @Test
    public void removeContactFromEvents_contactNotInEvents_noChange() {
        Contact contact = new ContactBuilder().build();
        Contact otherContact = new ContactBuilder().withEmail("other@example.com").build();
        List<Participant> participants = new ArrayList<>();
        participants.add(new Participant(otherContact));
        Event event = new EventBuilder().withParticipants(participants).build();

        appData.addContact(contact);
        appData.addContact(otherContact);
        appData.addEvent(event);

        appData.removeContact(contact);

        Event updatedEvent = appData.getEventList().get(0);
        assertTrue(updatedEvent.hasContact(otherContact));
    }

    @Test
    public void setContact_emailChanged_updatesAllEvents() {
        Contact contact = new ContactBuilder().withEmail("old@example.com").build();
        Event event = new EventBuilder().build();

        appData.addContact(contact);
        appData.addEvent(event);

        // Link contact to event
        Event linkedEvent = event.withContact(contact);
        appData.setEvent(event, linkedEvent);

        Contact contactWithEvent = contact.addEvent(linkedEvent);
        appData.setContact(contact, contactWithEvent);

        // Change the email
        Contact editedContact = new ContactBuilder(contactWithEvent)
                .withEmail("new@example.com")
                .withEvents(contactWithEvent.getEvents())
                .build();
        appData.setContact(contactWithEvent, editedContact);

        // Get the updated event from the event list
        ObservableList<Event> eventList = appData.getEventList();
        Event retrievedEvent = eventList.get(0);

        // Verify event has the new email and not the old one
        assertTrue(retrievedEvent.hasContactWithEmail("new@example.com"));
        assertFalse(retrievedEvent.hasContactWithEmail("old@example.com"));
    }

    @Test
    public void setContact_emailUnchanged_eventsNotUpdated() {
        Contact originalContact = new ContactBuilder().withEmail("same@example.com").build();
        Contact editedContact = new ContactBuilder().withEmail("same@example.com")
                .withName("New Name").build();

        List<Participant> participants = new ArrayList<>();
        participants.add(new Participant(originalContact));
        Event event = new EventBuilder().withParticipants(participants).build();

        appData.addContact(originalContact);
        appData.addEvent(event);

        appData.setContact(originalContact, editedContact);

        Event updatedEvent = appData.getEventList().get(0);
        assertTrue(updatedEvent.hasContactWithEmail(editedContact.getEmail().value));
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        assertTrue(appData.equals(appData));
    }

    @Test
    public void equals_null_returnsFalse() {
        assertFalse(appData.equals(null));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        assertFalse(appData.equals(5));
    }

    @Test
    public void equals_differentContacts_returnsFalse() {
        AppData otherAppData = new AppData();
        appData.addContact(ALICE);
        assertFalse(appData.equals(otherAppData));
    }

    @Test
    public void equals_sameContacts_returnsTrue() {
        AppData otherAppData = new AppData();
        appData.addContact(ALICE);
        otherAppData.addContact(ALICE);
        assertTrue(appData.equals(otherAppData));
    }

    @Test
    public void equals_differentEvents_returnsFalse() {
        AppData otherAppData = new AppData();
        Event event = new EventBuilder().build();
        appData.addEvent(event);
        assertFalse(appData.equals(otherAppData));
    }

    @Test
    public void equals_sameContactsAndEvents_returnsTrue() {
        AppData otherAppData = new AppData();
        Event event = new EventBuilder().build();
        appData.addContact(ALICE);
        appData.addEvent(event);
        otherAppData.addContact(ALICE);
        otherAppData.addEvent(event);
        assertTrue(appData.equals(otherAppData));
    }

    @Test
    public void hashCode_sameData_returnsSameHashCode() {
        AppData otherAppData = new AppData();
        appData.addContact(ALICE);
        otherAppData.addContact(ALICE);
        assertEquals(appData.hashCode(), otherAppData.hashCode());
    }

    @Test
    public void hashCode_differentData_returnsDifferentHashCode() {
        AppData otherAppData = new AppData();
        appData.addContact(ALICE);
        assertFalse(appData.hashCode() == otherAppData.hashCode());
    }

    @Test
    public void removeEvent_contactLinkedToEvent_updatesContact() {
        Contact contact = new ContactBuilder().build();
        Event event = new EventBuilder().build();

        appData.addContact(contact);
        appData.addEvent(event);

        // Link contact to event on both sides
        Event linkedEvent = event.withContact(contact);
        appData.setEvent(event, linkedEvent);

        Contact contactWithEvent = contact.addEvent(linkedEvent);
        appData.setContact(contact, contactWithEvent);

        // Remove the event
        appData.removeEvent(linkedEvent);

        // Get the updated contact from the contact list
        ObservableList<Contact> contactList = appData.getContactList();
        Contact retrievedContact = contactList.stream()
                .filter(c -> c.isSameContact(contact))
                .findFirst()
                .orElseThrow();

        // Verify contact no longer has the event
        assertFalse(retrievedContact.getEvents().contains(event));
    }

    @Test
    public void removeEvent_multipleContactsLinked_updatesAllContacts() {
        Contact contact1 = new ContactBuilder().withEmail("c1@example.com").build();
        Contact contact2 = new ContactBuilder().withEmail("c2@example.com").build();
        List<Participant> participants = new ArrayList<>();
        participants.add(new Participant(contact1));
        participants.add(new Participant(contact2));
        Event event = new EventBuilder().withParticipants(participants).build();

        appData.addContact(contact1);
        appData.addContact(contact2);
        appData.addEvent(event);

        appData.removeEvent(event);

        Contact updatedContact1 = appData.getContactList().get(0);
        Contact updatedContact2 = appData.getContactList().get(1);
        assertFalse(updatedContact1.hasEvent(event));
        assertFalse(updatedContact2.hasEvent(event));
    }

    @Test
    public void setContact_contactInMultipleEvents_updatesAllEvents() {
        // Create contact and events
        Contact contact = new ContactBuilder().build();
        Event event1 = new EventBuilder().withName("Event 1").build();
        Event event2 = new EventBuilder().withName("Event 2").build();

        appData.addContact(contact);
        appData.addEvent(event1);
        appData.addEvent(event2);

        // Link contact to both events
        Event updatedEvent1 = event1.withContact(contact);
        Event updatedEvent2 = event2.withContact(contact);
        appData.setEvent(event1, updatedEvent1);
        appData.setEvent(event2, updatedEvent2);

        // Update contact's event list to reflect the links
        Contact contactWithEvents = contact.addEvent(updatedEvent1).addEvent(updatedEvent2);
        appData.setContact(contact, contactWithEvents);

        // Edit the contact
        Contact editedContact = new ContactBuilder(contactWithEvents)
                .withAddress(VALID_CONTACT_ADDRESS_BOB)
                .withEvents(contactWithEvents.getEvents())
                .build();

        appData.setContact(contactWithEvents, editedContact);

        // Get the updated events from the event list
        ObservableList<Event> eventList = appData.getEventList();
        Event retrievedEvent1 = eventList.stream()
                .filter(e -> e.getName().value.equals("Event 1"))
                .findFirst()
                .orElseThrow();
        Event retrievedEvent2 = eventList.stream()
                .filter(e -> e.getName().value.equals("Event 2"))
                .findFirst()
                .orElseThrow();

        // Verify both events have the edited contact
        assertTrue(retrievedEvent1.hasContactWithEmail(editedContact.getEmail().value));
        assertTrue(retrievedEvent2.hasContactWithEmail(editedContact.getEmail().value));

    }

    /**
     * A stub ReadOnlyAppData whose contacts list can violate interface constraints.
     */
    private static class AppDataStub implements ReadOnlyAppData {
        private final ObservableList<Contact> contacts = FXCollections.observableArrayList();
        private final ObservableList<Event> events = FXCollections.observableArrayList();

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
    }

}
