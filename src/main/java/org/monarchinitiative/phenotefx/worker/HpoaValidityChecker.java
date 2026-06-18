package org.monarchinitiative.phenotefx.worker;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenotefx.exception.PhenoteFxException;
import org.monarchinitiative.phenotefx.io.SmallfileParser;
import org.monarchinitiative.phenotefx.smallfile.SmallFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class HpoaValidityChecker extends  PhenoteFxWorker{
    private static final Logger LOGGER = LoggerFactory.getLogger(HpoaValidityChecker.class);

    private int goodParse = 0;

    private final Map<String,String> badParseMap;


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

    public HpoaValidityChecker(String smallFilePath, Ontology ontology) {
        List<String> allHpoaFiles = getListOfHpoaFiles(smallFilePath);
        badParseMap = new HashMap<>();
        LOGGER.info("Got {} HPOA files", allHpoaFiles.size());
        int i=0;
        for (String path : allHpoaFiles) {
            if (++i%1000==0) {
                LOGGER.trace(String.format("Inputting %d-th file at %s",i,path));
            }
            String base = baseName(Path.of(path));
            SmallfileParser parser=new SmallfileParser(new File(path),ontology);
            try {
                Optional<SmallFile> hpoaFileOpt = parser.parseSmallFile();
                if (hpoaFileOpt.isPresent()) {
                    SmallFile hpoaFile = hpoaFileOpt.get();
                    goodParse++;
                } else {
                    String err = String.format("Could not parse V2 small file for %s", path);
                    badParseMap.put(base, err);
                }
            } catch (PhenoteFxException e) {
                badParseMap.put(base,  e.getMessage());
                LOGGER.error("Error parsing {}: {}.\n", path, e.getMessage());
            }
        }
    }

    public void printErros() {
        List<String> messages = new ArrayList<>();
        for (var e:badParseMap.entrySet()) {
            messages.add(String.format("%s: %s", e.getKey(), e.getValue()));
        }
        showList(messages, "Checking validity of HPOA files");
    }



    private List<String> getListOfHpoaFiles(String v2smallFileDirectory) {
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(v2smallFileDirectory))) {
            for (Path path : directoryStream) {
                if (path.toString().endsWith(".tab")) {

                    fileNames.add(path.toString());
                }
            }
        } catch (IOException ex) {
            LOGGER.error(String.format("Could not get list of small smallFilePaths from %s [%s]. Terminating...",
                    v2smallFileDirectory,ex));
        }
        return fileNames;
    }
}
