package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nusemp.commons.core.LogsCenter;
import nusemp.logic.commands.Command;
import nusemp.logic.commands.CommandType;
import nusemp.logic.commands.ContactAddCommand;
import nusemp.logic.commands.ContactClearCommand;
import nusemp.logic.commands.ContactDeleteCommand;
import nusemp.logic.commands.ContactEditCommand;
import nusemp.logic.commands.ContactFindCommand;
import nusemp.logic.commands.ContactListCommand;
import nusemp.logic.commands.EventAddCommand;
import nusemp.logic.commands.ExitCommand;
import nusemp.logic.commands.HelpCommand;
import nusemp.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandType>\\S+).*");
    private static final Pattern CONTACT_COMMAND_FORMAT =
            Pattern.compile("contact (?<commandWord>\\S+)(?<arguments>.*)");
    private static final Pattern EVENT_COMMAND_FORMAT =
            Pattern.compile("event (?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());

        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandType = matcher.group("commandType");

        // Note to developers: Change the log level in config.json to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command type: " + commandType);

        switch (CommandType.fromString(commandType)) {

        case CONTACT:
            return parseContactCommand(userInput);

        case EVENT:
            return parseEventCommand(userInput);

        case HELP:
            return new HelpCommand();

        case EXIT:
            return new ExitCommand();

        case UNKNOWN:
            // Fallthrough
        default:
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    /**
     * Parses user input that starts with "contact" into command for execution.
     *
     * @param userInput full user input string
     * @return the contact command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    private Command parseContactCommand(String userInput) throws ParseException {
        final Matcher matcher = CONTACT_COMMAND_FORMAT.matcher(userInput.trim());

        if (!matcher.matches()) {
            // TODO: improve error message to specify the correct format for contact commands
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        switch (commandWord) {

        case ContactAddCommand.COMMAND_WORD:
            return new ContactAddCommandParser().parse(arguments);

        case ContactEditCommand.COMMAND_WORD:
            return new ContactEditCommandParser().parse(arguments);

        case ContactDeleteCommand.COMMAND_WORD:
            return new ContactDeleteCommandParser().parse(arguments);

        case ContactClearCommand.COMMAND_WORD:
            return new ContactClearCommand();

        case ContactFindCommand.COMMAND_WORD:
            return new ContactFindCommandParser().parse(arguments);

        case ContactListCommand.COMMAND_WORD:
            return new ContactListCommand();

        default:
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    /**
     * Parses user input that starts with "event" into command for execution.
     *
     * @param userInput full user input string
     * @return the event command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    private Command parseEventCommand(String userInput) throws ParseException {
        final Matcher matcher = EVENT_COMMAND_FORMAT.matcher(userInput.trim());

        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        switch (commandWord) {
        case EventAddCommand.COMMAND_WORD:
            return new EventAddCommandParser().parse(arguments);

        default:
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }
}
