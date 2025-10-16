package nusemp.logic;

import static nusemp.logic.Messages.MESSAGE_DUPLICATE_FIELDS;
import static nusemp.testutil.TypicalPersons.ALICE;
import static nusemp.testutil.TypicalPersons.GEORGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import nusemp.logic.parser.Prefix;
import nusemp.model.event.Event;
import nusemp.model.event.EventDate;
import nusemp.model.event.EventName;
import nusemp.model.tag.Tag;

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
    void format_personWithAllFields_returnsFormattedPerson() {
        String result = Messages.format(ALICE);
        String expected = String.format("%s; Email: %s; Phone: %s; Address: %s; Tags: %s",
                ALICE.getName(), ALICE.getEmail(), ALICE.getPhone(), ALICE.getAddress(),
                ALICE.getTags().stream().map(Tag::toString).reduce("", (a, b) -> a + b));
        assertEquals(expected, result);
    }

    @Test
    void format_personWithNoOptionalFields_returnsFormattedPerson() {
        String result = Messages.format(GEORGE);
        String expected = String.format("%s; Email: %s", GEORGE.getName(), GEORGE.getEmail());
        assertEquals(expected, result);
    }

    @Test
    void format_event_returnsFormattedEvent() {
        EventName name = new EventName("Meeting");
        EventDate date = new EventDate("01-10-2025 14:00");
        Event event = new Event(name, date);
        String result = Messages.format(event);
        String expected = String.format("%s; Date: %s", event.getName(), event.getDate());
        assertEquals(expected, result);
    }
}
