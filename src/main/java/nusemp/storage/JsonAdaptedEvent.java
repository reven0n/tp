package nusemp.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import nusemp.commons.exceptions.IllegalValueException;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.event.EventStatus;
import nusemp.model.event.Participant;
import nusemp.model.event.ParticipantStatus;
import nusemp.model.fields.Address;
import nusemp.model.fields.Date;
import nusemp.model.fields.Email;
import nusemp.model.fields.Name;
import nusemp.model.fields.Tag;

/**
 * Jackson-friendly version of {@link Event}.
 */
class JsonAdaptedEvent {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Event's %s field is missing!";
    public static final String MISSING_PARTICIPANT_EMAIL_MESSAGE =
            "Participant with email %s not found in contact list";
    public static final String INVALID_PARTICIPANT_STATUS_MESSAGE =
            "Participant status is invalid for participant with email %s";

    private final String name;
    private final String date;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String address;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String status;

    private final List<JsonAdaptedParticipant> participants = new ArrayList<>();

    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedEvent} with the given event details.
     */
    @JsonCreator
    public JsonAdaptedEvent(@JsonProperty("name") String name, @JsonProperty("date") String date,
            @JsonProperty("address") String address, @JsonProperty("status") String status,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("participants") List<JsonAdaptedParticipant> participants) {
        this.name = name;
        this.date = date;
        this.address = address;
        this.status = status;
        if (participants != null) {
            this.participants.addAll(participants);
        }
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Event} into this class for Jackson use.
     */
    public JsonAdaptedEvent(Event source) {
        name = source.getName().value;
        date = source.getDate().toString();
        address = source.getAddress().value;
        status = source.getStatus().toString();
        participants.addAll(source.getParticipants().stream()
                .map(participant -> new JsonAdaptedParticipant(
                        participant.getContact().getEmail().value, participant.getStatus().toString()))
                .collect(Collectors.toList()));
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .toList());
    }

    /**
     * Converts this Jackson-friendly adapted event object into the model's {@code Event} object.
     *
     * @param appData The app data to resolve participant emails from.
     * @throws IllegalValueException if there were any data constraints violated in the adapted event.
     */
    public Event toModelType(ReadOnlyAppData appData) throws IllegalValueException {
        final List<Tag> eventTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            eventTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (date == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    Date.class.getSimpleName()));
        }
        if (!Date.isValidDate(date)) {
            throw new IllegalValueException(Date.MESSAGE_CONSTRAINTS);
        }
        final Date modelDate = new Date(date);

        Address modelAddress = Address.empty();
        if (address != null) {
            if (!Address.isValidAddress(address)) {
                throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
            }
            modelAddress = new Address(address);
        }

        // Default to STARTING if status is not provided (backward compatibility)
        EventStatus modelStatus = EventStatus.STARTING;
        if (status != null) {
            if (!EventStatus.isValidEventStatus(status)) {
                throw new IllegalValueException(EventStatus.MESSAGE_CONSTRAINTS);
            }
            modelStatus = EventStatus.fromString(status);
        }

        final List<Participant> modelParticipants = new ArrayList<>();
        for (JsonAdaptedParticipant participant : participants) {
            String email = participant.getEmail();
            String participantStatusStr = participant.getStatus();
            if (!Email.isValidEmail(email)) {
                throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
            }
            Contact contact = findContactByEmail(appData, email).orElseThrow(() ->
                    new IllegalValueException(String.format(MISSING_PARTICIPANT_EMAIL_MESSAGE, email)));

            if (!ParticipantStatus.isValidStatus(participantStatusStr)) {
                throw new IllegalValueException(String.format(INVALID_PARTICIPANT_STATUS_MESSAGE, email));
            }

            ParticipantStatus status = ParticipantStatus.fromString(participantStatusStr);
            modelParticipants.add(new Participant(contact, status));
        }

        final Set<Tag> modelTags = new HashSet<>(eventTags);

        return new Event(modelName, modelDate, modelAddress, modelStatus, modelTags, modelParticipants);
    }

    /**
     * Finds a contact in the app data by email.
     *
     * @param appData The app data to search.
     * @param email The email to search for.
     * @return an {@code Optional} containing the contact if found, otherwise {@code Optional.empty()}
     */
    private Optional<Contact> findContactByEmail(ReadOnlyAppData appData, String email) {
        return appData.getContactList().stream()
                .filter(contact -> contact.getEmail().value.equals(email))
                .findFirst();
    }
}
