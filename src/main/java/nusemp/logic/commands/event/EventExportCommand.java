package nusemp.logic.commands.event;

import static java.util.Objects.requireNonNull;

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
import nusemp.model.event.ParticipantStatus;

/**
 * Exports all contacts linked to an event.
 */
public class EventExportCommand extends Command {
    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = CommandType.EVENT + " " + COMMAND_WORD
            + ": Exports all contacts linked to an event identified by the index used in the displayed event list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + CommandType.EVENT + " " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "Successfully exported contacts linked to event to your clipboard.";

    private String exportContentData = "";

    private final Index eventIndex;

    /**
     * Creates an EventExportCommand to export the specified {@code Event}
     *
     *
     * @param eventIndex Index of the event in the filtered event list to export
     */
    public EventExportCommand(Index eventIndex) {
        requireNonNull(eventIndex);
        this.eventIndex = eventIndex;
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
        eventToExport.getParticipants().stream()
                .sorted(Comparator.comparing(p -> p.getContact().getName().value.toLowerCase()))
                .forEach(p -> {
                    String email = p.getContact().getEmail().value;

                    if (p.getStatus() == ParticipantStatus.AVAILABLE) {
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
        return new CommandResult(MESSAGE_SUCCESS);
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
