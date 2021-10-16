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

import java.io.*;

public class Settings {

    /* Biocurator bean */
    private final StringProperty bioCuratorId = new SimpleStringProperty(this, "bioCuratorId");

    public void setBioCuratorId(String id) {
        this.bioCuratorId.setValue(id);
    }

    public String getBioCuratorId() {
        return this.bioCuratorId.getValue();
    }

    /* Path to current HPO.obo file */
    private final StringProperty hpoFile = new SimpleStringProperty(this, "hpoFile");

    public final String getHpoFile() {
        return hpoFile.get();
    }

    public final void setHpoFile(String newHpoFile) {
        hpoFile.set(newHpoFile);
    }

    public StringProperty hpoFileProperty() {
        return hpoFile;
    }

    /* Path to current medgen HPO OMIM file */
    private final StringProperty medgenFile = new SimpleStringProperty(this, "medgenFile");

    public final String getMedgenFile() {
        return medgenFile.get();
    }

    public final void setMedgenFile(String filename) {
        medgenFile.set(filename);
    }

    public StringProperty medgenFileProperty() {
        return medgenFile;
    }

    public String getDefaultDirectory() {
        return defaultDirectory;
    }

    public void setDefaultDirectory(String defaultDirectory) {
        this.defaultDirectory = defaultDirectory;
    }

    private final StringProperty mondoFile = new SimpleStringProperty(this, "mondoFile");

    public final String getMondoFile() { return mondoFile.get(); }

    public final void setMondoFile(String path) { this.mondoFile.set(path); }

    private final StringProperty ectoFile = new SimpleStringProperty(this, "ectoFile");

    public final String getEctoFile() { return this.ectoFile.get(); }

    public final void setEctoFile(String path) {
        this.ectoFile.set(path);
    }

    /** Place on the file system where the main files are stored (checked out GitHub repo). */
    private String defaultDirectory = null;



    public Settings() {
    }

    @Override
    public String toString() {
        return String.format("\nBiocurator ID: %s\nHPO file: %s\nmedgen file: %s\ndefault directory: %s\n",
                bioCuratorId.get(),
                getHpoFile(),
                getMedgenFile(),
                getDefaultDirectory());
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

    public static Settings factory(String path) {
        Settings settings = new Settings();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                String[] pair = readPair(line);
                if (pair == null)
                    continue;
                if (pair[0].toLowerCase().contains("biocurator id")) {
                    settings.setBioCuratorId(pair[1]);
                } else if (pair[0].toLowerCase().contains("hpo file")) {
                    settings.setHpoFile(pair[1]);
                } else if (pair[0].toLowerCase().contains("medgen file")) {
                    settings.setMedgenFile(pair[1]);
                } else if (pair[0].toLowerCase().contains("default directory")){
                    settings.setDefaultDirectory(pair[1]);
                } else if (pair[0].toLowerCase().contains("mondo file")) {
                    settings.setMondoFile(pair[1]);
                } else if (pair[0].toLowerCase().contains("ecto file")) {
                    settings.setEctoFile(pair[1]);
                } else {
                    System.err.println("Did not recognize setting: " + line);
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return settings;

    }

    public static boolean saveToFile(Settings settings, File settingsFile) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(settingsFile));
            bw.write(String.format("Biocurator ID: %s\n", settings.getBioCuratorId()));
            bw.write(String.format("HPO file: %s\n", settings.getHpoFile()));
            bw.write(String.format("medgen file: %s\n", settings.getMedgenFile()));
            bw.write(String.format("Default directory: %s\n", settings.getDefaultDirectory()));
            bw.write(String.format("Mondo file: %s\n", settings.getMondoFile()));
            bw.write(String.format("Ecto file: %s\n", settings.getEctoFile()));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}