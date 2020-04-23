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
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.smallfile.SmallFile;
import org.monarchinitiative.phenotefx.smallfile.SmallFileEntry;
import org.monarchinitiative.phenotefx.smallfile.SmallFileIngestor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.monarchinitiative.phenotefx.smallfile.SmallFileEntry.getHeaderV2;

/**
 * If we update a term's label, its id stays the same. The label in the annotation file will no longer be up to date
 * This function checks whether the term labels of the annotated terms are up to date. It proceeds to update the
 * labels and to output a summary so the user of Phenote can visually check that no nonsense has taken place.
 * @author Peter Robinon
 */
public class TermLabelUpdater {
    private final String smallFilePath;
    private final Ontology ontology;
    private final List<SmallFile> smallFiles;

    private List<String> messages = new ArrayList<>();

    public TermLabelUpdater(String smallFilePath, Ontology ontology) {
            this.ontology=ontology;
            this.smallFilePath=smallFilePath;
            SmallFileIngestor ingestor = new SmallFileIngestor(smallFilePath,ontology);
            this.smallFiles = ingestor.getSmallFileEntries();
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
                if (!tid.equals(primaryId)) {
                    updatedDiseases.add(v2.getBasename());
                    String msg = String.format("Replacing outdated TermId [%s] with correct primary id [%s]",tid.getValue(),primaryId.getValue() );
                    messages.add(msg);
                    SmallFileEntry replacement = entry.withUpdatedPrimaryId(primaryId);
                    entrylist.set(i, replacement);
                    changed=true;
                }
                String currentLabel = ontology.getTermMap().get(primaryId).getName();
                if (! label.equals(currentLabel)) {
                    updatedDiseases.add(v2.getBasename());
                    String msg = String.format("Replacing outdated label [%s] with current label [%s]",label,currentLabel );
                    messages.add(msg);
                    SmallFileEntry replacement = entry.withUpdatedLabel(currentLabel);
                    entrylist.set(i, replacement);
                    changed=true;
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
        String diseaselist = diseases.stream().collect(Collectors.joining(", "));
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
            System.err.println(String.format("Could not find file %s",f.getAbsolutePath()));
            return;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            String header = getHeaderV2();
            writer.write(header + "\n");
            for (SmallFileEntry entry : updatedEntries) {
                String row = entry.getRow();
                writer.write(row + "\n");
            }
            writer.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
