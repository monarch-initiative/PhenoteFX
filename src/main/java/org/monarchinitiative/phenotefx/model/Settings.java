package org.monarchinitiative.phenotefx.model;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 Peter Robinson
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.monarchinitiative.phenotefx.gui.PopUps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Settings {
    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
    private static final String BIOCURATOR_ID_KEY = "biocurator";
    private static final String HPO_JSON_KEY = "hpo.json";
    private static final String DATA_DIR_KEY = "annotation.data.path";


    private static final String settingsFileName = "phenotefx.settings";

    private final String settingsFilePath;
    /* Biocurator bean */
    private final StringProperty bioCuratorIdProperty = new SimpleStringProperty();
    /* Path to current HPO.obo file */
    private final StringProperty hpoFile = new SimpleStringProperty();

    /** Place on the file system where the main files are stored (checked out GitHub repo). */
    private final StringProperty defaultDirectory = new SimpleStringProperty();



    public void setBioCuratorId(String id) {
        if (id.contains("\\")) {
            // why is this happening?
            //PopUps.showErrorMessage("Attempt to set biocurator id with slash -- removing");
            id = id.replace("\\","");
        }
        this.bioCuratorIdProperty.setValue(id);
        saveToFile();
    }

    public String getBioCuratorId() {
        return this.bioCuratorIdProperty.getValue();
    }



    public final String getHpoFile() {
        return hpoFile.get();
    }

    public final void setHpoFile(String newHpoFile) {
        hpoFile.set(newHpoFile);
        saveToFile();
    }

    public StringProperty hpoFileProperty() {
        return hpoFile;
    }

    public String getAnnotationFileDirectory() {
        return defaultDirectory.getValue();
    }

    public void setDefaultDirectory(String defaultDirectory) {
        this.defaultDirectory.set(defaultDirectory);
        saveToFile();
    }



    public Settings(String path) {
        settingsFilePath = path;
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || ! line.contains("=")) {
                    continue; // skip comments
                }
                String[] pair = line.split("=");
                if (pair.length < 2)
                    continue;
                switch (pair[0]) {
                    case BIOCURATOR_ID_KEY -> setBioCuratorId(pair[1]);
                    case HPO_JSON_KEY -> setHpoFile(pair[1]);
                    case DATA_DIR_KEY -> setDefaultDirectory(pair[1]);
                    default -> LOGGER.error("Did not recognize setting: {}", line);//should never happen
                }
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return String.format("%s=%s\n%s=%s\n%s=%s\n",
                BIOCURATOR_ID_KEY,
                bioCuratorIdProperty.getValue(),
                HPO_JSON_KEY,
                getHpoFile(),
                DATA_DIR_KEY,
                getAnnotationFileDirectory());
    }

    private static String[] readPair(String line) {
        int i = line.indexOf(':');
        if (i < 0) {
            System.out.println("[WARN] Could not read settings line: " + line);
            return null;
        }
        String[] pair = new String[2];
        pair[0] = line.substring(0, i).trim();
        pair[1] = line.substring(i + 1).trim();
        return pair;
    }




    public static Settings fromDefaultPath() {
        File phenoteFXDir = Platform.getPhenoteFXDir();
        if (phenoteFXDir == null) {
            throw new PhenolRuntimeException("Platform.getPhenoteFXDir() returned null");
        }
        LOGGER.info("Reading settings from {}", phenoteFXDir.getAbsoluteFile());
        if (!phenoteFXDir.exists()) {
            try {
                boolean res = phenoteFXDir.createNewFile();
                if (! res) {
                    PopUps.showInfoMessage("Error", "Could not created new file");
                }
            } catch (IOException e) {
                PopUps.showInfoMessage("Error saving settings. Settings not saved.", "Warning");
                throw new PhenolRuntimeException("Could not create new settings file");            }
        }
        File settingsFile = new File(phenoteFXDir.getAbsolutePath()
                + File.separator + settingsFileName);
        if (settingsFile.isFile()) {
            LOGGER.info("Opening existing settings file at {}", settingsFile.getAbsoluteFile());
        } else {
            LOGGER.info("Creating new settings file at {}", settingsFile.getAbsoluteFile());
        }
        return new Settings(settingsFile.getAbsolutePath());
    }


    public void saveToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.settingsFilePath));
            bw.write(String.format("%s=%s\n", BIOCURATOR_ID_KEY, getBioCuratorId()));
            bw.write(String.format("%s=%s\n", HPO_JSON_KEY,  getHpoFile()));
            bw.write(String.format("%s=%s\n", DATA_DIR_KEY, getAnnotationFileDirectory()));
            bw.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}