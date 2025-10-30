package nusemp.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import nusemp.commons.exceptions.IllegalValueException;
import nusemp.model.AppData;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.participant.Participant;

/**
 * An Immutable AppData that is serializable to JSON format.
 */
@JsonRootName(value = "appdata")
class JsonSerializableAppData {

    public static final String MESSAGE_DUPLICATE_CONTACT = "Contact list contains duplicate contacts.";
    public static final String MESSAGE_DUPLICATE_EVENT = "Event list contains duplicate events.";

    private final List<JsonAdaptedContact> contacts = new ArrayList<>();
    private final List<JsonAdaptedEvent> events = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAppData} with the given contacts and events.
     */
    @JsonCreator
    public JsonSerializableAppData(@JsonProperty("contacts") List<JsonAdaptedContact> contacts,
            @JsonProperty("events") List<JsonAdaptedEvent> events) {
        this.contacts.addAll(contacts);
        if (events != null) {
            this.events.addAll(events);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAppData} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAppData}.
     */
    public JsonSerializableAppData(ReadOnlyAppData source) {
        contacts.addAll(source.getContactList().stream().map(JsonAdaptedContact::new).toList());
        events.addAll(source.getEventList().stream()
                .map(event -> new JsonAdaptedEvent(event, source::getParticipants)).toList());
    }

    /**
     * Converts this app data into the model's {@code AppData} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AppData toModelType() throws IllegalValueException {
        AppData appData = new AppData();

        // Load contacts first
        for (JsonAdaptedContact jsonAdaptedContact : contacts) {
            Contact contact = jsonAdaptedContact.toModelType();
            if (appData.hasContact(contact)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_CONTACT);
            }
            appData.addContact(contact);
        }

        // Load events and add participants
        for (JsonAdaptedEvent jsonAdaptedEvent : events) {
            Event event = jsonAdaptedEvent.toModelType();
            List<Participant> participants = jsonAdaptedEvent.getParticipants(appData, event);
            if (appData.hasEvent(event)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_EVENT);
            }
            appData.addEvent(event);
            for (Participant participant : participants) {
                appData.addParticipant(participant.getContact(), event, participant.getStatus());
            }
        }

        return appData;
    }

}
