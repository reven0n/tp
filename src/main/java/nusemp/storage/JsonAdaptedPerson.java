package nusemp.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import nusemp.commons.exceptions.IllegalValueException;
import nusemp.model.person.Address;
import nusemp.model.person.Email;
import nusemp.model.person.Name;
import nusemp.model.person.Person;
import nusemp.model.person.Phone;
import nusemp.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String email;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String phone;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String address;

    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("email") String email,
            @JsonProperty("phone") String phone, @JsonProperty("address") String address,
            @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        email = source.getEmail().value;
        phone = source.getPhone().value;
        address = source.getAddress().value;
        tags.addAll(source.getTags().stream().map(JsonAdaptedTag::new).toList());
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        Phone modelPhone = Phone.empty();
        if (phone != null) {
            if (!Phone.isValidPhone(phone)) {
                throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
            }
            modelPhone = new Phone(phone);
        }

        Address modelAddress = Address.empty();
        if (address != null) {
            if (!Address.isValidAddress(address)) {
                throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
            }
            modelAddress = new Address(address);
        }

        final Set<Tag> modelTags = new HashSet<>(personTags);

        return new Person(modelName, modelEmail, modelPhone, modelAddress, modelTags);
    }

}
