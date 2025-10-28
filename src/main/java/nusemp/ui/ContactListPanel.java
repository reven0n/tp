package nusemp.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

import nusemp.model.contact.Contact;

/**
 * Panel containing the list of contacts.
 */
public class ContactListPanel extends UiPart<Region> {

    private static final String FXML = "ContactListPanel.fxml";

    /* Width offset accounts for scrollbar, used for binding widths. */
    private static final int WIDTH_OFFSET = 12;
    @FXML
    private ListView<Contact> contactListView;

    /**
     * Creates a {@code ContactListPanel} with the given {@code ObservableList}.
     */
    public ContactListPanel(ObservableList<Contact> contactList) {
        super(FXML);
        contactListView.setItems(contactList);
        contactListView.setCellFactory(listView -> {
            ContactListViewCell cell = new ContactListViewCell();
            cell.prefWidthProperty().bind(listView.widthProperty().subtract(WIDTH_OFFSET));
            return cell;
        });
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Contact} using a {@code ContactCard}.
     */
    class ContactListViewCell extends ListCell<Contact> {
        @Override
        protected void updateItem(Contact contact, boolean empty) {
            super.updateItem(contact, empty);

            if (empty || contact == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new ContactCard(contact, getIndex() + 1, contactListView).getRoot());
            }
        }
    }

}
