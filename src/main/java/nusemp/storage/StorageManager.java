package nusemp.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import nusemp.commons.core.LogsCenter;
import nusemp.commons.exceptions.DataLoadingException;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.ReadOnlyUserPrefs;
import nusemp.model.UserPrefs;

/**
 * Manages storage of AppData data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private AppDataStorage appDataStorage;
    private UserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given {@code AppDataStorage} and {@code UserPrefStorage}.
     */
    public StorageManager(AppDataStorage appDataStorage, UserPrefsStorage userPrefsStorage) {
        this.appDataStorage = appDataStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ AppData methods ==============================

    @Override
    public Path getAppDataFilePath() {
        return appDataStorage.getAppDataFilePath();
    }

    @Override
    public Optional<ReadOnlyAppData> readAppData() throws DataLoadingException {
        return readAppData(appDataStorage.getAppDataFilePath());
    }

    @Override
    public Optional<ReadOnlyAppData> readAppData(Path filePath) throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + filePath);
        return appDataStorage.readAppData(filePath);
    }

    @Override
    public void saveAppData(ReadOnlyAppData appData) throws IOException {
        saveAppData(appData, appDataStorage.getAppDataFilePath());
    }

    @Override
    public void saveAppData(ReadOnlyAppData appData, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        appDataStorage.saveAppData(appData, filePath);
    }

}
