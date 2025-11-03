package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;
import static nusemp.commons.util.CollectionUtil.requireAllNonNull;
import static nusemp.logic.parser.CliSyntax.PREFIX_STATUS;

import java.util.Comparator;
import java.util.List;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.Command;
import nusemp.logic.commands.CommandResult;
import nusemp.logic.commands.CommandType;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.model.Model;
import nusemp.model.event.Event;
import nusemp.model.participant.ParticipantStatus;

/**
 * Exports all contacts linked to an event.
 */
public class EventExportCommand extends Command {
    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Exports all contacts linked to an event identified by the index used in the displayed event list.\n"
            + "The status can be either \"available\", \"unavailable\", or \"unknown\"\n\n"
            + "Parameters:  "
            + "INDEX  [" + PREFIX_STATUS + "STATUS]\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " 1 " + PREFIX_STATUS + "unknown\n\n"
            + "Note: INDEX must be a positive integer within the size of the displayed event list.";

    public static final String MESSAGE_SUCCESS =
            "Successfully exported all contacts with status \"%2$s\" linked to event \"%1$s\" to your clipboard.";

    private String exportContentData = "";

    private final Index eventIndex;
    private final ParticipantStatus status;
    private final boolean isStatusProvided;

    public EventExportCommand(Index eventIndex) {
        this(eventIndex, ParticipantStatus.AVAILABLE, false);
    }
    /**
     * Creates an EventExportCommand to export the specified {@code Event}
     */
    public EventExportCommand(Index eventIndex, ParticipantStatus status) {
        this(eventIndex, status, true);
    }

    /**
     * Creates an EventExportCommand with explicit status provision flag
     */
    private EventExportCommand(Index eventIndex, ParticipantStatus status, boolean isStatusProvided) {
        requireAllNonNull(eventIndex, status);
        this.eventIndex = eventIndex;
        this.status = status;
        this.isStatusProvided = isStatusProvided;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Event> lastShownEventList = model.getFilteredEventList();

        // check if the event index is within bounds
        if (eventIndex.getZeroBased() >= lastShownEventList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }
        Event eventToExport = lastShownEventList.get(eventIndex.getZeroBased());
        model.getParticipants(eventToExport).stream()
                .sorted(Comparator.comparing(p -> p.getContact().getName().value.toLowerCase()))
                .forEach(p -> {
                    String email = p.getContact().getEmail().value;

                    if (p.getStatus() == status) {
                        exportContentData = exportContentData + email + ",";
                    }
                });
        if (!exportContentData.isEmpty()) {
            exportContentData = exportContentData.substring(0, exportContentData.length() - 1);
        }
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(exportContentData);
        clipboard.setContent(content);

        String successMessage = String.format(MESSAGE_SUCCESS,
                eventToExport.getName(),
                status.toString().toLowerCase());

        return new CommandResult(successMessage);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventExportCommand)) {
            return false;
        }

        EventExportCommand otherExportCommand = (EventExportCommand) other;
        return eventIndex.equals(otherExportCommand.eventIndex);
    }

    @Override
    public String toString() {
        return EventExportCommand.class.getCanonicalName() + "{eventIndex=" + eventIndex + "}";
    }
}
