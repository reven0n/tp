package nusemp.logic.commands;

import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import nusemp.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    /** The UI behavior after executing the command. */
    public enum UiBehavior {
        SHOW_CONTACTS,
        SHOW_EVENTS,
        NONE
    }

    private final String feedbackToUser;

    /** The heading to be displayed above the contact or event list. */
    private final String heading;

    /**
     * Whether to show event list after command.
     * If false, will show contact list instead.
     * If null, will not change the current list being shown.
     */
    private final UiBehavior behavior;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, UiBehavior behavior, String heading, boolean showHelp, boolean exit) {
        requireAllNonNull(feedbackToUser, behavior, heading, showHelp, exit);
        this.feedbackToUser = feedbackToUser;
        this.heading = heading;
        this.behavior = behavior;
        this.showHelp = showHelp;
        this.exit = exit;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser}, {@code behavior} and
     * {@code heading}, with other fields set to their default value.
     * <p>
     * Note that if UiBehavior is NONE, then the heading value will be ignored.
     */
    public CommandResult(String feedbackToUser, UiBehavior behavior, String heading) {
        this(feedbackToUser, behavior, heading, false, false);
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * with other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, UiBehavior.NONE, "");
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public String getHeading() {
        return heading;
    }

    public UiBehavior getUiBehavior() {
        return behavior;
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
                && behavior == otherCommandResult.behavior
                && heading.equals(otherCommandResult.heading)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, behavior, heading, showHelp, exit);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("behavior", behavior)
                .add("heading", heading)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .toString();
    }

}
