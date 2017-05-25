package org.monarch.hphenote.io;

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
            File medgenpath = new File(dir + File.separator + basename);
            setFilePath(medgenpath);
            setURL(urlstring);
        }
    }


