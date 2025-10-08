package nusemp.logic.commands;

/**
 * Represents the type of command: CONTACT or EVENT.
 * A 3rd type, UNKNOWN, is used for unrecognized command types.
 */
public enum CommandType {
    CONTACT, EVENT, UNKNOWN;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    /**
     * Gets the CommandType enum value from a string.
     *
     * @param text the command type in string format.
     * @return the corresponding CommandType enum value for "contact" and "event", or UNKNOWN for unrecognized types.
     */
    public static CommandType fromString(String text) {
        switch (text.toLowerCase()) {
        case "contact":
            return CONTACT;
        case "event":
            return EVENT;
        default:
            return UNKNOWN;
        }
    }
}
