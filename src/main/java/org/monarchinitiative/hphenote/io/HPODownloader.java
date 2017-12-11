package org.monarchinitiative.hphenote.io;

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