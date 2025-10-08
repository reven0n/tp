package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nusemp.commons.core.LogsCenter;
import nusemp.logic.commands.CommandType;
import nusemp.logic.commands.contact.AddCommand;
import nusemp.logic.commands.contact.ClearCommand;
import nusemp.logic.commands.contact.Command;
import nusemp.logic.commands.contact.DeleteCommand;
import nusemp.logic.commands.contact.EditCommand;
import nusemp.logic.commands.contact.ExitCommand;
import nusemp.logic.commands.contact.FindCommand;
import nusemp.logic.commands.contact.HelpCommand;
import nusemp.logic.commands.contact.ListCommand;
import nusemp.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT =
            Pattern.compile("(?<commandType>\\S+)\\s+(?<commandWord>\\S+)(?<arguments>.*)");
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
        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        // Note to developers: Change the log level in config.json to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command type: " + commandType + "Command word: " + commandWord + "; Arguments: " + arguments);

        switch (CommandType.fromString(commandType)) {

        case CONTACT:
            return parseContactCommand(userInput, commandWord, arguments);

        case EVENT:
            return parseEventCommand(userInput, commandWord, arguments);

        case UNKNOWN:
            // Fallthrough
        default:
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    private Command parseContactCommand(String userInput, String commandWord, String arguments) throws ParseException {

        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return new AddCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        default:
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    private Command parseEventCommand(String userInput, String commandWord, String arguments) throws ParseException {
        // TODO: implement event commands
        logger.finer("This user input caused a ParseException: " + userInput);
        throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
    }
}
