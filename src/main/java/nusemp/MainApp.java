package nusemp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;

import nusemp.commons.core.Config;
import nusemp.commons.core.LogsCenter;
import nusemp.commons.core.Version;
import nusemp.commons.exceptions.DataLoadingException;
import nusemp.commons.util.ConfigUtil;
import nusemp.commons.util.FileUtil;
import nusemp.commons.util.StringUtil;
import nusemp.logic.Logic;
import nusemp.logic.LogicManager;
import nusemp.model.AppData;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.ReadOnlyAppData;
import nusemp.model.ReadOnlyUserPrefs;
import nusemp.model.UserPrefs;
import nusemp.model.util.SampleDataUtil;
import nusemp.storage.AppDataStorage;
import nusemp.storage.JsonAppDataStorage;
import nusemp.storage.JsonUserPrefsStorage;
import nusemp.storage.Storage;
import nusemp.storage.StorageManager;
import nusemp.storage.UserPrefsStorage;
import nusemp.ui.Ui;
import nusemp.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 4, 0, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    // Store corruption details to show alert after UI initializes
    private String corruptionBackupPath = null;
    private String corruptionErrorDetails = null;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing NUS EMP ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());
        initLogging(config);

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        AppDataStorage appDataStorage = new JsonAppDataStorage(userPrefs.getAppDataFilePath());
        storage = new StorageManager(appDataStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s app data and {@code userPrefs}. <br>
     * The data from the sample app data will be used instead if {@code storage}'s app data is not found,
     * or an empty app data will be used instead if errors occur when reading {@code storage}'s app data.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getAppDataFilePath());

        Optional<ReadOnlyAppData> appDataOptional;
        ReadOnlyAppData initialData;
        try {
            appDataOptional = storage.readAppData();
            if (!appDataOptional.isPresent()) {
                logger.info("Creating a new data file " + storage.getAppDataFilePath()
                        + " populated with a sample app data.");
            }
            initialData = appDataOptional.orElseGet(SampleDataUtil::getSampleAppData);
        } catch (DataLoadingException e) {
            Path dataFilePath = storage.getAppDataFilePath();
            logger.warning("Data file at " + dataFilePath + " is corrupted and could not be loaded.");

            // Create backup of corrupted file
            try {
                Path backupPath = FileUtil.createBackup(dataFilePath);
                corruptionBackupPath = backupPath.toString();
                logger.info("Corrupted data file backed up to: " + backupPath);
            } catch (IOException backupError) {
                logger.severe("Failed to create backup of corrupted file: " + backupError.getMessage());
                corruptionBackupPath = "Failed to create backup: " + backupError.getMessage();
            }

            // Store detailed error message
            corruptionErrorDetails = e.getDetailedMessage();
            logger.warning("Corruption details: " + corruptionErrorDetails);

            // Start with empty data
            initialData = new AppData();
        }

        return new ModelManager(initialData, userPrefs);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            if (!configOptional.isPresent()) {
                logger.info("Creating new config file " + configFilePathUsed);
            }
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataLoadingException e) {
            logger.warning("Config file at " + configFilePathUsed + " could not be loaded."
                    + " Using default config properties.");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting NUS EMP " + MainApp.VERSION);
        ui.start(primaryStage);

        // Show corruption alert if data was corrupted during initialization
        if (corruptionBackupPath != null) {
            UiManager uiManager = (UiManager) ui;
            uiManager.showDataCorruptionAlert(corruptionBackupPath, corruptionErrorDetails);
        }
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping NUS EMP ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
