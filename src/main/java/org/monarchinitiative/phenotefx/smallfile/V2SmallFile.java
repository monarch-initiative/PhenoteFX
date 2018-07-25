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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;


/**
 * This class represents one disease-entity annotation  consisting usually of multiple annotations lines, and using
 * the new format introduced in 2018. The constructor will need to be adapted to input the new V2 file format
 * once the dust has settled. TODO
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * Created by peter on 1/20/2018.
 */
public class V2SmallFile {
    private static final Logger logger = LogManager.getLogger();
    /** The base name of the V2 file, which is the same as the v1 small file. */
    private final String basename;
    /** List of {@link V2SmallFileEntry} objects representing the original lines of the small file */
    private final List<V2SmallFileEntry> originalEntryList;


    public String getBasename() {
        return basename;
    }

    /** The constructor creates an immutable copy of the original list of {@link V2SmallFileEntry} objects
     * privided by tghe parser
     * @param name Name of the "small file"
     * @param entries List of {@link V2SmallFileEntry} objects -- one per line of the small file.
     */
    public V2SmallFile(String name, List<V2SmallFileEntry> entries) {
        basename=name;
        originalEntryList = ImmutableList.copyOf(entries);
    }

    /** @return original {@link V2SmallFileEntry} objects -- one per line of the small file.*/
    public List<V2SmallFileEntry> getOriginalEntryList() {
        return originalEntryList;
    }

    public int getNumberOfAnnotations() { return originalEntryList.size(); }


}
