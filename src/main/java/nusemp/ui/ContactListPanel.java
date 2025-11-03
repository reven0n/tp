package nusemp.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

import nusemp.model.contact.Contact;
import nusemp.model.participant.ContactToParticipantsFunction;

/**
 * Panel containing the list of contacts.
 */
public class ContactListPanel extends UiPart<Region> {
    private static final String FXML = "ContactListPanel.fxml";

    /* Width offset accounts for scrollbar, used for binding widths. */
    private static final int WIDTH_OFFSET = 12;

    private final ContactToParticipantsFunction participantsFn;

    private final PrefixedList<Contact, String> prefixedList;

    @FXML
    private ListView<Contact> contactListView;

    /**
     * Creates a {@code ContactListPanel} with the given {@code ObservableList}.
     */
    public ContactListPanel(String heading, ObservableList<Contact> contactList,
            ContactToParticipantsFunction participantsFn) {
        super(FXML);
        this.participantsFn = participantsFn;
        prefixedList = new PrefixedList<>(contactList, heading);
        contactListView.setItems(prefixedList);

        // Add a listener to clear the selection when the ListView loses focus
        contactListView.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                // If the ListView is no longer focused, clear the selection
                contactListView.getSelectionModel().clearSelection();
            }
        });

        contactListView.setCellFactory(listView -> {
            ContactListViewCell cell = new ContactListViewCell();
            cell.prefWidthProperty().bind(listView.widthProperty().subtract(WIDTH_OFFSET));
            return cell;
        });
    }

    public ListView<Contact> getContactListView() {
        return contactListView;
    }

    public void updateHeading(String newHeading) {
        prefixedList.setPrefix(newHeading);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Contact} using a {@code ContactCard}.
     */
    class ContactListViewCell extends ListCell<Contact> {
        @Override
        protected void updateItem(Contact contact, boolean empty) {
            super.updateItem(contact, empty);

            if (getIndex() == 0) {
                setGraphic(new ListHeading(prefixedList.getPrefix()).getRoot());
            } else if (empty || contact == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new ContactCard(contact, getIndex(), participantsFn.apply(contact), contactListView)
                        .getRoot());
            }
        }
    }

}
