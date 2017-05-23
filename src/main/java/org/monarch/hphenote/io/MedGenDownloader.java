package org.monarch.hphenote.io;

import java.io.File;

/**
 * Created by robinp on 5/23/17.
 */
public class MedGenDownloader extends Downloader {

        private String urlstring="ftp://ftp.ncbi.nlm.nih.gov/pub/medgen/MedGen_HPO_OMIM_Mapping.txt.gz";

        private String basename="MedGen_HPO_OMIM_Mapping.txt.gz";



        /**
         * Download MedGen_HPO_OMIM_Mapping.txt.gzgiven dataDirPaths
         */
        public MedGenDownloader() {
            File dir = org.monarch.hphenote.gui.Platform.getHPhenoteDir();
            File hpoPath = new File(dir + File.separator + basename);
            setFilePath(hpoPath);
            setURL(urlstring);
        }
    }


