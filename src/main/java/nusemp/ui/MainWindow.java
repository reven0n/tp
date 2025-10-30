package nusemp.ui;

import java.awt.Desktop;
import java.net.URI;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import nusemp.commons.core.GuiSettings;
import nusemp.commons.core.LogsCenter;
import nusemp.logic.Logic;
import nusemp.logic.Messages;
import nusemp.logic.commands.CommandResult;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.logic.parser.exceptions.ParseException;

/**
 * The Main Window. Provides the basic application layout containing
 * a sidebar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private ContactListPanel contactListPanel;
    private EventListPanel eventListPanel;

    // Terminal window components
    private ResultDisplay resultDisplay;
    private CommandBox commandBox;
    private boolean isTerminalVisible = false;

    // Theme management
    private boolean isDarkTheme = true;
    private Scene scene;

    @FXML
    private StackPane contactListPanelPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private Region terminalBackdrop;

    @FXML
    private VBox terminalWindow;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private Button contactsToggle;

    @FXML
    private Button eventsToggle;

    @FXML
    private Button themeToggle;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        primaryStage.setTitle("NUS Event Mailer Pro");

        // Store scene reference for theme switching
        this.scene = primaryStage.getScene();

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());
        setTheme(logic.getGuiSettings());

        setAccelerators();
    }


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        // Add Ctrl+T shortcut for terminal
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.T) {
                handleTerminal();
                event.consume();
            }
            // ESC to close terminal
            if (event.getCode() == KeyCode.ESCAPE && isTerminalVisible) {
                hideTerminal();
                event.consume();
            }
        });
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        String contactHeading = logic.getFilteredContactList().isEmpty()
                ? Messages.HEADING_CONTACTS_NONE : Messages.HEADING_CONTACTS;
        contactListPanel = new ContactListPanel(contactHeading, logic.getFilteredContactList(), logic::getParticipants);

        String eventHeading = logic.getFilteredEventList().isEmpty()
                ? Messages.HEADING_EVENTS_NONE : Messages.HEADING_EVENTS;
        eventListPanel = new EventListPanel(eventHeading, logic.getFilteredEventList(), logic::getParticipants);

        contactListPanelPlaceholder.getChildren().add(contactListPanel.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAppDataFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        // Initialize terminal components
        resultDisplay = new ResultDisplay();
        resultDisplay.setFeedbackToUser(Messages.MESSAGE_WELCOME);
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(this.commandBox.getRoot());

        setContactsActive();
    }

    /**
     * Handles the contact view toggle and updates button states.
     */
    @FXML
    public void handleContactViewToggle() {
        // Update UI logic
        contactListPanelPlaceholder.getChildren().clear();
        contactListPanelPlaceholder.getChildren().add(contactListPanel.getRoot());

        // Update button styles
        setContactsActive();
    }

    /**
     * Handles the event view toggle and updates button states.
     */
    @FXML
    public void handleEventViewToggle() {
        // Update UI logic
        contactListPanelPlaceholder.getChildren().clear();
        contactListPanelPlaceholder.getChildren().add(eventListPanel.getRoot());

        // Update button styles
        setEventsActive();
    }

    /**
     * Sets the contacts button as active and events as inactive.
     */
    private void setContactsActive() {
        contactsToggle.getStyleClass().removeAll("toggle-inactive");
        contactsToggle.getStyleClass().add("toggle-active");

        eventsToggle.getStyleClass().removeAll("toggle-active");
        eventsToggle.getStyleClass().add("toggle-inactive");
    }

    /**
     * Sets the events button as active and contacts as inactive.
     */
    private void setEventsActive() {
        eventsToggle.getStyleClass().removeAll("toggle-inactive");
        eventsToggle.getStyleClass().add("toggle-active");

        contactsToggle.getStyleClass().removeAll("toggle-active");
        contactsToggle.getStyleClass().add("toggle-inactive");
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Sets the theme based on {@code guiSettings}.
     */
    void setTheme(GuiSettings guiSettings) {
        if (guiSettings.isDarkTheme()) {
            switchToDarkTheme();
        } else {
            switchToLightTheme();
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        try {

            String url = "https://ay2526s1-cs2103t-f15b-2.github.io/tp/UserGuide.html";
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Handles the terminal button click and Ctrl+T shortcut.
     * Shows/hides the terminal window similar to macOS Spotlight.
     */
    @FXML
    public void handleTerminal() {
        if (isTerminalVisible) {
            hideTerminal();
        } else {
            showTerminal();
        }
    }

    /**
     * Shows the terminal window.
     */
    private void showTerminal() {
        terminalBackdrop.setVisible(true);
        terminalBackdrop.setManaged(true);
        terminalWindow.setVisible(true);
        terminalWindow.setManaged(true);
        isTerminalVisible = true;

        // Focus on the terminal command box
        commandBox.requestFocus();

    }

    /**
     * Hides the terminal overlay.
     */
    private void hideTerminal() {
        terminalBackdrop.setVisible(false);
        terminalBackdrop.setManaged(false);
        terminalWindow.setVisible(false);
        terminalWindow.setManaged(false);
        isTerminalVisible = false;
    }

    /**
     * Handles the theme toggle button to switch between light and dark themes.
     */
    @FXML
    public void handleThemeToggle() {
        if (isDarkTheme) {
            switchToLightTheme();
        } else {
            switchToDarkTheme();
        }
    }

    /**
     * Switches to light theme.
     */
    private void switchToLightTheme() {
        scene.getStylesheets().clear();
        scene.getStylesheets().addAll(
            getClass().getResource("/css/LightTheme.css").toExternalForm(),
            getClass().getResource("/css/Extensions.css").toExternalForm()
        );
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M11 19.25q-3.438 0-5.844-2.406T2.75 11t2.406-5.844T11 2.75a8.5 8.5 0 0 1 1.238.092 4.85 4.8"
                + "5 0 0 0-1.501 1.73 4.87 4.87 0 0 0-.562 2.303q0 2.063 1.444 3.506 1.443 1.444 3.506 1.444 1.26 0 2.3"
                + "15-.561a4.9 4.9 0 0 0 1.718-1.501A8 8 0 0 1 19.25 11q0 3.438-2.406 5.844T11 19.25");
        svgPath.getStyleClass().add("icon");
        themeToggle.setGraphic(svgPath);
        isDarkTheme = false;
    }

    /**
     * Switches to dark theme.
     */
    private void switchToDarkTheme() {
        scene.getStylesheets().clear();
        scene.getStylesheets().addAll(
            getClass().getResource("/css/DarkTheme.css").toExternalForm(),
            getClass().getResource("/css/Extensions.css").toExternalForm()
        );
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M11 15.583q-1.902 0-3.243-1.34T6.417 11t1.34-3.243T11 6.417t3.243 1.34T15.583 11t-1.34 3.24"
                + "3T11 15.583m-6.417-3.666H.917v-1.834h3.666zm16.5 0h-3.666v-1.834h3.666zm-11-7.334V.917h1.834v3.666zm"
                + "0 16.5v-3.666h1.834v3.666zM5.867 7.104 3.552 4.881 4.858 3.53l2.2 2.292zm11.275 11.367-2.223-2.315 1"
                + ".214-1.26 2.315 2.223zM14.896 5.867l2.223-2.315 1.352 1.306-2.292 2.2zM3.529 17.142l2.315-2.223 1.26"
                + " 1.214-2.223 2.315z");
        svgPath.getStyleClass().add("icon");
        themeToggle.setGraphic(svgPath);
        isDarkTheme = true;
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY(), isDarkTheme);
        logic.setGuiSettings(guiSettings);
        primaryStage.hide();
    }

    public ContactListPanel getContactListPanel() {
        return contactListPanel;
    }

    public EventListPanel getEventListPanel() {
        return eventListPanel;
    }

    /**
     * Executes the command and returns the result.
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            handleCommandResult(commandResult);
            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    private void handleCommandResult(CommandResult commandResult) {
        logger.info("Result: " + commandResult.getFeedbackToUser());
        resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

        if (commandResult.isShowHelp()) {
            handleHelp();
        }
        if (commandResult.isExit()) {
            handleExit();
        }

        switch (commandResult.getUiBehavior()) {
        case SHOW_CONTACTS:
            contactListPanel.updateHeading(commandResult.getHeading());
            handleContactViewToggle();
            break;
        case SHOW_EVENTS:
            eventListPanel.updateHeading(commandResult.getHeading());
            handleEventViewToggle();
            break;
        case NONE:
            // Fallthrough
        default:
            break;
        }
    }
}

