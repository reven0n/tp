package nusemp.storage;

import static nusemp.testutil.Assert.assertThrows;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithoutEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import nusemp.commons.exceptions.IllegalValueException;
import nusemp.commons.util.JsonUtil;
import nusemp.model.AppData;
import nusemp.model.contact.Contact;
import nusemp.model.event.Event;

public class JsonSerializableAppDataTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAppDataTest");
    private static final Path TYPICAL_CONTACTS_FILE = TEST_DATA_FOLDER.resolve("typicalContactsAppData.json");
    private static final Path TYPICAL_APPDATA_FILE = TEST_DATA_FOLDER.resolve("typicalAppData.json");
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

    @Test
    public void toModelType_bidirectionalLinking_success() throws Exception {
        JsonSerializableAppData dataFromFile = JsonUtil.readJsonFile(TYPICAL_APPDATA_FILE,
                JsonSerializableAppData.class).get();
        AppData appData = dataFromFile.toModelType();

        // Find Alex Yeoh and Bernice Yu
        Contact alex = appData.getContactList().stream()
                .filter(c -> c.getEmail().value.equals("alexyeoh@example.com"))
                .findFirst()
                .orElse(null);

        Contact bernice = appData.getContactList().stream()
                .filter(c -> c.getEmail().value.equals("berniceyu@example.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(alex);
        assertNotNull(bernice);

        // Check using ParticipantMap
        assertEquals(2, appData.getEventsForContact(alex).size());
        assertEquals(2, appData.getEventsForContact(bernice).size());

        // Verify event participation
        Event teamMeeting = appData.getEventList().stream()
                .filter(e -> e.getName().value.equals("Team Meeting"))
                .findFirst()
                .orElse(null);

        Event marathon = appData.getEventList().stream()
                .filter(e -> e.getName().value.equals("Marathon"))
                .findFirst()
                .orElse(null);

        assertNotNull(teamMeeting);
        assertTrue(appData.hasParticipantEvent(alex, teamMeeting));
        assertTrue(appData.hasParticipantEvent(bernice, teamMeeting));
        assertTrue(appData.hasParticipantEvent(alex, marathon));
        assertTrue(appData.hasParticipantEvent(bernice, marathon));
    }

    @Test
    public void toModelType_typicalAppDataFile_success() throws Exception {
        JsonSerializableAppData dataFromFile = JsonUtil.readJsonFile(TYPICAL_APPDATA_FILE,
                JsonSerializableAppData.class).get();
        AppData appDataFromFile = dataFromFile.toModelType();

        assertNotNull(appDataFromFile);
        assertEquals(6, appDataFromFile.getContactList().size());
        assertEquals(2, appDataFromFile.getEventList().size());
    }

    @Test
    public void toModelType_contactsWithoutEvents_success() throws Exception {
        JsonSerializableAppData dataFromFile = JsonUtil.readJsonFile(TYPICAL_APPDATA_FILE,
                JsonSerializableAppData.class).get();
        AppData appData = dataFromFile.toModelType();

        // Find contacts not in any event
        Contact charlotte = appData.getContactList().stream()
                .filter(c -> c.getEmail().value.equals("charlotte@example.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(charlotte);
        assertEquals(0, charlotte.getEvents().size());
    }

    @Test
    public void toModelType_eventsHaveParticipants_success() throws Exception {
        JsonSerializableAppData dataFromFile = JsonUtil.readJsonFile(TYPICAL_APPDATA_FILE,
                JsonSerializableAppData.class).get();
        AppData appData = dataFromFile.toModelType();

        Event teamMeeting = appData.getEventList().stream()
                .filter(e -> e.getName().value.equals("Team Meeting"))
                .findFirst()
                .orElse(null);

        assertNotNull(teamMeeting);

        // Check using ParticipantMap
        List<Contact> participants = appData.getContactsForEvent(teamMeeting);
        assertEquals(2, participants.size());

        // Verify participants
        assertTrue(participants.stream()
                .anyMatch(c -> c.getEmail().value.equals("alexyeoh@example.com")));
        assertTrue(participants.stream()
                .anyMatch(c -> c.getEmail().value.equals("berniceyu@example.com")));
    }

}
