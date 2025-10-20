package nusemp.storage;

import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithoutEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import nusemp.commons.exceptions.IllegalValueException;
import nusemp.commons.util.JsonUtil;
import nusemp.model.AppData;
import nusemp.testutil.TypicalContacts;

public class JsonSerializableAppDataTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAppDataTest");
    private static final Path TYPICAL_CONTACTS_FILE = TEST_DATA_FOLDER.resolve("typicalContactsAppData.json");
    private static final Path INVALID_CONTACT_FILE = TEST_DATA_FOLDER.resolve("invalidContactAppData.json");
    private static final Path DUPLICATE_CONTACT_FILE = TEST_DATA_FOLDER.resolve("duplicateContactAppData.json");

    @Test
    public void toModelType_typicalContactsFile_success() throws Exception {
        JsonSerializableAppData dataFromFile = JsonUtil.readJsonFile(TYPICAL_CONTACTS_FILE,
                JsonSerializableAppData.class).get();
        AppData appDataFromFile = dataFromFile.toModelType();
        AppData typicalContactsAppData = getTypicalAppDataWithoutEvent();
        assertEquals(typicalContactsAppData, appDataFromFile);
    }

    @Test
    public void toModelType_invalidContactFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAppData dataFromFile = JsonUtil.readJsonFile(INVALID_CONTACT_FILE,
                JsonSerializableAppData.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateContacts_throwsIllegalValueException() throws Exception {
        JsonSerializableAppData dataFromFile = JsonUtil.readJsonFile(DUPLICATE_CONTACT_FILE,
                JsonSerializableAppData.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAppData.MESSAGE_DUPLICATE_CONTACT,
                dataFromFile::toModelType);
    }

}
