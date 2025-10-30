package nusemp.storage;

import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalAppData.getTypicalAppData;
import static nusemp.testutil.TypicalContacts.ALICE;
import static nusemp.testutil.TypicalContacts.HOON;
import static nusemp.testutil.TypicalContacts.IDA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nusemp.commons.exceptions.DataLoadingException;
import nusemp.model.AppData;
import nusemp.model.ReadOnlyAppData;

public class JsonAppDataStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAppDataStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAppData_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAppData(null));
    }

    private Optional<ReadOnlyAppData> readAppData(String filePath) throws Exception {
        return new JsonAppDataStorage(Paths.get(filePath)).readAppData(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAppData("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAppData("notJsonFormatAppData.json"));
    }

    @Test
    public void readAppData_invalidContactAppData_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAppData("invalidContactAppData.json"));
    }

    @Test
    public void readAppData_invalidAndValidContactAppData_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAppData("invalidAndValidContactAppData.json"));
    }

    @Test
    public void readAndSaveAppData_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAppData.json");
        AppData original = getTypicalAppData();
        JsonAppDataStorage jsonAppDataStorage = new JsonAppDataStorage(filePath);

        // Save in new file and read back
        jsonAppDataStorage.saveAppData(original, filePath);
        ReadOnlyAppData readBack = jsonAppDataStorage.readAppData(filePath).get();
        assertEquals(original, new AppData(readBack));

        // Modify data, overwrite exiting file, and read back
        original.addContact(HOON);
        original.removeContact(ALICE);
        jsonAppDataStorage.saveAppData(original, filePath);
        readBack = jsonAppDataStorage.readAppData(filePath).get();
        assertEquals(original, new AppData(readBack));

        // Save and read without specifying file path
        original.addContact(IDA);
        jsonAppDataStorage.saveAppData(original); // file path not specified
        readBack = jsonAppDataStorage.readAppData().get(); // file path not specified
        assertEquals(original, new AppData(readBack));

    }

    @Test
    public void saveAppData_nullAppData_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAppData(null, "SomeFile.json"));
    }

    /**
     * Saves {@code appData} at the specified {@code filePath}.
     */
    private void saveAppData(ReadOnlyAppData appData, String filePath) {
        try {
            new JsonAppDataStorage(Paths.get(filePath))
                    .saveAppData(appData, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAppData_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAppData(new AppData(), null));
    }
}
