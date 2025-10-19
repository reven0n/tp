package nusemp.logic.parser;

import static nusemp.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nusemp.logic.parser.CliSyntax.PREFIX_NAME;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseFailure;
import static nusemp.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.event.EventListCommand;
import nusemp.logic.parser.event.EventListCommandParser;
import nusemp.logic.parser.exceptions.ParseException;

class EventListCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT = String.format(
            MESSAGE_INVALID_COMMAND_FORMAT, EventListCommand.MESSAGE_USAGE);
    private EventListCommandParser parser = new EventListCommandParser();

    @Test
    public void parse_validArgs_success() throws ParseException {
        assertParseSuccess(parser, "", new EventListCommand());
        assertParseSuccess(parser, "     ", new EventListCommand());
    }

    @Test
    public void parse_invalidArgs_failure() {
        assertParseFailure(parser, " " + PREFIX_NAME + "Meeting", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " extraArg", MESSAGE_INVALID_FORMAT);
    }
}
