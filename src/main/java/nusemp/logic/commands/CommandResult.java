package nusemp.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import nusemp.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** The heading to be displayed above the contact or event list. */
    private final String displayedListHeading;

    /**
     * Whether to show event list after command.
     * If false, will show contact list instead.
     * If null, will not change the current list being shown.
     */
    private final Boolean showEventList;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, String displayedListHeading,
            Boolean showEventList, boolean showHelp, boolean exit) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.displayedListHeading = displayedListHeading;
        this.showEventList = showEventList;
        this.showHelp = showHelp;
        this.exit = exit;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser}, {@code displayedListHeading} and
     * {@code showEventList}, with other fields set to their default value.
     * <p>
     * Note that showEventList is of type Boolean, so it can be null to represent that the screen should not change.
     */
    public CommandResult(String feedbackToUser, String displayedListHeading, Boolean showEventList) {
        this(feedbackToUser, displayedListHeading, showEventList, false, false);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public String getDisplayedListHeading() {
        return displayedListHeading;
    }

    public Boolean isShowEventList() {
        return showEventList;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult)) {
            return false;
        }

        CommandResult otherCommandResult = (CommandResult) other;
        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && displayedListHeading.equals(otherCommandResult.displayedListHeading)
                && showEventList == otherCommandResult.showEventList
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, displayedListHeading, showEventList, showHelp, exit);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("displayedListHeading", displayedListHeading)
                .add("showEventList", showEventList)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .toString();
    }

}
