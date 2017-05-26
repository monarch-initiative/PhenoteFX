package org.monarch.hphenote.model;

/*
 * #%L
 * HPhenote
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


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.*;

public class Settings {

    /* Biocurator bean */
    private ObjectProperty<Biocurator> biocurator = new SimpleObjectProperty<>(this, "biocurator", new Biocurator());
    public final Biocurator getBiocurator() {return biocurator.get();}
    public final void setBiocurator(Biocurator newBiocurator) {biocurator.set(newBiocurator);}
    public ObjectProperty<Biocurator> bioCuratorIdProperty() {return biocurator;}

    /* Path to current HPO.obo file */
    private StringProperty hpoFile = new SimpleStringProperty(this, "hpoFile");
    public final String getHpoFile() {return hpoFile.get();}
    public final void setHpoFile(String newHpoFile) {hpoFile.set(newHpoFile);}
    public StringProperty hpoFileProperty() {return hpoFile;}

    /* Path to current medgen HPO OMIM file */
    private StringProperty medgenFile = new SimpleStringProperty(this, "medgenFile");
    public final String getMedgenFile() {return medgenFile.get();}
    public final void setMedgenFile(String newEntrezGeneFile) {medgenFile.set(newEntrezGeneFile);}
    public StringProperty medgenFileProperty() {return medgenFile;}


    public Settings() {}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Current settings:");
        sb.append(String.format("\nBiocurator ID:         %s", getBiocurator().getBioCuratorId()));
        sb.append(String.format("\nHPO file:              %s", getHpoFile()));
        sb.append(String.format("\nmedgen file:      %s", getMedgenFile()));
        sb.append("\n");
        return sb.toString();
    }

    private static String[] readPair(String line) {
        String pair[] = line.split(":");
        if (pair.length != 2)
            return null;
        pair[0] = pair[0].trim();
        pair[1] = pair[1].trim();
        return pair;
    }

    public static Settings factory(String path) {
        Settings settings = new Settings();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line=br.readLine())!=null) {
                String pair[]=readPair(line);
                if (pair==null)
                    continue;
                if (pair[0].toLowerCase().contains("biocurator id")) {
                    settings.getBiocurator().setBioCuratorId(pair[1]);
                } else if (pair[0].toLowerCase().contains("hpo file")) {
                    settings.setHpoFile(pair[1]);
                } else if (pair[0].toLowerCase().contains("medgen file")){
                    settings.setMedgenFile(pair[1]);
                } else {
                    System.err.println("Did not reciognize setting: " + line);
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
            bw.write(String.format("Biocurator ID: %s\n", settings.getBiocurator().getBioCuratorId()));
            bw.write(String.format("HPO file: %s\n", settings.getHpoFile()));
            bw.write(String.format("medgen file: %s", settings.getMedgenFile()));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}