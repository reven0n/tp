// @@author
package nusemp.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import nusemp.commons.core.LogsCenter;
import nusemp.commons.exceptions.DataLoadingException;
import nusemp.commons.exceptions.IllegalValueException;
import nusemp.commons.util.FileUtil;
import nusemp.commons.util.JsonUtil;
import nusemp.model.ReadOnlyAppData;

/**
 * A class to access AppData data stored as a json file on the hard disk.
 */
public class JsonAppDataStorage implements AppDataStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonAppDataStorage.class);

    private Path filePath;

    public JsonAppDataStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getAppDataFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyAppData> readAppData() throws DataLoadingException {
        return readAppData(filePath);
    }

    /**
     * Similar to {@link #readAppData()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAppData> readAppData(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableAppData> jsonAppData = JsonUtil.readJsonFile(
                filePath, JsonSerializableAppData.class);
        if (!jsonAppData.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonAppData.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    @Override
    public void saveAppData(ReadOnlyAppData appData) throws IOException {
        saveAppData(appData, filePath);
    }

    /**
     * Similar to {@link #saveAppData(ReadOnlyAppData)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveAppData(ReadOnlyAppData appData, Path filePath) throws IOException {
        requireNonNull(appData);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableAppData(appData), filePath);
    }

}
