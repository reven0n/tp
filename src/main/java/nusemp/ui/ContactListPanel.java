package nusemp.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

import nusemp.commons.core.LogsCenter;
import nusemp.model.AppData;
import nusemp.model.ParticipantEvent;
import nusemp.model.contact.Contact;

/**
 * Panel containing the list of contacts.
 */
public class ContactListPanel extends UiPart<Region> {
    private static final String FXML = "ContactListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(ContactListPanel.class);
    private final ObservableMap<Contact, List<ParticipantEvent>> contactEventMap;

    @FXML
    private ListView<Contact> contactListView;

    /**
     * Creates a {@code ContactListPanel} with the given {@code ObservableList}.
     */
    public ContactListPanel(ObservableList<Contact> contactList, ObservableMap<Contact, List<ParticipantEvent>> contactEventMap) {
        super(FXML);
        this.contactEventMap = contactEventMap;
        contactListView.setItems(contactList);
        contactListView.setCellFactory(listView -> new ContactListViewCell());

        // Add listener to refresh cells when the contact-event map changes
        contactEventMap.addListener((MapChangeListener<Contact, List<ParticipantEvent>>) change -> {
            contactListView.refresh();
        });
    }

    //ObservableMap<Contact, List<ParticipantEvent>> = appData.getContactEventMap();

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
                List<ParticipantEvent> events = contactEventMap.getOrDefault(contact, new ArrayList<>());
                setGraphic(new ContactCard(contact, getIndex() + 1, events).getRoot());
            }
        }
    }

}
