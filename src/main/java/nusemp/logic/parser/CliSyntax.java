package nusemp.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("--name", "-n");
    public static final Prefix PREFIX_PHONE = new Prefix("--phone", "-p");
    public static final Prefix PREFIX_EMAIL = new Prefix("--email", "-e");
    public static final Prefix PREFIX_ADDRESS = new Prefix("--address", "-a");
    public static final Prefix PREFIX_TAG = new Prefix("--tag", "-t");
    public static final Prefix PREFIX_DATE = new Prefix("--date", "-d");
    public static final Prefix PREFIX_CONTACT = new Prefix("--contact", "-c");
    public static final Prefix PREFIX_EVENT = new Prefix("--event", "-e");
    public static final Prefix PREFIX_STATUS = new Prefix("--status", "-s");

}
