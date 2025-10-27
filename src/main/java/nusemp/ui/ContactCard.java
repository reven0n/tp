package nusemp.ui;

import java.util.Comparator;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import nusemp.model.contact.Contact;
import nusemp.model.event.Participant;
import nusemp.model.event.Status;

/**
 * A UI component that displays information of a {@code Contact}.
 */
public class ContactCard extends UiPart<Region> {

    private static final String FXML = "ContactListCard.fxml";

    public final Contact contact;

    @FXML
    private VBox cardPane;
    @FXML
    private Label id;
    @FXML
    private FlowPane tags;
    @FXML
    private Label name;
    @FXML
    private Label email;
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
     */
    public ContactCard(Contact contact, int displayedIndex) {
        super(FXML);
        this.contact = contact;
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
                    .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        } else {
            tags.setManaged(false);
            tags.setVisible(false);
        }

        if (contact.hasEvents()) {
            addEvents();
        } else {
            events.setManaged(false);
            events.setVisible(false);
        }
    }

    private void addEvents() {
        contact.getEvents().forEach(event -> {
            List<Participant> matchingParticipants = event.getParticipants().stream()
                    .filter(p -> p.getContact().isSameContact(contact)).toList();
            assert matchingParticipants.size() == 1;
            Label label = new Label(event.getName().value);
            Participant participant = matchingParticipants.get(0);
            if (participant.getStatus() != Status.ATTENDING) {
                label.setStyle("-fx-background-color: #a8a8a8;");
            }
            events.getChildren().add(label);
        });
    }
}
