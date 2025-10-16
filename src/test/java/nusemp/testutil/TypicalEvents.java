package nusemp.testutil;

import nusemp.model.event.Event;
import nusemp.model.event.EventDate;
import nusemp.model.event.EventName;

/**
 * A utility class containing a list of {@code Event} objects to be used in tests.
 */
public class TypicalEvents {
    public static final Event MEETING_EMPTY = new Event(
            new EventName("MEETING"), new EventDate("01-10-2025 14:00"));
    public static final Event MEETING_FILLED = new Event(
            new EventName("MEETING"), new EventDate("01-10-2025 14:00"), TypicalPersons.getTypicalPersons());
    public static final Event CONFERENCE_EMPTY = new Event(
            new EventName("CONFERENCE"), new EventDate("29-02-2024 09:00"));
    public static final Event CONFERENCE_FILLED = new Event(
            new EventName("CONFERENCE"), new EventDate("29-02-2024 09:00"), TypicalPersons.getTypicalPersons());
}
