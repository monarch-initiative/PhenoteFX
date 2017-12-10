package org.monarchinitiative.hphenote.io;

import org.monarchinitiative.hphenote.gui.Platform;

import java.io.File;

/**
 * Convenience class that is used to download the latest version of {@code hp.obo} from our GitHub page.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.1
 */
public class HPODownloader extends Downloader {

    private String hpo_urlstring="https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.obo";

    /**
     * Download HP.obo file to given dataDir
     */
    public HPODownloader() {
        File dir = Platform.getHPhenoteDir();
        File hpoPath = new File(dir + File.separator + "hp.obo");
        setFilePath(hpoPath);
        setURL(hpo_urlstring);
    }

}