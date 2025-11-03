package nusemp.ui;

import java.util.Comparator;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import javafx.util.Duration;

import nusemp.model.event.Event;
import nusemp.model.participant.Participant;
import nusemp.model.participant.ParticipantStatus;


/**
 * A UI component that displays information of an {@code Event}.
 */
public class EventCard extends UiPart<Region> {
    private static final String FXML = "EventListCard.fxml";
    private static final String ICON_PENDING = "m15.3 16.7 1.4-1.4-3.7-3.7V7h-2v5.4l4.3 4.3ZM12 22a9.738 9.738 0 0 "
            + "1-3.9-.788 10.099 10.099 0 0 1-3.175-2.137c-.9-.9-1.612-1.958-2.137-3.175A9.738 9.738 0 0 1 2 12c0-1"
            + ".383.263-2.683.788-3.9a10.099 10.099 0 0 1 2.137-3.175c.9-.9 1.958-1.612 3.175-2.137A9.738 9.738 0 0 1"
            + " 12 2c1.383 0 2.683.263 3.9.788a10.098 10.098 0 0 1 3.175 2.137c.9.9 1.613 1.958 2.137 3.175A9.738 9"
            + ".738 0 0 1 22 12a9.738 9.738 0 0 1-.788 3.9 10.098 10.098 0 0 1-2.137 3.175c-.9.9-1.958 1.613-3.175 2"
            + ".137A9.738 9.738 0 0 1 12 22Zm0-2c2.217 0 4.104-.78 5.663-2.337C19.22 16.104 20 14.217 20 12s-.78-4"
            + ".104-2.337-5.662C16.104 4.779 14.217 4 12 4s-4.104.78-5.662 2.338C4.779 7.896 4 9.783 4 12s.78 4.104 2"
            + ".338 5.663C7.896 19.22 9.783 20 12 20Z";

    private static final String ICON_ONGOING = "m9.5 16.5 7-4.5-7-4.5v9ZM12 22a9.738 9.738 0 0 1-3.9-.788 10.099 10"
            + ".099 0 0 1-3.175-2.137c-.9-.9-1.612-1.958-2.137-3.175A9.738 9.738 0 0 1 2 12c0-1.383.263-2.683.788-3"
            + ".9a10.099 10.099 0 0 1 2.137-3.175c.9-.9 1.958-1.612 3.175-2.137A9.738 9.738 0 0 1 12 2c1.383 0 2.683"
            + ".263 3.9.788a10.098 10.098 0 0 1 3.175 2.137c.9.9 1.613 1.958 2.137 3.175A9.738 9.738 0 0 1 22 12a9"
            + ".738 9.738 0 0 1-.788 3.9 10.098 10.098 0 0 1-2.137 3.175c-.9.9-1.958 1.613-3.175 2.137A9.738 9.738 0 "
            + "0 1 12 22Zm0-2c2.233 0 4.125-.775 5.675-2.325C19.225 16.125 20 14.233 20 12c0-2.233-.775-4.125-2.325-5"
            + ".675C16.125 4.775 14.233 4 12 4c-2.233 0-4.125.775-5.675 2.325C4.775 7.875 4 9.767 4 12c0 2.233.775 4"
            + ".125 2.325 5.675C7.875 19.225 9.767 20 12 20Z";

    private static final String ICON_DONE = "M12 22a9.738 9.738 0 0 1-3.9-.788 10.099 10.099 0 0 1-3.175-2.137c-.9-"
            + ".9-1.612-1.958-2.137-3.175A9.738 9.738 0 0 1 2 12c0-1.383.263-2.683.788-3.9a10.099 10.099 0 0 1 2"
            + ".137-3.175c.9-.9 1.958-1.612 3.175-2.137A9.738 9.738 0 0 1 12 2c1.083 0 2.108.158 3.075.475.967.317 1"
            + ".858.758 2.675 1.325L16.3 5.275a8.6 8.6 0 0 0-2.025-.938A7.552 7.552 0 0 0 12 4c-2.217 0-4.104.78-5"
            + ".662 2.338C4.779 7.896 4 9.783 4 12s.78 4.104 2.338 5.663C7.896 19.22 9.783 20 12 20s4.104-.78 5.663-2"
            + ".337C19.22 16.104 20 14.217 20 12c0-.3-.017-.6-.05-.9a7.356 7.356 0 0 0-.15-.875L21.425 8.6c.183.533"
            + ".325 1.083.425 1.65.1.567.15 1.15.15 1.75a9.738 9.738 0 0 1-.788 3.9 10.098 10.098 0 0 1-2.137 3.175c-"
            + ".9.9-1.958 1.613-3.175 2.137A9.738 9.738 0 0 1 12 22Zm-1.4-5.4-4.25-4.25 1.4-1.4 2.85 2.85 10-10.025 1"
            + ".4 1.4L10.6 16.6Z";

    /* Width offset accounts for padding and scrollbar, used for binding widths. */
    private static final int WIDTH_OFFSET = 40;

    /* Additional offset from the copy to clipboard button. */
    private static final int CLIPBOARD_BUTTON_OFFSET = 30;

    public final Event event;
    private final int displayedIndex;
    private final List<Participant> participants;
    private final ListView<Event> parentListView;

    private String exportContentData = "";

    @FXML
    private StackPane cardPane;
    @FXML
    private Label name;
    @FXML
    private HBox nameBox;
    @FXML
    private Label id;
    @FXML
    private FlowPane tags;
    @FXML
    private Label date;
    @FXML
    private HBox dateBox;
    @FXML
    private Label address;
    @FXML
    private HBox addressBox;
    @FXML
    private Label status;
    @FXML
    private SVGPath statusIcon;
    @FXML
    private HBox statusBox;
    @FXML
    private FlowPane people;
    @FXML
    private Label exportContent;
    @FXML
    private Button copyButton;

    /**
     * Creates an {@code EventCard} with the given {@code Event} and index to display.
     */
    public EventCard(Event event, int displayedIndex, List<Participant> participants, ListView<Event> parentListView) {
        super(FXML);
        this.event = event;
        this.displayedIndex = displayedIndex;
        this.participants = participants;
        this.parentListView = parentListView;

        initializeEventInfo();
        initializeExportContent();
        bindWidths();
    }

    private void initializeEventInfo() {
        id.setText(displayedIndex + ". ");
        name.setText(event.getName().toString());
        date.setText(event.getDate().toString());

        initializeStatus();

        if (event.hasAddress()) {
            address.setText(event.getAddress().value);
        } else {
            addressBox.setManaged(false);
            addressBox.setVisible(false);
        }

        if (event.hasTags()) {
            event.getTags().stream()
                    .sorted(Comparator.comparing(tag -> tag.tagName))
                    .forEach(tag -> tags.getChildren().add(createLabel(tag.tagName)));
        } else {
            tags.setManaged(false);
            tags.setVisible(false);
        }

        addPeople();

    }

    private void initializeStatus() {
        status.setText(capitalize(event.getStatus().toString()));
        switch (event.getStatus()) {
        case PENDING:
            statusBox.getStyleClass().add("event-status-pending");
            statusIcon.setContent(ICON_PENDING);
            break;
        case ONGOING:
            statusBox.getStyleClass().add("event-status-ongoing");
            statusIcon.setContent(ICON_ONGOING);
            break;
        case DONE:
            statusBox.getStyleClass().add("event-status-done");
            statusIcon.setContent(ICON_DONE);
            break;
        default:
            break;
        }
    }

    private void addPeople() {
        if (participants.isEmpty()) {
            people.setManaged(false);
            people.setVisible(false);
            copyButton.setVisible(false);

            return;
        }
        List<Participant> sortedParticipants = participants.stream()
                .sorted(Comparator.comparing(p -> p.getContact().getName().value.toLowerCase()))
                .toList();
        for (Participant p : sortedParticipants) {
            String name = p.getContact().getName().value;
            String email = p.getContact().getEmail().value;

            Label label = createLabel(name);
            if (p.getStatus() == ParticipantStatus.AVAILABLE) {
                exportContentData += email + ",";

            } else if (p.getStatus() == ParticipantStatus.UNAVAILABLE){
                label.setStyle("-fx-background-color: #FFA556;");

            } else {
                label.setStyle("-fx-background-color: #a8a8a8;");
            }
            people.getChildren().add(label);

        }
    }

    private void initializeExportContent() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M8.25 7.5V6.108c0-1.135.845-2.098 1.976-2.192.373-.03.748-.057 1.123-.08M15.75 18H18a2."
                + "25 2.25 0 0 0 2.25-2.25V6.108c0-1.135-.845-2.098-1.976-2.192a48.424 48.424 0 0 0-1.123-.08M15.75 "
                + "18.75v-1.875a3.375 3.375 0 0 0-3.375-3.375h-1.5a1.125 1.125 0 0 1-1.125-1.125v-1.5A3.375 3.375 "
                + "0 0 0 6.375 7.5H5.25m11.9-3.664A2.251 2.251 0 0 0 15 2.25h-1.5a2.251 2.251 0 0 0-2.15 1.586m5.8 0c"
                + ".065.21.1.433.1.664v.75h-6V4.5c0-.231.035-.454.1-.664M6.75 7.5H4.875c-.621 0-1.125.504-1.125 1.1"
                + "25v12c0 .621.504 1.125 1.125 1.125h9.75c.621 0 1.125-.504 1.125-1.125V16.5a9 9 0 0 0-9-9Z");
        svgPath.setStyle("-fx-fill: none; -fx-stroke: #a8a8a8; -fx-stroke-width: 1");
        copyButton.setGraphic(svgPath);

        if (!exportContentData.isEmpty()) {
            exportContentData = exportContentData.substring(0, exportContentData.length() - 1);
        }
        exportContent.setText(exportContentData);
    }

    private void bindWidths() {
        nameBox.maxWidthProperty().bind(
                parentListView.widthProperty().subtract(WIDTH_OFFSET + CLIPBOARD_BUTTON_OFFSET));
        dateBox.maxWidthProperty().bind(parentListView.widthProperty().subtract(WIDTH_OFFSET));
        addressBox.maxWidthProperty().bind(parentListView.widthProperty().subtract(WIDTH_OFFSET));
        statusBox.maxWidthProperty().bind(parentListView.widthProperty().subtract(WIDTH_OFFSET));
    }

    /**
     * Handles the copy button click to copy export content to clipboard.
     */
    @FXML
    private void handleCopyEmails() {
        String contentToCopy = exportContent.getText();

        if (contentToCopy == null || contentToCopy.trim().isEmpty()) {
            showPopupMessage("No available contacts present: clipboard not updated.");
            return;
        }

        // Copy to clipboard
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(contentToCopy);
        clipboard.setContent(content);

        // Show popup message
        showPopupMessage("Available contacts copied into clipboard!");
    }

    /**
     * Shows a popup message near the copy button.
     */
    private void showPopupMessage(String message) {
        Popup popup = new Popup();

        Label popupLabel = new Label(message);
        popupLabel.setStyle(
                "-fx-background-color: #2d2d30; "
                        + "-fx-text-fill: white; "
                        + "-fx-padding: 10px 15px; "
                        + "-fx-background-radius: 5px; "
                        + "-fx-font-size: 12px; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 2);"
        );

        StackPane popupContent = new StackPane(popupLabel);
        popupContent.setAlignment(Pos.CENTER);
        popup.getContent().add(popupContent);

        // Position popup near the button
        popup.setAutoHide(true);
        popup.show(
                copyButton.getScene().getWindow(),
                copyButton.localToScreen(copyButton.getBoundsInLocal()).getMinX() - 80,
                copyButton.localToScreen(copyButton.getBoundsInLocal()).getMinY() - 40
        );

        // Auto-hide after 2 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> popup.hide());
        delay.play();
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.maxWidthProperty().bind(parentListView.widthProperty().subtract(WIDTH_OFFSET));
        return label;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
