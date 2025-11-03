// @@author
package nusemp.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import nusemp.commons.exceptions.DataLoadingException;
import nusemp.model.AppData;
import nusemp.model.ReadOnlyAppData;

/**
 * Represents a storage for {@link AppData}.
 */
public interface AppDataStorage {

    /**
     * Returns the file path of the data file.
     */
    Path getAppDataFilePath();

    /**
     * Returns app data as a {@link ReadOnlyAppData}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    Optional<ReadOnlyAppData> readAppData() throws DataLoadingException;

    /**
     * @see #getAppDataFilePath()
     */
    Optional<ReadOnlyAppData> readAppData(Path filePath) throws DataLoadingException;

    /**
     * Saves the given {@link ReadOnlyAppData} to the storage.
     * @param appData cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveAppData(ReadOnlyAppData appData) throws IOException;

    /**
     * @see #saveAppData(ReadOnlyAppData)
     */
    void saveAppData(ReadOnlyAppData appData, Path filePath) throws IOException;

}
