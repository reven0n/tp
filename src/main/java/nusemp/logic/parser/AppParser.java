package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nusemp.commons.core.LogsCenter;
import nusemp.logic.commands.Command;
import nusemp.logic.commands.CommandType;
import nusemp.logic.commands.ExitCommand;
import nusemp.logic.commands.HelpCommand;
import nusemp.logic.commands.contact.ContactAddCommand;
import nusemp.logic.commands.contact.ContactDeleteCommand;
import nusemp.logic.commands.contact.ContactEditCommand;
import nusemp.logic.commands.contact.ContactFindCommand;
import nusemp.logic.commands.contact.ContactListCommand;
import nusemp.logic.commands.contact.ContactShowCommand;
import nusemp.logic.commands.event.EventAddCommand;
import nusemp.logic.commands.event.EventDeleteCommand;
import nusemp.logic.commands.event.EventEditCommand;
import nusemp.logic.commands.event.EventExportCommand;
import nusemp.logic.commands.event.EventFindCommand;
import nusemp.logic.commands.event.EventLinkCommand;
import nusemp.logic.commands.event.EventListCommand;
import nusemp.logic.commands.event.EventRsvpCommand;
import nusemp.logic.commands.event.EventShowCommand;
import nusemp.logic.commands.event.EventUnlinkCommand;
import nusemp.logic.parser.contact.ContactAddCommandParser;
import nusemp.logic.parser.contact.ContactDeleteCommandParser;
import nusemp.logic.parser.contact.ContactEditCommandParser;
import nusemp.logic.parser.contact.ContactFindCommandParser;
import nusemp.logic.parser.contact.ContactListCommandParser;
import nusemp.logic.parser.contact.ContactShowCommandParser;
import nusemp.logic.parser.event.EventAddCommandParser;
import nusemp.logic.parser.event.EventDeleteCommandParser;
import nusemp.logic.parser.event.EventEditCommandParser;
import nusemp.logic.parser.event.EventExportCommandParser;
import nusemp.logic.parser.event.EventFindCommandParser;
import nusemp.logic.parser.event.EventLinkCommandParser;
import nusemp.logic.parser.event.EventListCommandParser;
import nusemp.logic.parser.event.EventRsvpCommandParser;
import nusemp.logic.parser.event.EventShowCommandParser;
import nusemp.logic.parser.event.EventUnlinkCommandParser;
import nusemp.logic.parser.exceptions.ParseException;

/**
 * Parses all user input.
 */
public class AppParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandType>\\S+).*");
    private static final Pattern CONTACT_COMMAND_FORMAT =
            Pattern.compile("contact (?<commandWord>\\S+)(?<arguments>.*)");
    private static final Pattern EVENT_COMMAND_FORMAT =
            Pattern.compile("event (?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(AppParser.class);

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

        case ContactFindCommand.COMMAND_WORD:
            return new ContactFindCommandParser().parse(arguments);

        case ContactListCommand.COMMAND_WORD:
            return new ContactListCommandParser().parse(arguments);

        case ContactShowCommand.COMMAND_WORD:
            return new ContactShowCommandParser().parse(arguments);

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

        case EventEditCommand.COMMAND_WORD:
            return new EventEditCommandParser().parse(arguments);

        case EventListCommand.COMMAND_WORD:
            return new EventListCommandParser().parse(arguments);

        case EventLinkCommand.COMMAND_WORD:
            return new EventLinkCommandParser().parse(arguments);

        case EventUnlinkCommand.COMMAND_WORD:
            return new EventUnlinkCommandParser().parse(arguments);

        case EventShowCommand.COMMAND_WORD:
            return new EventShowCommandParser().parse(arguments);

        case EventDeleteCommand.COMMAND_WORD:
            return new EventDeleteCommandParser().parse(arguments);

        case EventRsvpCommand.COMMAND_WORD:
            return new EventRsvpCommandParser().parse(arguments);

        case EventExportCommand.COMMAND_WORD:
            return new EventExportCommandParser().parse(arguments);

        case EventFindCommand.COMMAND_WORD:
            return new EventFindCommandParser().parse(arguments);


        default:
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }
}
