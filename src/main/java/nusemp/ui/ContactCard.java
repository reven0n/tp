package nusemp.ui;

import java.util.Comparator;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import nusemp.model.contact.Contact;
import nusemp.model.participant.Participant;
import nusemp.model.participant.ParticipantStatus;

/**
 * A UI component that displays information of a {@code Contact}.
 */
public class ContactCard extends UiPart<Region> {
    private static final String FXML = "ContactListCard.fxml";

    /* Width offset accounts for padding and scrollbar, used for binding widths. */
    private static final int WIDTH_OFFSET = 40;

    public final Contact contact;
    private final int displayedIndex;
    private final List<Participant> participants;
    private final ListView<Contact> parentListView;

    @FXML
    private VBox cardPane;
    @FXML
    private Label id;
    @FXML
    private FlowPane tags;
    @FXML
    private Label name;
    @FXML
    private HBox nameBox;
    @FXML
    private Label email;
    @FXML
    private HBox emailBox;
    @FXML
    private Label phone;
    @FXML
    private HBox phoneBox;
    @FXML
    private Label address;
    @FXML
    private HBox addressBox;
    @FXML
    private FlowPane events;

    /**
     * Creates a {@code ContactCard} with the given {@code Contact} and index to display.
     * The parent list view is also needed for width binding.
     */
    public ContactCard(Contact contact, int displayedIndex, List<Participant> participants,
            ListView<Contact> parentListView) {
        super(FXML);
        this.contact = contact;
        this.participants = participants;
        this.displayedIndex = displayedIndex;
        this.parentListView = parentListView;

        initializeContactInfo();
        bindWidths();
    }

    private void initializeContactInfo() {
        id.setText(displayedIndex + ". ");
        name.setText(contact.getName().value);
        email.setText(contact.getEmail().value);

        if (contact.hasPhone()) {
            phone.setText(contact.getPhone().value);
        } else {
            phoneBox.setManaged(false);
            phoneBox.setVisible(false);
        }

        if (contact.hasAddress()) {
            address.setText(contact.getAddress().value);
        } else {
            addressBox.setManaged(false);
            addressBox.setVisible(false);
        }

        if (contact.hasTags()) {
            contact.getTags().stream()
                    .sorted(Comparator.comparing(tag -> tag.tagName))
                    .forEach(tag -> tags.getChildren().add(createLabel(tag.tagName)));
        } else {
            tags.setManaged(false);
            tags.setVisible(false);
        }

        addEvents();
    }

    private void bindWidths() {
        List<HBox> allHBoxes = List.of(nameBox, emailBox, phoneBox, addressBox);
        for (HBox box : allHBoxes) {
            box.maxWidthProperty().bind(parentListView.widthProperty().subtract(WIDTH_OFFSET));
        }
    }

    private void addEvents() {
        if (participants.isEmpty()) {
            events.setManaged(false);
            events.setVisible(false);
            return;
        }
        List<Participant> sortedParticipants = participants.stream()
                .sorted(Comparator.comparing(p -> p.getContact().getName().value.toLowerCase())).toList();
        for (Participant p : sortedParticipants) {
            String name = p.getEvent().getName().value;

            Label label = createLabel(name);
            if (p.getStatus() != ParticipantStatus.AVAILABLE) {
                label.setStyle("-fx-background-color: #a8a8a8;");
            }
            events.getChildren().add(label);

        }
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.maxWidthProperty().bind(parentListView.widthProperty().subtract(WIDTH_OFFSET));
        return label;
    }
}
