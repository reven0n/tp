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
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
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


        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 "
                + "0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z");
        svgPath.setStyle("-fx-fill: lightgray; -fx-stroke: #222; -fx-stroke-width: 1;");
        contactsToggle.setGraphic(svgPath);

        SVGPath svgPath1 = new SVGPath();
        svgPath1.setContent("M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 0 1 2.25-2.25h13.5A2.25 2.25 0 0 1"
                + " 21 7.5v11.25m-18 0A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75m-18 0v-7.5A2.25 2.25 0 0"
                + " 1 5.25 9h13.5A2.25 2.25 0 0 1 21 11.25v7.5m-9-6h.008v.008H12v-.008ZM12 15h.008v.008H12V15Zm0 2.2"
                + "5h.008v.008H12v-.008ZM9.75 15h.008v.008H9.75V15Zm0 2.25h.008v.008H9.75v-.008ZM7.5 15h.008v.008H7.5V"
                + "15Zm0 2.25h.008v.008H7.5v-.008Zm6.75-4.5h.008v.008h-.008v-.008Zm0 2.25h.008v.008h-.008V15Zm0 2.25h."
                + "008v.008h-.008v-.008Zm2.25-4.5h.008v.008H16.5v-.008Zm0 2.25h.008v.008H16.5V15Z");
        svgPath1.setStyle("-fx-fill: lightgray; -fx-stroke: #222; -fx-stroke-width: 1;");
        eventsToggle.setGraphic(svgPath1);

        SVGPath svgPath2 = new SVGPath();
        svgPath2.setContent("M12 3v2.25m6.364.386-1.591 1.591M21 12h-2.25m-.386 6.364-1.591-1.591M12 18.75V21m-4.773-"
                + "4.227-1.591 1.591M5.25 12H3m4.227-4.773L5.636 5.636M15.75 12a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7"
                + ".5 0Z");
        svgPath2.setStyle("-fx-fill: lightgray; -fx-stroke: lightgray; -fx-stroke-width: 1");
        themeToggle.setGraphic(svgPath2);

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
        terminalCommandBox.requestFocus();

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
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M21.752 15.002A9.72 9.72 0 0 1 18 15.75c-5.385 0-9.75-4.365-9.75-9.75 0-1.33.266-2.597.7"
                + "48-3.752A9.753 9.753 0 0 0 3 11.25C3 16.635 7.365 21 12.75 21a9.753 9.753 0 0 0 9.002-5.998Z");
        svgPath.setStyle("-fx-fill: gray;");
        themeToggle.setGraphic(svgPath);
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
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M12 3v2.25m6.364.386-1.591 1.591M21 12h-2.25m-.386 6.364-1.591-1.591M12 18.75V21m-4.773"
                + "-4.227-1.591 1.591M5.25 12H3m4.227-4.773L5.636 5.636M15.75 12a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 "
                + "1 7.5 0Z");
        svgPath.setStyle("-fx-fill: lightgray; -fx-stroke: lightgray; -fx-stroke-width: 1");
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
                (int) primaryStage.getX(), (int) primaryStage.getY());
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

