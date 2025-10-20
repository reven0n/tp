package nusemp.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import nusemp.commons.exceptions.DataLoadingException;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.ReadOnlyUserPrefs;
import nusemp.model.UserPrefs;

/**
 * API of the Storage component
 */
public interface Storage extends AppDataStorage, UserPrefsStorage {

    @Override
    Optional<UserPrefs> readUserPrefs() throws DataLoadingException;

    @Override
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    @Override
    Path getAppDataFilePath();

    @Override
    Optional<ReadOnlyAppData> readAppData() throws DataLoadingException;

    @Override
    void saveAppData(ReadOnlyAppData appData) throws IOException;

}
