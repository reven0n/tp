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

    public static final String HEADING_CONTACTS = "Contacts";
    public static final String HEADING_CONTACTS_NONE = "No Contacts!";
    public static final String HEADING_EVENTS = "Events";
    public static final String HEADING_EVENTS_NONE = "No Events";
    public static final String HEADING_CONTACTS_FROM_EVENT = "Contacts in event \"%1$s\"";
    public static final String HEADING_CONTACTS_FROM_EVENT_NONE = "No contacts in event \"%1$s\"";
    public static final String HEADING_CONTACT_FIND = "Contacts that match conditions:\n%1$s";
    public static final String HEADING_CONTACT_FIND_NONE = "No contacts that match conditions:\n%1$s";
    public static final String HEADING_EVENTS_FROM_CONTACT = "Events containing contact \"%1$s\"";
    public static final String HEADING_EVENTS_FROM_CONTACT_NONE = "No events containing contact \"%1$s\"";
    public static final String HEADING_EVENT_FIND = "Events that match conditions:\n%1$s";
    public static final String HEADING_EVENT_FIND_NONE = "No events that match conditions:\n%1$s";

    public static final String MESSAGE_WELCOME = "Welcome to NUS Event Mailer Pro!\n"
            + "Type \"help\" to open the user guide.";
    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command!";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n\n%1$s";
    public static final String MESSAGE_INVALID_INDEX_FORMAT =
        "Invalid index. Index must be a positive integer within the displayed list range.";
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
     * Formats the {@code contact} for display to the user.<br>
     * <b>Warning</b>: The string spans over multiple lines, so it should not be used for short messages.
     */
    public static String format(Contact contact) {
        final StringBuilder builder = new StringBuilder("⌜\n  Name: ");
        builder.append(contact.getName())
                .append("\n  Email: ")
                .append(contact.getEmail());

        if (contact.hasPhone()) {
            builder.append("\n  Phone: ").append(contact.getPhone());
        }

        if (contact.hasAddress()) {
            builder.append("\n  Address: ").append(contact.getAddress());
        }

        if (contact.hasTags()) {
            builder.append("\n  Tags: ");
            contact.getTags().forEach(t -> builder.append(t).append(" "));
        }

        builder.append("\n⌞");
        return builder.toString();
    }

    /**
     * Formats the {@code event} for display to the user.
     */
    public static String format(Event event) {
        final StringBuilder builder = new StringBuilder("⌜\n  Name: ");
        builder.append(event.getName())
                .append("\n  Status: ")
                .append(event.getStatus().toCapitalizedString())
                .append("\n  Date: ")
                .append(event.getDate());

        if (event.hasAddress()) {
            builder.append("\n  Address: ").append(event.getAddress());
        }

        if (event.hasTags()) {
            builder.append("\n  Tags: ");
            event.getTags().forEach(t -> builder.append(t).append(" "));
        }

        builder.append("\n⌞");
        return builder.toString();
    }

}
