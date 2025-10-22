package nusemp.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.util.Pair;

import nusemp.commons.exceptions.IllegalValueException;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.contact.Contact;
import nusemp.model.event.ContactStatus;
import nusemp.model.event.Event;
import nusemp.model.event.Status;
import nusemp.model.event.exceptions.InvalidStatusException;
import nusemp.model.fields.Address;
import nusemp.model.fields.Date;
import nusemp.model.fields.Email;
import nusemp.model.fields.Name;

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

    private final List<Pair<String, String>> partcipantStatuses = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedEvent} with the given event details.
     */
    @JsonCreator
    public JsonAdaptedEvent(@JsonProperty("name") String name, @JsonProperty("date") String date,
            @JsonProperty("address") String address,
            @JsonProperty("participantStatuses") List<Pair<String, String>> participantStatuses) {
        this.name = name;
        this.date = date;
        this.address = address;
        if (participantStatuses != null) {
            this.partcipantStatuses.addAll(participantStatuses);
        }
    }

    /**
     * Converts a given {@code Event} into this class for Jackson use.
     */
    public JsonAdaptedEvent(Event source) {
        name = source.getName().value;
        date = source.getDate().toString();
        address = source.getAddress().value;
        partcipantStatuses.addAll(source.getParticipants().stream()
                .map(status -> new Pair<>(
                        status.getContact().getEmail().value, status.getStatus().toString()))
                .toList());
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

        final List<ContactStatus> modelParticipants = new ArrayList<>();
        for (Pair<String, String> contactStatus : partcipantStatuses) {
            String email = contactStatus.getKey();
            String statusStr = contactStatus.getValue();
            if (!Email.isValidEmail(email)) {
                throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
            }
            Contact participant = findContactByEmail(appData, email).orElseThrow(() ->
                    new IllegalValueException(String.format(MISSING_PARTICIPANT_EMAIL_MESSAGE, email)));
            try {
                Status status = Status.convertStringToStatus(statusStr);
                modelParticipants.add(new ContactStatus(participant, status));
            } catch (InvalidStatusException e) {
                throw new IllegalValueException(String.format(INVALID_PARTICIPANT_STATUS_MESSAGE, email));
            }
        }

        return new Event(modelName, modelDate, modelAddress, modelParticipants);
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
