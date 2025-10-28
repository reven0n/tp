package nusemp.commons.exceptions;

/**
 * Represents an error during loading of data from a file.
 */
public class DataLoadingException extends Exception {
    public DataLoadingException(Exception cause) {
        super(cause);
    }

    /**
     * Returns a detailed error message that includes information about the parsing error.
     * For JSON parsing errors, this includes line and column numbers.
     *
     * @return detailed error message
     */
    public String getDetailedMessage() {
        Throwable cause = getCause();
        if (cause != null) {
            return cause.getMessage();
        }
        return getMessage();
    }

}
