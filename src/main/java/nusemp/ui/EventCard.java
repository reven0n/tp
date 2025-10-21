package nusemp.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import nusemp.model.event.Event;


/**
 * A UI component that displays information of an {@code Event}.
 */
public class EventCard extends UiPart<Region> {

    private static final String FXML = "EventListCard.fxml";

    public final Event event;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label date;
    @FXML
    private Label address;
    @FXML
    private FlowPane people;

    /**
     * Creates an {@code EventCard} with the given {@code Event} and index to display.
     */
    public EventCard(Event event, int displayedIndex) {
        super(FXML);
        this.event = event;
        id.setText(displayedIndex + ". ");
        name.setText(event.getName().toString());
        name.setWrapText(true);
        date.setText(event.getDate().toString());
        date.setWrapText(true);

        if (event.hasAddress()) {
            address.setText(event.getAddress().value);
        } else {
            address.setManaged(false);
            address.setVisible(false);
        }

        event.getParticipants().stream()
                .sorted(Comparator.comparing(contact -> contact.getName().value.toLowerCase()))
                .forEach(contact -> people.getChildren().add(new Label(contact.getName().value)));
    }
}
