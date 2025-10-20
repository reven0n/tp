package nusemp.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import nusemp.commons.exceptions.IllegalValueException;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.event.EventDate;
import nusemp.model.event.EventName;

/**
 * Jackson-friendly version of {@link Event}.
 */
class JsonAdaptedEvent {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Event's %s field is missing!";
    public static final String INVALID_PARTICIPANT_EMAIL_MESSAGE = "Participant with email %s not found in address"
            + " book";

    private final String name;
    private final String date;
    private final List<String> participantEmails = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedEvent} with the given event details.
     */
    @JsonCreator
    public JsonAdaptedEvent(@JsonProperty("name") String name,
            @JsonProperty("date") String date,
            @JsonProperty("participantEmails") List<String> participantEmails) {
        this.name = name;
        this.date = date;
        if (participantEmails != null) {
            this.participantEmails.addAll(participantEmails);
        }
    }

    /**
     * Converts a given {@code Event} into this class for Jackson use.
     */
    public JsonAdaptedEvent(Event source) {
        name = source.getName().value;
        date = source.getDate().toString();
        participantEmails.addAll(source.getParticipants().stream()
                .map(contact -> contact.getEmail().value)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted event object into the model's {@code Event} object.
     *
     * @param appData The app data to resolve participant emails from.
     * @throws IllegalValueException if there were any data constraints violated in the adapted event.
     */
    public Event toModelType(ReadOnlyAppData appData) throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    EventName.class.getSimpleName()));
        }
        if (!EventName.isValidEventName(name)) {
            throw new IllegalValueException(EventName.MESSAGE_CONSTRAINTS);
        }
        final EventName modelName = new EventName(name);

        if (date == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    EventDate.class.getSimpleName()));
        }
        if (!EventDate.isValidEventDate(date)) {
            throw new IllegalValueException(EventDate.MESSAGE_CONSTRAINTS);
        }
        final EventDate modelDate = new EventDate(date);

        final List<Contact> modelParticipants = new ArrayList<>();
        for (String email : participantEmails) {
            Contact participant = findContactByEmail(appData, email);
            if (participant == null) {
                throw new IllegalValueException(String.format(INVALID_PARTICIPANT_EMAIL_MESSAGE, email));
            }
            modelParticipants.add(participant);
        }

        return new Event(modelName, modelDate, modelParticipants);
    }

    /**
     * Finds a contact in the app data by email.
     *
     * @param appData The app data to search.
     * @param email The email to search for.
     * @return The contact with the given email, or null if not found.
     */
    private Contact findContactByEmail(ReadOnlyAppData appData, String email) {
        return appData.getContactList().stream()
                .filter(contact -> contact.getEmail().value.equals(email))
                .findFirst()
                .orElse(null);
    }
}
