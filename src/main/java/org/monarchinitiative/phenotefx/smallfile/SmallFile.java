package org.monarchinitiative.phenotefx.smallfile;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2018 Peter Robinson
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

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * This class represents one disease-entity annotation (one line in a HPOA file).
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * Created by peter on 1/20/2018.
 */
public class SmallFile {
    private static final Logger logger = LoggerFactory.getLogger(SmallFile.class);
    /** The base name of the HPOA file. */
    private final String basename;
    /** List of {@link SmallFileEntry} objects representing the original lines of the small file */
    private final List<SmallFileEntry> originalEntryList;


    public String getBasename() {
        return basename;
    }

    /** The constructor creates an immutable copy of the original list of {@link SmallFileEntry} objects
     * privided by tghe parser
     * @param name Name of the "small file"
     * @param entries List of {@link SmallFileEntry} objects -- one per line of the small file.
     */
    public SmallFile(String name, List<SmallFileEntry> entries) {
        basename=name;
        originalEntryList = ImmutableList.copyOf(entries);
    }

    /** @return original {@link SmallFileEntry} objects -- one per line of the small file.*/
    public List<SmallFileEntry> getOriginalEntryList() {
        return originalEntryList;
    }

    public int getNumberOfAnnotations() { return originalEntryList.size(); }


}
