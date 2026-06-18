package org.monarchinitiative.phenotefx.smallfile;

import java.util.List;


/**
 * This class represents one disease-entity annotation (one line in a HPOA file).
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * Created by peter on 1/20/2018.
 */
public class SmallFile {
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
        originalEntryList = List.copyOf(entries);
    }

    /** @return original {@link SmallFileEntry} objects -- one per line of the small file.*/
    public List<SmallFileEntry> getOriginalEntryList() {
        return originalEntryList;
    }

    public int getNumberOfAnnotations() { return originalEntryList.size(); }


}
