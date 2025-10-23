package nusemp.logic;

import java.util.List;
import java.util.stream.Stream;

import nusemp.logic.parser.Prefix;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX = "The contact index provided is invalid";
    public static final String MESSAGE_CONTACTS_LISTED_OVERVIEW = "%1$d contact(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    // Event-related messages
    public static final String MESSAGE_INVALID_EVENT_DISPLAYED_INDEX = "The event index provided is invalid";
    public static final String MESSAGE_EVENTS_LISTED_OVERVIEW = "%1$d event(s) listed!";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        List<String> duplicateFields = Stream.of(duplicatePrefixes).distinct().map(Prefix::toString).toList();

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code contact} for display to the user.
     */
    public static String format(Contact contact) {
        final StringBuilder builder = new StringBuilder();
        builder.append(contact.getName())
                .append("; Email: ")
                .append(contact.getEmail());

        if (contact.hasPhone()) {
            builder.append("; Phone: ").append(contact.getPhone());
        }

        if (contact.hasAddress()) {
            builder.append("; Address: ").append(contact.getAddress());
        }

        if (contact.hasTags()) {
            builder.append("; Tags: ");
            contact.getTags().forEach(builder::append);
        }

        return builder.toString();
    }

    /**
     * Formats the {@code event} for display to the user.
     */
    public static String format(Event event) {
        final StringBuilder builder = new StringBuilder();
        builder.append(event.getName())
                .append("; Date: ")
                .append(event.getDate());

        if (event.hasAddress()) {
            builder.append("; Address: ").append(event.getAddress());
        }

        if (event.hasTags()) {
            builder.append("; Tags: ");
            event.getTags().forEach(builder::append);
        }

        return builder.toString();
    }

}
