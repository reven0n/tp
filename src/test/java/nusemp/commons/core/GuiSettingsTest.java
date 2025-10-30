package nusemp.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class GuiSettingsTest {
    @Test
    public void toStringMethod() {
        GuiSettings guiSettings = new GuiSettings();
        String expected = GuiSettings.class.getCanonicalName()
                + "{windowWidth=" + guiSettings.getWindowWidth()
                + ", windowHeight=" + guiSettings.getWindowHeight()
                + ", windowCoordinates=" + guiSettings.getWindowCoordinates()
                + ", isDarkTheme=" + guiSettings.isDarkTheme() + "}";
        assertEquals(expected, guiSettings.toString());
    }
}
