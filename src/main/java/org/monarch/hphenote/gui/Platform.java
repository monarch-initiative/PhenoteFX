package org.monarch.hphenote.gui;


import java.io.File;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Platform {

    private static String projectDirname = "projects";

    /**
     * Get path to directory where HRMD-gui stores global settings.
     * The path depends on underlying operating system. Linux, Windows & OSX
     * currently supported.
     * @return File to directory
     */
    public static File getHPhenoteDir() {
        CurrentPlatform platform = figureOutPlatform();

        File linuxPath = new File(System.getProperty("user.home") + File.separator + ".hphenote");
        File windowsPath = new File(System.getProperty("user.home") + File.separator + "hphenote");
        File osxPath = new File(System.getProperty("user.home") + File.separator + ".hphenote");

        switch (platform) {
            case LINUX: return linuxPath;
            case WINDOWS: return windowsPath;
            case OSX: return osxPath;
            case UNKNOWN: return null;
            default:
                Alert a = new Alert(AlertType.ERROR);
                a.setTitle("Find gui config dir");
                a.setHeaderText(null);
                a.setContentText(String.format("Unrecognized platform. %s", platform.toString()));
                a.showAndWait();
                return null;
        }
    }

    /**
     * Get path to directory whrere HRMD-gui stores XML files describing
     * projects. The method doesn't create a directory if it doesn't exist.
     * @return
     */
    public static File getProjectDir() {
        File hrmdDir = getHPhenoteDir();
        return new File(hrmdDir + File.separator + projectDirname);
    }


    public static File getParametersFile() {
        String parametersFileName = "parameters.yml";
        return new File(getHPhenoteDir() + File.separator + parametersFileName);
    }


    /* Based on this post: http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/ */
    private static CurrentPlatform figureOutPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0 || osName.indexOf("aix") >= 0) {
            return CurrentPlatform.LINUX;
        } else if (osName.indexOf("win") >= 0) {
            return CurrentPlatform.WINDOWS;
        } else if (osName.indexOf("mac") >= 0) {
            return CurrentPlatform.OSX;
        } else {
            return CurrentPlatform.UNKNOWN;
        }
    }

    public static boolean checkHPOFileDownloaded() {
        File hpo =  new File(getHPhenoteDir() + File.separator + "hp.obo");
        if ( hpo.exists())
            return true;
        else
            return false;
    }

    public static boolean checkMedgenFileDownloaded() {
        File medgen = new File(getHPhenoteDir() + File.separator + "MedGen_HPO_OMIM_Mapping.txt.gz");
        if (medgen.exists())
            return true;
        return false;
    }





    private enum CurrentPlatform {

        LINUX("Linux"),
        WINDOWS("Windows"),
        OSX("Os X"),
        UNKNOWN("Unknown");

        private String name;

        private CurrentPlatform(String n) {this.name = n; }

        @Override
        public String toString() { return this.name; }
    }



}