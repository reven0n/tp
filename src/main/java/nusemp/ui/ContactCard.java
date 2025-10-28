package nusemp.ui;

import java.util.Comparator;
import java.util.List;

import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import nusemp.model.AppData;
import nusemp.model.ParticipantEvent;
import nusemp.model.contact.Contact;
import nusemp.model.event.ParticipantStatus;

/**
 * An UI component that displays information of a {@code Contact}.
 */
public class ContactCard extends UiPart<Region> {

    private static final String FXML = "ContactListCard.fxml";
    //private final AppData appData;

    public final Contact contact;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label name;
    @FXML
    private Label email;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private FlowPane tags;
    @FXML
    private FlowPane events;

    /**
     * Creates a {@code ContactCard} with the given {@code Contact} and index to display.
     */
    public ContactCard(Contact contact, int displayedIndex, List<ParticipantEvent> contactEvent) {
        super(FXML);
        this.contact = contact;
        id.setText(displayedIndex + ". ");
        name.setText(contact.getName().value);
        email.setText(contact.getEmail().value);

        if (contact.hasPhone()) {
            phone.setText(contact.getPhone().value);
        } else {
            phone.setManaged(false);
            phone.setVisible(false);
        }

        if (contact.hasAddress()) {
            address.setText(contact.getAddress().value);
        } else {
            address.setManaged(false);
            address.setVisible(false);
        }

        contact.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
//        contact.getEvents()
//                .forEach(event -> {
//                    event.getParticipants().stream()
//                        .forEach(p -> {
//                            String email = p.getContact().getEmail().value;
//                            if (email.equals(contact.getEmail().value)) {
//                                Label label = new Label(event.getName().value);
//                                if (p.getStatus() != ParticipantStatus.AVAILABLE) {
//                                    label.setStyle("-fx-background-color: #a8a8a8;");
//                                }
//                                events.getChildren().add(label);
//
//                            }
//                        });
//
//                });
//        appData.getEventsForContact(contact)
//                .forEach(event -> {
//                    Label label = new Label(event.getName().value);
//                    events.getChildren().add(label);
//                });
//        contactEventMap.get(contact)
//                .forEach(participantEvent -> {
//                    Label label = new Label(participantEvent.getEvent().getName().value);
//                    if (participantEvent.getStatus() != ParticipantStatus.AVAILABLE) {
//                        label.setStyle("-fx-background-color: #a8a8a8;");
//                    }
//                    events.getChildren().add(label);
//                });

        contactEvent.forEach(participantEvent -> {
            Label eventLabel = new Label(participantEvent.getEvent().getName().value
                    + " (" + participantEvent.getStatus() + ")");
            events.getChildren().add(eventLabel);
        });
    }
}
