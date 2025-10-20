package nusemp.ui;

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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import nusemp.commons.core.GuiSettings;
import nusemp.commons.core.LogsCenter;
import nusemp.logic.Logic;
import nusemp.logic.commands.CommandResult;
import nusemp.logic.commands.exceptions.CommandException;
import nusemp.logic.parser.exceptions.ParseException;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private ContactListPanel contactListPanel;
    private EventListPanel eventListPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    // Terminal overlay components
    private ResultDisplay terminalResultDisplay;
    private CommandBox terminalCommandBox;
    private boolean isTerminalVisible = false;

    // Theme management
    private boolean isDarkTheme = true;
    private Scene scene;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane contactListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private StackPane terminalOverlay;

    @FXML
    private StackPane terminalResultDisplayPlaceholder;

    @FXML
    private StackPane terminalCommandBoxPlaceholder;

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

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));

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
        contactListPanel = new ContactListPanel(logic.getFilteredContactList());

        contactListPanelPlaceholder.getChildren().add(contactListPanel.getRoot());

        resultDisplay = new ResultDisplay();

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAppDataFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);

        // Initialize terminal components
        terminalResultDisplay = new ResultDisplay();
        terminalResultDisplayPlaceholder.getChildren().add(terminalResultDisplay.getRoot());

        terminalCommandBox = new CommandBox(this::executeTerminalCommand);
        terminalCommandBoxPlaceholder.getChildren().add(terminalCommandBox.getRoot());

        setContactsActive();
    }

    /**
     * Handles the contact view toggle and updates button states.
     */
    @FXML
    public void handleContactViewToggle() {
        // Update UI logic
        contactListPanel = new ContactListPanel(logic.getFilteredContactList());
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
        eventListPanel = new EventListPanel(logic.getFilteredEventList());
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
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }


    /**
     * Handles the terminal button click and Ctrl+T shortcut.
     * Shows/hides the terminal overlay similar to macOS Spotlight.
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
     * Shows the terminal overlay.
     */
    private void showTerminal() {
        terminalOverlay.setVisible(true);
        terminalOverlay.setManaged(true);
        isTerminalVisible = true;

        // Focus on the terminal command box
        terminalCommandBox.getRoot().requestFocus();
    }

    /**
     * Hides the terminal overlay.
     */
    private void hideTerminal() {
        terminalOverlay.setVisible(false);
        terminalOverlay.setManaged(false);
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
            getClass().getResource("/view/LightTheme.css").toExternalForm(),
            getClass().getResource("/view/Extensions.css").toExternalForm()
        );
        themeToggle.setText("🌙"); // Moon icon for dark theme
        isDarkTheme = false;
    }

    /**
     * Switches to dark theme.
     */
    private void switchToDarkTheme() {
        scene.getStylesheets().clear();
        scene.getStylesheets().addAll(
            getClass().getResource("/view/DarkTheme.css").toExternalForm(),
            getClass().getResource("/view/Extensions.css").toExternalForm()
        );
        themeToggle.setText("☀"); // Sun icon for light theme
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
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
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
     *
     * @see Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    /**
     * Executes the terminal command and returns the result.
     */
    private CommandResult executeTerminalCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Terminal Result: " + commandResult.getFeedbackToUser());
            terminalResultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing terminal command: " + commandText);
            terminalResultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}

