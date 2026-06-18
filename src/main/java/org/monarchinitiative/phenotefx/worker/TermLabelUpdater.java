package org.monarchinitiative.phenotefx.worker;

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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.gui.PopUps;
import org.monarchinitiative.phenotefx.smallfile.SmallFile;
import org.monarchinitiative.phenotefx.smallfile.SmallFileEntry;
import org.monarchinitiative.phenotefx.smallfile.SmallFileIngestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.monarchinitiative.phenotefx.smallfile.SmallFileEntry.getHeader;

/**
 * If we update a term's label, its id stays the same. The label in the annotation file will no longer be up to date
 * This function checks whether the term labels of the annotated terms are up to date. It proceeds to update the
 * labels and to output a summary so the user of Phenote can visually check that no nonsense has taken place.
 * @author Peter Robinon
 */
public class TermLabelUpdater {
   private static final Logger LOGGER = LoggerFactory.getLogger(TermLabelUpdater.class);

    private final String smallFilePath;
    private final Ontology ontology;
    private final List<SmallFile> smallFiles;

    private final List<String> messages = new ArrayList<>();

    public TermLabelUpdater(String smallFilePath, Ontology ontology) {
            this.ontology=ontology;
            this.smallFilePath=smallFilePath;
            SmallFileIngestor ingestor = new SmallFileIngestor(smallFilePath,ontology);
            this.smallFiles = ingestor.getHpoaFileEntries();
    }

    /**
     * The sex string has been entered in lower case letters, but should be all upper case
     * @param sstring
     * @return
     */
    private Optional<String> sexTermNeedsUpdate(String sstring) {
        if (sstring.isEmpty()) return Optional.empty();  // OK
        else if (sstring.equals("male")) {
            return Optional.of("MALE");
        } else if (sstring.equals("female")) {
            return Optional.of("FEMALE");
        } else if (sstring.equals("MALE") || sstring.equals("FEMALE")) {
            return Optional.empty(); // OK
        }
        else {
            throw new PhenolRuntimeException("Malformed Sex string: " + sstring);
        }
    }

    /**
     * The biocuration string has been entered without a date, e.g., Malformed biocuration entry: "ORCID:0000-0002-0736-9199"
     * If so, add today's date to it
     */
    private Optional<String> biocurationTermNeedsUpdate(String biocuration) {
       if (biocuration.equals("ORCID:0000-0002-0736-9199")) {
           Date date = new Date();
           String todaysDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
           return Optional.of(String.format("%s[%s]", biocuration, todaysDate));
       } else {
           return Optional.empty();
        }
    }



    public void replaceOutOfDateLabels() {
        javafx.application.Platform.runLater(() -> {
            Set<String> updatedDiseases=new HashSet<>();
        for (SmallFile v2 : smallFiles) {
            boolean changed=false;
            List<SmallFileEntry> entrylist =new ArrayList<>(v2.getOriginalEntryList());
            for (int i=0;i< entrylist.size();i++) {
                SmallFileEntry entry =entrylist.get(i);
                TermId tid = entry.getPhenotypeId();
                String label = entry.getPhenotypeName();
                TermId primaryId = ontology.getPrimaryTermId(tid);
                Optional<String> labelOpt = ontology.getTermLabel(primaryId);
                Optional<String> sexStringOpt = sexTermNeedsUpdate(entry.getSex());
                Optional<String> biocurateOpt = biocurationTermNeedsUpdate(entry.getBiocuration());
                if (labelOpt.isEmpty()) {
                    LOGGER.error("Could not find label for {}", primaryId.getValue());
                } else {
                    String currentLabel = labelOpt.get();
                    if (!tid.equals(primaryId) || !label.equals(currentLabel)) {
                        updatedDiseases.add(v2.getBasename());
                        String msg = String.format("Replacing outdated TermId [%s] with correct primary id [%s]", tid.getValue(), primaryId.getValue());
                        messages.add(msg);
                        SmallFileEntry replacement = entry.withUpdatedPrimaryIdAndLabel(primaryId, currentLabel);
                        entrylist.set(i, replacement);
                        changed = true;
                    } else if (sexStringOpt.isPresent()) {
                        String replacementSexString = sexStringOpt.get();
                        SmallFileEntry replacement = entry.withUpdatedSexString(replacementSexString);
                        entrylist.set(i, replacement);
                        String msg = String.format("Replacing malformed Sex string [%s] with correct one [%s].",
                                entry.getSex(), replacement.getSex());
                        messages.add(msg);
                        changed = true;
                    } else if (biocurateOpt.isPresent()) {
                        String replacementBiocurateString = biocurateOpt.get();
                        SmallFileEntry replacement = entry.withUpdatedBiocurationString(replacementBiocurateString);
                        entrylist.set(i, replacement);
                        String msg = String.format("Replacing malformed biocuration [%s] with correct one [%s].",
                                entry.getBiocuration(), replacement.getBiocuration());
                        messages.add(msg);
                        changed = true;

                    }
                }
            }
            if (changed) {
                writeUpdatedSmallFile(v2.getBasename(),entrylist);
            }
        }
            showList(messages,updatedDiseases);
        }); // end of run-later ToDo--wrap this in a Task!
    }



    private void showList(List<String> messages, Set<String> diseases) {
        String diseasestring=String.format("Updates performed on %d disease files",diseases.size());
        String diseaselist = String.join(", ", diseases);
        Map<String, Long> counted = messages.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<String> uniqued=new ArrayList<>();
        for (Map.Entry<String,Long> entry: counted.entrySet()) {
            String s = String.format("n=%d: %s",entry.getValue(),entry.getKey());
            uniqued.add(s);
        }



        final ObservableList<String> data =   FXCollections.observableArrayList();
        data.add(diseasestring);
        data.add(diseaselist);
        data.addAll(uniqued);
        final ListView<String> listView = new ListView<>(data);

        Stage stage = new Stage();
        VBox box = new VBox(listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
        Button ok=new Button("Close");

        ok.setOnAction(e-> stage.close() );
        box.getChildren().add(ok);
        Scene scene = new Scene(box, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Updating outdated TermId's and labels");
        stage.setScene(scene);
        stage.show();
    }





    private void writeUpdatedSmallFile(String v2basename,List<SmallFileEntry> updatedEntries) {
        String path = String.format("%s%s%s",this.smallFilePath, File.separator,v2basename );
        File f = new File(path);
        if (! f.exists()) {
            System.err.printf("Could not find file %s%n",f.getAbsolutePath());
            return;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            String header = getHeader();
            writer.write(header + "\n");
            for (SmallFileEntry entry : updatedEntries) {
                String row = entry.getRow();
                writer.write(row + "\n");
            }
            writer.close();
        } catch (IOException e){
            LOGGER.error(e.getMessage());
            PopUps.alertDialog("Error", e.getMessage());
        }
    }

}
