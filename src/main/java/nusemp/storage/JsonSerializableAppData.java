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
import nusemp.model.event.Participant;
import nusemp.model.event.ParticipantStatus;

/**
 * An Immutable AppData that is serializable to JSON format.
 */
@JsonRootName(value = "appdata")
class JsonSerializableAppData {

    public static final String MESSAGE_DUPLICATE_CONTACT = "Contact list contains duplicate contacts.";
    public static final String MESSAGE_DUPLICATE_EVENT = "Event list contains duplicate events.";

    private final List<JsonAdaptedContact> contacts = new ArrayList<>();
    private final List<JsonAdaptedEvent> events = new ArrayList<>();
    private final List<JsonAdaptedParticipantMapping> participantMappings = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAppData} with the given contacts and events.
     */
    @JsonCreator
    public JsonSerializableAppData(@JsonProperty("contacts") List<JsonAdaptedContact> contacts,
            @JsonProperty("events") List<JsonAdaptedEvent> events,
            @JsonProperty("participantMappings") List<JsonAdaptedParticipantMapping> participantMappings) {
        this.contacts.addAll(contacts);
        if (events != null) {
            this.events.addAll(events);
        }
        if (participantMappings != null) {
            this.participantMappings.addAll(participantMappings);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAppData} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAppData}.
     */
    public JsonSerializableAppData(ReadOnlyAppData source) {
        contacts.addAll(source.getContactList().stream().map(JsonAdaptedContact::new).toList());
        events.addAll(source.getEventList().stream().map(JsonAdaptedEvent::new).toList());

        // Serialize participant mappings
        for (Contact contact : source.getContactList()) {
            List<Event> contactEvents = source.getEventsForContact(contact);
            for (Event event : contactEvents) {
                ParticipantStatus status = source.getParticipantStatus(contact, event);
                participantMappings.add(new JsonAdaptedParticipantMapping(
                        contact.getEmail().value,
                        event.getName().value,
                        status.toString()));
            }
        }
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

        // Load events after contacts (needed for participant resolution)
        for (JsonAdaptedEvent jsonAdaptedEvent : events) {
            Event event = jsonAdaptedEvent.toModelType(appData);
            if (appData.hasEvent(event)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_EVENT);
            }
            appData.addEvent(event);
        }

        // Populate contact event lists based on event participants
        populateContactEventLists(appData);

        return appData;
    }

    /**
     * Populates the event lists in contacts based on event participants.
     * For each event, adds the event to all its participants' event lists.
     */
    private void populateContactEventLists(AppData appData) {
        // Reconstruct participant mappings
        for (JsonAdaptedParticipantMapping mapping : participantMappings) {
            Contact contact = findContactByEmail(appData, mapping.getContactEmail());
            Event event = findEventByName(appData, mapping.getEventName());
            ParticipantStatus status = ParticipantStatus.fromString(mapping.getStatus());

            if (contact != null && event != null) {
                appData.addParticipantEvent(contact, event, status);
            }
        }
    }

    private Contact findContactByEmail(AppData appData, String email) {
        return appData.getContactList().stream()
                .filter(c -> c.getEmail().value.equals(email))
                .findFirst()
                .orElse(null);
    }

    private Event findEventByName(AppData appData, String name) {
        return appData.getEventList().stream()
                .filter(e -> e.getName().value.equals(name))
                .findFirst()
                .orElse(null);
    }

}
