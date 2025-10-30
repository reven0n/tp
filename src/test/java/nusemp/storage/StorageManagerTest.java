package nusemp.storage;

import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithoutEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nusemp.commons.core.GuiSettings;
import nusemp.model.AppData;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.UserPrefs;

public class StorageManagerTest {

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
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6, true));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void appDataReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonAppDataStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonAppDataStorageTest} class.
         */
        AppData original = getTypicalAppDataWithoutEvent();
        storageManager.saveAppData(original);
        ReadOnlyAppData retrieved = storageManager.readAppData().get();
        assertEquals(original, new AppData(retrieved));
    }

    @Test
    public void getAppDataFilePath() {
        assertNotNull(storageManager.getAppDataFilePath());
    }

}
