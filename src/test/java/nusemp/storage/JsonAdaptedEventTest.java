package nusemp.storage;

import static nusemp.storage.JsonAdaptedEvent.MISSING_FIELD_MESSAGE_FORMAT;
import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithoutEvent;
import static nusemp.testutil.TypicalEvents.MEETING_FILLED;
import static nusemp.testutil.TypicalEvents.MEETING_WITH_TAGS;
import static nusemp.testutil.TypicalEvents.MEETING_WITH_TAGS_FILLED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import nusemp.commons.exceptions.IllegalValueException;
import nusemp.model.AppData;
import nusemp.model.event.Event;
import nusemp.model.event.ParticipantStatus;
import nusemp.model.fields.Address;
import nusemp.model.fields.Date;
import nusemp.model.fields.Email;
import nusemp.model.fields.Name;
import nusemp.testutil.EventBuilder;
import nusemp.testutil.TypicalContacts;

class JsonAdaptedEventTest {

    private static final String INVALID_NAME = " ";
    private static final String INVALID_DATE = "invalid-date";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_TAG = "Music&";
    private static final String INVALID_STATUS = "INVALID_STATUS";
    private static final JsonAdaptedParticipant INVALID_PARTICIPANT_EMAIL =
            new JsonAdaptedParticipant("invalid-email", "ATTENDING");

    private static final String VALID_NAME = MEETING_FILLED.getName().value;
    private static final String VALID_DATE = MEETING_FILLED.getDate().toString();
    private static final String VALID_ADDRESS = MEETING_FILLED.getAddress().value;
    private static final String VALID_STATUS = MEETING_FILLED.getStatus().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = MEETING_WITH_TAGS.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    private static final List<JsonAdaptedParticipant> VALID_PARTICIPANT_STATUS =
            TypicalContacts.getTypicalContacts().stream().map(c -> new JsonAdaptedParticipant(
                    c.getEmail().value, ParticipantStatus.AVAILABLE.toString())).toList();

    @Test
    public void toModelType_validEventDetails_returnsEvent() throws Exception {
        JsonAdaptedEvent event = new JsonAdaptedEvent(MEETING_FILLED);
        assertEquals(MEETING_FILLED, event.toModelType(getTypicalAppDataWithoutEvent()));
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedEvent event = new JsonAdaptedEvent(null, VALID_DATE, VALID_ADDRESS, VALID_STATUS, VALID_TAGS,
                VALID_PARTICIPANT_STATUS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, () ->
                event.toModelType(getTypicalAppDataWithoutEvent()));
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedEvent event =
                new JsonAdaptedEvent(INVALID_NAME, VALID_DATE, VALID_ADDRESS, VALID_STATUS, VALID_TAGS,
                        VALID_PARTICIPANT_STATUS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, () ->
                event.toModelType(getTypicalAppDataWithoutEvent()));
    }

    @Test
    public void toModelType_nullDate_throwsIllegalValueException() {
        JsonAdaptedEvent event = new JsonAdaptedEvent(VALID_NAME, null, VALID_ADDRESS, VALID_STATUS, VALID_TAGS,
                VALID_PARTICIPANT_STATUS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Date.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, () ->
                event.toModelType(getTypicalAppDataWithoutEvent()));
    }

    @Test
    public void toModelType_invalidDate_throwsIllegalValueException() {
        JsonAdaptedEvent event =
                new JsonAdaptedEvent(VALID_NAME, INVALID_DATE, VALID_ADDRESS, VALID_STATUS, VALID_TAGS,
                        VALID_PARTICIPANT_STATUS);
        String expectedMessage = Date.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, () ->
                event.toModelType(getTypicalAppDataWithoutEvent()));
    }

    @Test
    public void toModelType_nullAddress_returnsEvent() throws Exception {
        JsonAdaptedEvent event = new JsonAdaptedEvent(VALID_NAME, VALID_DATE, null, VALID_STATUS, VALID_TAGS,
                VALID_PARTICIPANT_STATUS);
        Event expectedEvent = new EventBuilder(MEETING_WITH_TAGS_FILLED).withoutAddress().build();
        assertEquals(expectedEvent, event.toModelType(getTypicalAppDataWithoutEvent()));
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedEvent event =
                new JsonAdaptedEvent(VALID_NAME, VALID_DATE, INVALID_ADDRESS, VALID_STATUS, VALID_TAGS,
                        VALID_PARTICIPANT_STATUS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, () ->
                event.toModelType(getTypicalAppDataWithoutEvent()));
    }

    @Test
    public void toModelType_invalidParticipantEmail_throwsIllegalValueException() {
        List<JsonAdaptedParticipant> participants = new ArrayList<>(VALID_PARTICIPANT_STATUS);
        participants.add(INVALID_PARTICIPANT_EMAIL);
        JsonAdaptedEvent event = new JsonAdaptedEvent(VALID_NAME, VALID_DATE, VALID_ADDRESS, VALID_STATUS, VALID_TAGS,
                participants);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, () ->
                event.toModelType(getTypicalAppDataWithoutEvent()));
    }

    @Test
    public void toModelType_missingParticipantEmail_throwsIllegalValueException() {
        JsonAdaptedEvent event = new JsonAdaptedEvent(VALID_NAME, VALID_DATE, VALID_ADDRESS, VALID_STATUS, VALID_TAGS,
                VALID_PARTICIPANT_STATUS);
        String expectedMessage =
                String.format(JsonAdaptedEvent.MISSING_PARTICIPANT_EMAIL_MESSAGE,
                        VALID_PARTICIPANT_STATUS.get(0).getEmail());
        assertThrows(IllegalValueException.class, expectedMessage, () -> event.toModelType(new AppData()));
    }

    @Test
    public void toModelType_validEventWithTags_returnsEvent() throws Exception {
        JsonAdaptedEvent event = new JsonAdaptedEvent(MEETING_WITH_TAGS);
        assertEquals(MEETING_WITH_TAGS, event.toModelType(getTypicalAppDataWithoutEvent()));
    }

    @Test
    public void toModelType_invalidTag_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>();
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedEvent event = new JsonAdaptedEvent(VALID_NAME, VALID_DATE, VALID_ADDRESS, VALID_STATUS, invalidTags,
                VALID_PARTICIPANT_STATUS);
        assertThrows(IllegalValueException.class, () -> event.toModelType(getTypicalAppDataWithoutEvent()));
    }

    @Test
    public void toModelType_emptyTags_returnsEvent() throws Exception {
        List<JsonAdaptedTag> emptyTags = new ArrayList<>();
        JsonAdaptedEvent event = new JsonAdaptedEvent(VALID_NAME, VALID_DATE, VALID_ADDRESS, VALID_STATUS, emptyTags,
                VALID_PARTICIPANT_STATUS);
        Event expectedEvent = new EventBuilder(MEETING_FILLED).withTags().build();
        assertEquals(expectedEvent, event.toModelType(getTypicalAppDataWithoutEvent()));
    }

    @Test
    public void toModelType_nullStatus_defaultsToStarting() throws Exception {
        JsonAdaptedEvent event = new JsonAdaptedEvent(VALID_NAME, VALID_DATE, VALID_ADDRESS, null, VALID_TAGS,
                VALID_PARTICIPANT_STATUS);
        Event result = event.toModelType(getTypicalAppDataWithoutEvent());
        assertEquals(nusemp.model.event.EventStatus.STARTING, result.getStatus());
    }

    @Test
    public void toModelType_invalidStatus_throwsIllegalValueException() {
        JsonAdaptedEvent event = new JsonAdaptedEvent(VALID_NAME, VALID_DATE, VALID_ADDRESS, INVALID_STATUS,
                VALID_TAGS, VALID_PARTICIPANT_STATUS);
        assertThrows(IllegalValueException.class, () -> event.toModelType(getTypicalAppDataWithoutEvent()));
    }

    @Test
    public void toModelType_validStatus_returnsEvent() throws Exception {
        JsonAdaptedEvent event = new JsonAdaptedEvent(VALID_NAME, VALID_DATE, VALID_ADDRESS, "ongoing",
                VALID_TAGS, VALID_PARTICIPANT_STATUS);
        Event result = event.toModelType(getTypicalAppDataWithoutEvent());
        assertEquals(nusemp.model.event.EventStatus.ONGOING, result.getStatus());
    }
}
