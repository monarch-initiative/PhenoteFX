package org.monarchinitiative.phenotefx.io;

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

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;


import org.monarchinitiative.phenotefx.gui.PopUps;
import org.monarchinitiative.phenotefx.gui.help.HelpPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class is used to download files to the local file system of the user (chromFa.tar.gz and refGene.txt.gz).
 * @author Peter Robinson
 * @version 0.2.0 (2017-10-20)
 */
public class Downloader extends Task<Void> {
    private static final Logger logger = LoggerFactory.getLogger(Downloader.class);    /**
     * The absolute path to the place (directory) where the downloaded file will be
     * saved in the local filesystem.*/
    private File localDir=null;

    /**
     * The full local path of the file we will download. It should be set to be identical
     * to {@link #localDir} except for the final file base name.
     */
    private File localFilePath=null;

    /** A reference to a ProgressIndicator that must have be
     * initialized in the GUI and not within this class.
     */
    private ProgressIndicator progress=null;

    /** This is the URL of the file we want to download */
    private String urlstring=null;

    private Downloader(File directoryPath, String url, String basename) {
        this.localDir = directoryPath;
        this.urlstring=url;
        setLocalFilePath(basename);
        makeDirectoryIfNotExist();
    }

    private Downloader(String path, String url, String basename) {
        this(new File(path),url,basename);
    }

    public Downloader(String path, String url, String basename, ProgressIndicator pi) {
        this(path,url,basename);
        this.progress = pi;
    }

    public Downloader(File path, String url, String basename, ProgressIndicator pi) {
        this(path,url,basename);
        this.progress = pi;
    }


    protected File getLocalFilePath() { return  this.localFilePath; }

    protected void setLocalFilePath (String bname) {
        this.localFilePath = new File(this.localDir + File.separator + bname);
        logger.debug("setLocalFilepath for download to: "+localFilePath);
    }

    /**
     * @param url Subclasses need to set this to the URL of the resource to be downloaded. Alternatively,
     * client code needs to set it.
     */
    public void setURL(String url) {
        this.urlstring=url;
    }

    /**
     * This method downloads a file to the specified local file path. If the file already exists, it emits a warning
     * message and does nothing.
     */
    @Override
    protected Void call() {
        logger.debug("[INFO] Downloading: \"" + urlstring + "\"");
        InputStream reader;
        FileOutputStream writer;

        int threshold = 0;
        int block = 250000;
        try {
            URL url = new URL(urlstring);
            URLConnection urlc = url.openConnection();
            reader = urlc.getInputStream();
            logger.trace("URL host: "+ url.getHost() + "\n reader available="+reader.available());
            logger.trace("LocalFilePath: "+localFilePath);
            writer = new FileOutputStream(localFilePath);
            byte[] buffer = new byte[153600];
            int totalBytesRead = 0;
            int bytesRead;
            int size = urlc.getContentLength();
            if (progress!=null) { updateProgress(0.01); }
            logger.trace("Size of file to be downloaded: "+size);
            if (size >= 0)
                block = size /100;
            while ((bytesRead = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, bytesRead);
                buffer = new byte[153600];
                totalBytesRead += bytesRead;
                if (size>0 && totalBytesRead > threshold) {
                    updateProgress((double)totalBytesRead/size);
                    threshold += block;
                }
            }
            logger.info("Successful download from "+urlstring+": " + (totalBytesRead) + "(" + size + ") bytes read.");
            writer.close();
        } catch (MalformedURLException e) {
            updateProgress(0.00);
            showException(String.format("Malformed url: \"%s\"\n%s", urlstring, e));
        } catch (IOException e) {
            updateProgress(0.00);
            showException(String.format("IO Exception reading from URL: \"%s\" to local file \"%s\"\n%s", urlstring,localFilePath, e));
        } catch (Exception e){
            updateProgress(0.00);
            showException(e.getMessage());
        }
        updateProgress(1.000); /* show 100% completion */
        return null;
    }


    private void showException (String e) {
        Platform.runLater( () -> { // new Runnable
                PopUps.showInfoMessage(e,"Download Error");
        });
    }





    /** Update the progress bar of the GUI in a separate thread.
     * @param pr Current progress.
     */
    private void updateProgress(double pr) {
        javafx.application.Platform.runLater( //new Runnable
           () -> {
                if (progress==null) {
                    logger.error("NULL pointer to download progress indicator");
                    return;
                }
                progress.setProgress(pr);
        });
    }

    /**
     * This function creates a new directory to store the downloaded file. If the directory already exists, it
     *  does nothing.
     */
    private void makeDirectoryIfNotExist() {
        if (localDir==null) {
            logger.error("Null pointer passed, unable to make directory.");
            return;
        }
        if (! this.localDir.getParentFile().exists()) {
            logger.info("Creating directory: "+ localDir);
            this.localDir.mkdir();
        }
    }

}
