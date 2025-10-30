package nusemp.logic;

import static nusemp.logic.Messages.MESSAGE_DUPLICATE_FIELDS;
import static nusemp.testutil.TypicalContacts.ALICE;
import static nusemp.testutil.TypicalContacts.GEORGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import nusemp.logic.parser.Prefix;
import nusemp.model.event.Event;
import nusemp.model.event.EventStatus;
import nusemp.model.fields.Address;
import nusemp.model.fields.Date;
import nusemp.model.fields.Name;
import nusemp.model.fields.Tag;

class MessagesTest {

    @Test
    void getErrorMessageForDuplicatePrefixes_duplicatePrefix_returnsErrorMessage() {
        Prefix prefix1 = new Prefix("p/");
        Prefix prefix2 = new Prefix("q/");
        String result = Messages.getErrorMessageForDuplicatePrefixes(prefix1, prefix2);
        String expected = MESSAGE_DUPLICATE_FIELDS + prefix1 + " " + prefix2;
        assertEquals(expected, result);
    }

    @Test
    void format_contactWithRequiredFields_returnsFormattedContact() {
        String result = Messages.format(GEORGE);
        String expected = String.format("%s; Email: %s", GEORGE.getName(), GEORGE.getEmail());
        assertEquals(expected, result);
    }

    @Test
    void format_contactWithAllFields_returnsFormattedContact() {
        String result = Messages.format(ALICE);
        String expected = String.format("%s; Email: %s; Phone: %s; Address: %s; Tags: %s",
                ALICE.getName(), ALICE.getEmail(), ALICE.getPhone(), ALICE.getAddress(),
                ALICE.getTags().stream().map(Tag::toString).reduce("", (a, b) -> a + b));
        assertEquals(expected, result);
    }

    @Test
    void format_eventWithRequiredFields_returnsFormattedEvent() {
        Name name = new Name("Meeting");
        Date date = new Date("01-10-2025 14:00");
        Event event = new Event(name, date, Address.empty());
        String result = Messages.format(event);
        String expected = String.format("%s; Date: %s", event.getName(), event.getDate());
        assertEquals(expected, result);
    }

    @Test
    void format_eventWithAllFields_returnsFormattedEvent() {
        Name name = new Name("Conference");
        Date date = new Date("15-11-2025 09:00");
        Address address = new Address("123 Main St");
        Set<Tag> tags = Set.of(new Tag("Work"), new Tag("Important"));
        Event event = new Event(name, date, address, EventStatus.STARTING, tags);
        String result = Messages.format(event);
        String expected = String.format("%s; Date: %s; Address: %s; Tags: %s",
                event.getName(), event.getDate(), event.getAddress(),
                event.getTags().stream().map(Tag::toString).reduce("", (a, b) -> a + b));
        assertEquals(expected, result);
    }
}
