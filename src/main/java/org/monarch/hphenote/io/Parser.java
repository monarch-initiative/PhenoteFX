package org.monarch.hphenote.io;

import java.io.File;

/**
 * Created by peter on 24.05.17.
 */
public class Parser {
    /** This variable will be initialized with the location of the File that was
     * downloaded and is to be parsed.
     */
    protected File absolutepath=null;

    public Parser() {

    }


    protected boolean inputFileExists() {
        return (this.absolutepath != null & absolutepath.exists());
    }
}
