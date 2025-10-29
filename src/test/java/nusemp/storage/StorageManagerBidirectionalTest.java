package nusemp.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nusemp.model.AppData;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;

public class StorageManagerBidirectionalTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAppDataTest");

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;


    @BeforeEach
    public void setUp() {
        JsonAppDataStorage appData = new JsonAppDataStorage(getTempFilePath("ad"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        storageManager = new StorageManager(appData, userPrefsStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void readAppData_withLinkedEvents_bidirectionalLinkingMaintained() throws Exception {
        // Use the test appdata.json from data folder
        Path appdataPath = TEST_DATA_FOLDER.resolve("typicalAppData.json");
        ReadOnlyAppData retrieved = storageManager.readAppData(appdataPath).get();
        AppData appData = new AppData(retrieved);

        Contact alex = appData.getContactList().stream()
                .filter(c -> c.getEmail().value.equals("alexyeoh@example.com"))
                .findFirst()
                .orElseThrow();

        Event teamMeeting = appData.getEventList().stream()
                .filter(e -> e.getName().value.equals("Team Meeting"))
                .findFirst()
                .orElseThrow();

        // Verify bidirectional linking through ParticipantMap
        assertTrue(appData.getEventsForContact(alex).contains(teamMeeting));
        assertTrue(appData.getContactsForEvent(teamMeeting).contains(alex));
    }

    @Test
    public void saveAndReadAppData_bidirectionalLinkingPreserved() throws Exception {
        ReadOnlyAppData original = storageManager.readAppData(TEST_DATA_FOLDER.resolve("typicalAppData.json")).get();
        storageManager.saveAppData(original);
        ReadOnlyAppData retrieved = storageManager.readAppData().get();

        AppData originalAppData = new AppData(original);
        AppData retrievedAppData = new AppData(retrieved);

        assertEquals(originalAppData, retrievedAppData);
    }
}
