package org.monarchinitiative.phenotefx.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class Settings {
    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
    private static final String BIOCURATOR_ID_KEY = "biocurator";
    private static final String HPO_JSON_KEY = "hpo.json";
    private static final String DATA_DIR_KEY = "annotation.data.path";
    private static final String settingsFileName = "phenotefx.settings";

    private final StringProperty bioCuratorId = new SimpleStringProperty(this, "bioCuratorId");
    private final StringProperty hpoFile = new SimpleStringProperty(this, "hpoFile");
    private String defaultDirectory = null;
    private final String settingsFilePath;

    public void setBioCuratorId(String id) {
        if (id != null && id.contains("\\")) {
            id = id.replace("\\", "");
        }
        this.bioCuratorId.setValue(id);
    }

    public String getBioCuratorId() {
        return this.bioCuratorId.getValue();
    }

    public final String getHpoFile() {
        return hpoFile.get();
    }

    public final void setHpoFile(String newHpoFile) {
        hpoFile.set(newHpoFile);
    }

    public StringProperty hpoFileProperty() {
        return hpoFile;
    }

    public String getAnnotationFileDirectory() {
        return defaultDirectory;
    }

    public void setDefaultDirectory(String defaultDirectory) {
        this.defaultDirectory = defaultDirectory;
    }

    public Settings(String path) {
        this.settingsFilePath = path;
        File file = new File(path);
        if (!file.exists()) {
            LOGGER.info("No settings file found at initialization path: {}", path);
            return;
        }

        Properties props = new Properties();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            props.load(br);
            if (props.containsKey(BIOCURATOR_ID_KEY)) setBioCuratorId(props.getProperty(BIOCURATOR_ID_KEY));
            if (props.containsKey(HPO_JSON_KEY)) setHpoFile(props.getProperty(HPO_JSON_KEY));
            if (props.containsKey(DATA_DIR_KEY)) setDefaultDirectory(props.getProperty(DATA_DIR_KEY));
        } catch (IOException e) {
            LOGGER.error("Error reading settings file: {}", e.getMessage());
        }
    }

    public static Settings fromDefaultPath() {
        File phenoteFXDir = Platform.getPhenoteFXDir();
        if (phenoteFXDir == null) {
            throw new PhenolRuntimeException("Platform.getPhenoteFXDir() returned null");
        }
        if (!phenoteFXDir.exists()) {
            boolean created = phenoteFXDir.mkdirs();
            if (!created) {
                LOGGER.error("Failed to create configuration directory: {}", phenoteFXDir.getAbsolutePath());
                throw new PhenolRuntimeException("Could not create configuration directory");
            }
        }

        File settingsFile = new File(phenoteFXDir, settingsFileName);
        if (settingsFile.exists()) {
            LOGGER.info("Opening existing settings file at {}", settingsFile.getAbsolutePath());
        } else {
            LOGGER.info("Settings file does not exist yet. Will be created upon save at {}", settingsFile.getAbsolutePath());
        }

        return new Settings(settingsFile.getAbsolutePath());
    }

    public boolean saveToFile() {
        Properties props = new Properties();
        // Fallback checks to prevent writing 'null' literal strings to your config
        props.setProperty(BIOCURATOR_ID_KEY, getBioCuratorId() != null ? getBioCuratorId() : "");
        props.setProperty(HPO_JSON_KEY, getHpoFile() != null ? getHpoFile() : "");
        props.setProperty(DATA_DIR_KEY, getAnnotationFileDirectory() != null ? getAnnotationFileDirectory() : "");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.settingsFilePath))) {
            props.store(bw, "PhenoteFX User Settings");
            LOGGER.info("Successfully saved settings to {}", this.settingsFilePath);
            return true;
        } catch (IOException e) {
            LOGGER.error("Failed to save settings to file: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("%s=%s\n%s=%s\n%s=%s\n",
                BIOCURATOR_ID_KEY, bioCuratorId.get(),
                HPO_JSON_KEY, getHpoFile(),
                DATA_DIR_KEY, getAnnotationFileDirectory());
    }
}