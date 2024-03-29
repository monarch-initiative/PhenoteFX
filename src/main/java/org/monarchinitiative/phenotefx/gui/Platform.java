package org.monarchinitiative.phenotefx.gui;

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


import java.io.File;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Platform {

    /**
     * Get path to directory where HRMD-gui stores global settings.
     * The path depends on underlying operating system. Linux, Windows & OSX
     * currently supported.
     * @return File to directory
     */
    public static File getPhenoteFXDir() {
        CurrentPlatform platform = figureOutPlatform();

        File linuxPath = new File(System.getProperty("user.home") + File.separator + ".phenotefx");
        File windowsPath = new File(System.getProperty("user.home") + File.separator + "phenotefx");
        File osxPath = new File(System.getProperty("user.home") + File.separator + ".phenotefx");

        switch (platform) {
            case LINUX -> {
                return linuxPath;
            }
            case WINDOWS -> {
                return windowsPath;
            }
            case OSX -> {
                return osxPath;
            }
            case UNKNOWN -> {
                return null;
            }
            default -> {
                Alert a = new Alert(AlertType.ERROR);
                a.setTitle("Find gui config dir");
                a.setHeaderText(null);
                a.setContentText(String.format("Unrecognized platform. %s", platform));
                a.showAndWait();
                return null;
            }
        }
    }

    /**
     * Get the absolute path to the log file.
     * @return the absolute path,e.g., /home/user/.vpvgui/vpvgui.log
     */
    public static String getAbsoluteLogPath() {
        File dir = getPhenoteFXDir();
        return dir + File.separator +  "phenotefx.log";
    }


    /**
     * Get path to directory where PhenoteFX stores hp.obo.
     * @return path to the downloaded hp.obo file
     */
    public static File getLocalHpOboPath() {
        File phenoteFXpath = getPhenoteFXDir();
        return new File(phenoteFXpath + File.separator + "hp.obo");
    }


    public static File getParametersFile() {
        String parametersFileName = "parameters.yml";
        return new File(getPhenoteFXDir() + File.separator + parametersFileName);
    }


    /* Based on this post: http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/ */
    private static CurrentPlatform figureOutPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return CurrentPlatform.LINUX;
        } else if (osName.contains("win")) {
            return CurrentPlatform.WINDOWS;
        } else if (osName.contains("mac")) {
            return CurrentPlatform.OSX;
        } else {
            return CurrentPlatform.UNKNOWN;
        }
    }

    public static boolean checkHPOFileDownloaded() {
        File hpo =  new File(getPhenoteFXDir() + File.separator + "hp.json");
        return  hpo.exists();
    }

    public static boolean checkMedgenFileDownloaded() {
        File medgen = new File(getPhenoteFXDir() + File.separator + "MedGen_HPO_OMIM_Mapping.txt.gz");
        return medgen.exists();
    }

    public static boolean isMacintosh() {
        return figureOutPlatform().equals(CurrentPlatform.OSX);
    }





    private enum CurrentPlatform {

        LINUX("Linux"),
        WINDOWS("Windows"),
        OSX("Os X"),
        UNKNOWN("Unknown");

        private final String name;

        CurrentPlatform(String n) {this.name = n; }

        @Override
        public String toString() { return this.name; }
    }



}
