package nusemp.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * A heading that displays on top of the contact or event list.
 */
public class ListHeading extends UiPart<Region> {
    private static final String FXML = "ListHeading.fxml";

    @FXML
    private VBox headingBox;
    @FXML
    private Label heading;


    /**
     * Constructs a {@link ListHeading}
     */
    public ListHeading(String headingText) {
        super(FXML);
        heading.setText(headingText);
    }
}
