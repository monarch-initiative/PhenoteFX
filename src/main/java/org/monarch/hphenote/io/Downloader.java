package org.monarch.hphenote.io;


import javafx.concurrent.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Downloader {

    /** An error message, if an error occured */
    protected String errorMessage=null;

    /**
     * This is the absolute path to the place where downloaded file will be
     * saved in local filesystem.
     */
    private File localFilePath;

    /** This is the URL of the file we want to download */
    protected String urlstring=null;


   public  String getLocalFilePath() { return  localFilePath.getAbsolutePath(); }

    public Downloader( ) {
    }

    public void setFilePath(File path) {
        this.localFilePath = path;
    }

    public boolean hasError() {
        return this.errorMessage != null;
    }

    public String getError() {
        return this.errorMessage;
    }


    public void setURL(String url) {
        this.urlstring=url;
    }

    /**
     * This method downloads a file to the specified local file path. If the file already exists, it emits a warning
     * message and does nothing.
     */
    public Task<Void> download()  {

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                InputStream reader;
                FileOutputStream writer;

                int threshold = 0;
                int block = 250000;
                try {
                    URL url = new URL(urlstring);
                    URLConnection urlc = url.openConnection();
                    reader = urlc.getInputStream();
                    writer = new FileOutputStream(localFilePath);
                    byte[] buffer = new byte[153600];
                    int totalBytesRead = 0;
                    int bytesRead = 0;
                    int size = urlc.getContentLength();
                    if (size >= 0)
                        block = size / 20;
                    //System.err.println("0%       50%      100%");
                    while ((bytesRead = reader.read(buffer)) > 0) {
                        writer.write(buffer, 0, bytesRead);
                        buffer = new byte[153600];
                        totalBytesRead += bytesRead;
                        updateProgress(bytesRead,size);
                       /* if (totalBytesRead > threshold) {
                            //System.err.print("=");
                            threshold += block;

                        }*/
                    }
                    //System.err.println();
                    //System.err.println("[INFO] Done. " + (new Integer(totalBytesRead).toString()) + "(" + size + ") bytes read.");
                    writer.close();
                } catch (MalformedURLException e) {
                    System.err.println(String.format("Could not interpret url: \"%s\"\n%s", urlstring, e.toString()));
                } catch (IOException e) {
                    System.err.println(String.format("IO Exception reading from URL: \"%s\"\n%s", urlstring, e.toString()));
                }

                return null;
            }
        };
        return task;

    }

}