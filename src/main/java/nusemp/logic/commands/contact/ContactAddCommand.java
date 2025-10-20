package nusemp.logic.commands.contact;

import static java.util.Objects.requireNonNull;
import static nusemp.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static nusemp.logic.parser.CliSyntax.PREFIX_EMAIL;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;
import static nusemp.logic.parser.CliSyntax.PREFIX_PHONE;
import static nusemp.logic.parser.CliSyntax.PREFIX_TAG;

import nusemp.commons.util.ToStringBuilder;
import nusemp.logic.Messages;
import nusemp.logic.commands.Command;
import nusemp.logic.commands.CommandResult;
import nusemp.logic.commands.CommandType;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.model.Model;
import nusemp.model.fields.Contact;

/**
 * Adds a contact.
 */
public class ContactAddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = CommandType.CONTACT + " " + COMMAND_WORD
            + ": Adds a contact. "
            + "Parameters: "
            + PREFIX_NAME + " NAME "
            + PREFIX_EMAIL + " EMAIL "
            + "[" + PREFIX_PHONE + " PHONE] "
            + "[" + PREFIX_ADDRESS + " ADDRESS] "
            + "[" + PREFIX_TAG + " TAG]...\n"
            + "Example: " + CommandType.CONTACT + " " + COMMAND_WORD + " "
            + PREFIX_NAME + " John Doe "
            + PREFIX_EMAIL + " johnd@example.com "
            + PREFIX_PHONE + " 98765432 "
            + PREFIX_ADDRESS + " 311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + " friends "
            + PREFIX_TAG + " owesMoney";

    public static final String MESSAGE_SUCCESS = "Successfully added contact:\n%1$s";
    public static final String MESSAGE_DUPLICATE_CONTACT =
            "Error adding contact: contact with email \"%1$s\" already exists";

    private final Contact toAdd;

    /**
     * Creates an ContactAddCommand to add the specified {@code Contact}
     */
    public ContactAddCommand(Contact contact) {
        requireNonNull(contact);
        toAdd = contact;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasContact(toAdd)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_CONTACT, toAdd.getEmail()));
        }

        model.addContact(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ContactAddCommand)) {
            return false;
        }

        ContactAddCommand otherContactAddCommand = (ContactAddCommand) other;
        return toAdd.equals(otherContactAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
