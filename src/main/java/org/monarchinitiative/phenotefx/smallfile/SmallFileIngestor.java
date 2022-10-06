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


import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenotefx.exception.PhenoteFxException;
import org.monarchinitiative.phenotefx.io.SmallfileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class coordinates the input of all the small files. If an
 * {@code omit-list.txt} is provided by the user, then these files are
 * omitted. The output of this class is a list of {@link SmallFile} objects
 * @author <a href="mailto:peter.robinson@jjax.org">Peter Robinson</a>
 */
public class SmallFileIngestor {
    private static final Logger logger = LoggerFactory.getLogger(SmallFileIngestor.class);    /** Reference to the HPO object. */
    private final Ontology ontology;
    /** The paths to all of the small files. */
    private final List<String> smallFilePaths;
    /** List of all of the {@link SmallFile} objects, which represent annotated diseases. */
    private final List<SmallFile> smallFileList =new ArrayList<>();
    /** Names of entries (small files) that we will omit because they do not represent diseases. */
    private final Set<String> omitEntries;

    /** Total number of annotations of all of the annotation files. */
    private int n_total_annotation_lines=0;

    private int n_total_omitted_entries=0;

    private final List<String> errors = new ArrayList<>();

    public List<SmallFile> getSmallFileEntries() {
        return smallFileList;
    }

    public SmallFileIngestor(String directoryPath, Ontology ontology) {
        String omitFile=String.format("%s%s%s",directoryPath, File.separator,"omit-list.txt");
        omitEntries=getOmitEntries(omitFile);
        smallFilePaths =getListOfV2SmallFiles(directoryPath);
        this.ontology=ontology;
        inputSmallfiles();
    }

    /**
     * Read all of the small files representing individual diseases.
     */
    private void inputSmallfiles() {
        logger.trace("We found " + smallFilePaths.size() + " small files.");
        int i=0;
        for (String path : smallFilePaths) {
            if (++i%1000==0) {
                logger.trace(String.format("Inputting %d-th file at %s",i,path));
            }
            SmallfileParser parser=new SmallfileParser(new File(path),ontology);
            try {
                Optional<SmallFile> v2sfOpt = parser.parseSmallFile();
                if (v2sfOpt.isPresent()) {
                    SmallFile v2sf = v2sfOpt.get();
                    n_total_annotation_lines += v2sf.getNumberOfAnnotations();
                    smallFileList.add(v2sf);
                } else {
                    logger.error("Could not parse V2 small file for {}", path);
                }
            } catch (PhenoteFxException e) {
                e.printStackTrace();
                System.err.printf("Error parsing %s: %s\n", path, e.getMessage());
            }
        }
        logger.error("Finished with input of {} files with {} annotations",i,n_total_annotation_lines);
        logger.error("A total of {} entries found in the small file directory were omitted.",n_total_omitted_entries);
    }

    /**
     * This is the format of the omit-list.txt file.
     * Thus, we need to extract only the first field.
     * <pre></pre>
     * #List of OMIM entries that we want to omit from further analysis
     * #DiseaseId    Reason
     * OMIM:107850   trait
     * OMIM:147320   legacy
     * </pre>
     * @param path the path to {@code omit-list.txt}
     * @return List of entries (encoded as strings like "OMIM:600123") that should be omitted
     */
    private Set<String> getOmitEntries(String path) {
        Set<String> entrylist=new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#")) continue; // skip comment
                String[] A = line.split("\\s+");
                String id = A[0]; // the first field has items such as OMIM:500123
                entrylist.add(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
            errors.add(e.getMessage());
        }
        return entrylist;
    }

    /**
     * Get the entry Curie for a certain path
     * @param path e.g., /.../rare-diseases/annotated/OMIM-600123.tab
     * @return the corresinding Curie, e.g., OMIM:600123
     */
    private String baseName(Path path) {
        String bname=path.getFileName().toString();
        bname=bname.replace('-',':').replace(".tab","");
        return bname;
    }


    private List<String> getListOfV2SmallFiles(String v2smallFileDirectory) {
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(v2smallFileDirectory))) {
            for (Path path : directoryStream) {
                if (path.toString().endsWith(".tab")) {
                    String basename=baseName(path);
                    if (omitEntries.contains(basename)) {
                        logger.info("Skipping annotations for entry {} (omit list entry)", basename);
                        n_total_omitted_entries++;
                        continue; // skip this one!
                    }
                    fileNames.add(path.toString());
                }
            }
        } catch (IOException ex) {
            errors.add(String.format("Could not get list of small smallFilePaths from %s [%s]. Terminating...",
                    v2smallFileDirectory,ex));
        }
        return fileNames;
    }

}
