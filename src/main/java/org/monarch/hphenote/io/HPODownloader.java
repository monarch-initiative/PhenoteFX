package org.monarch.hphenote.io;

import java.io.File;

public class HPODownloader extends Downloader {

    private String hpo_urlstring="https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.obo";

    /**
     * Download HP.obo file to given dataDir
     */
    public HPODownloader() {
        File dir = org.monarch.hphenote.gui.Platform.getHPhenoteDir();
        File hpoPath = new File(dir + File.separator + "hp.obo");
        setFilePath(hpoPath);
        setURL(hpo_urlstring);
    }

}