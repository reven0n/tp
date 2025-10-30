package nusemp.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

import nusemp.model.event.Event;
import nusemp.model.participant.EventToParticipantsFunction;


/**
 * Panel containing the list of events.
 */
public class EventListPanel extends UiPart<Region> {

    private static final String FXML = "EventListPanel.fxml";

    /* Width offset accounts for scrollbar, used for binding widths. */
    private static final int WIDTH_OFFSET = 12;

    private final EventToParticipantsFunction participantsFn;

    private final DisplayedList<Event, String> displayedList;

    @FXML
    private ListView<Event> eventListView;

    /**
     * Creates a {@code EventListPanel} with the given {@code ObservableList}.
     */
    public EventListPanel(String heading, ObservableList<Event> eventList, EventToParticipantsFunction participantsFn) {
        super(FXML);
        this.participantsFn = participantsFn;
        displayedList = new DisplayedList<>(eventList, heading);
        eventListView.setItems(displayedList);
        eventListView.setCellFactory(listView -> {
            EventListViewCell cell = new EventListViewCell();
            cell.prefWidthProperty().bind(listView.widthProperty().subtract(WIDTH_OFFSET));
            return cell;
        });
    }

    public void updateHeading(String newHeading) {
        displayedList.updateExtraValue(newHeading);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Event} using an {@code EventCard}.
     */
    class EventListViewCell extends ListCell<Event> {
        @Override
        protected void updateItem(Event event, boolean empty) {
            super.updateItem(event, empty);

            if (getIndex() == 0) {
                setGraphic(new ListHeading(displayedList.getExtraValue()).getRoot());
            } else if (empty || event == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new EventCard(event, getIndex(), participantsFn.apply(event), eventListView).getRoot());
            }
        }
    }

}
