package org.monarchinitiative.phenotefx.worker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.exception.PhenoteFxException;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.monarchinitiative.phenotefx.io.HPOParser;
import org.monarchinitiative.phenotefx.smallfile.V2SmallFile;
import org.monarchinitiative.phenotefx.smallfile.V2SmallFileEntry;
import org.monarchinitiative.phenotefx.smallfile.V2SmallFileIngestor;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.monarchinitiative.phenotefx.smallfile.V2SmallFileEntry.getHeaderV2;

/**
 * If we update a term's label, its id stays the same. The label in the annotation file will no longer be up to date
 * This function checks whether the term labels of the annotated terms are up to date. It proceeds to update the
 * labels and to output a summary so the user of Phenote can visually check that no nonsense has taken place.
 * @author Peter Robinon
 */
public class TermLabelUpdater {
    private final String smallFilePath;
    private final HpoOntology ontology;
    private final List<V2SmallFile> smallFiles;

    private List<String> messages = new ArrayList<>();

    public TermLabelUpdater(String smallFilePath, HpoOntology ontology) {
            this.ontology=ontology;
            this.smallFilePath=smallFilePath;
            V2SmallFileIngestor ingestor = new V2SmallFileIngestor(smallFilePath,ontology);
            this.smallFiles = ingestor.getV2SmallFileEntries();
    }


    public void replaceOutOfDateLabels() {
        for (V2SmallFile v2 : smallFiles) {
            boolean changed=false;
            List<V2SmallFileEntry> entrylist =new ArrayList<>(v2.getOriginalEntryList());
            for (int i=0;i< entrylist.size();i++) {
                V2SmallFileEntry entry =entrylist.get(i);
                TermId tid = entry.getPhenotypeId();
                String label = entry.getPhenotypeName();
                TermId primaryId = ontology.getPrimaryTermId(tid);
                if (!tid.equals(primaryId)) {
                    String msg = String.format("%s: replacing outdated TermId [%s] with correct primary id [%s]",v2.getBasename(),tid.getIdWithPrefix(),primaryId.getIdWithPrefix() );
                    messages.add(msg);
                    V2SmallFileEntry replacement = entry.withUpdatedPrimaryId(primaryId);
                    entrylist.set(i, replacement);
                    changed=true;
                }
                String currentLabel = ontology.getTermMap().get(primaryId).getName();
                if (! label.equals(currentLabel)) {
                    String msg = String.format("%s: replacing outdated label [%s] with current label [%s]",v2.getBasename(),label,currentLabel );
                    messages.add(msg);
                    V2SmallFileEntry replacement = entry.withUpdatedLabel(currentLabel);
                    entrylist.set(i, replacement);
                    changed=true;
                }
            }
            if (changed) {
                writeUpdatedSmallFile(v2.getBasename(),entrylist);
            }
        }

        javafx.application.Platform.runLater(() -> {
            showList(messages);
        });
    }



    private void showList(List<String> messages) {
        final ObservableList<String> data =   FXCollections.observableArrayList(messages);
        final ListView listView = new ListView(data);

        Stage stage = new Stage();
        VBox box = new VBox(listView);
        Scene scene = new Scene(box, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Updating outdated TermId's and labels");
        stage.setScene(scene);
        stage.show();
    }





    private void writeUpdatedSmallFile(String v2basename,List<V2SmallFileEntry> updatedEntries) {
        String path = String.format("%s%sTEST%s",this.smallFilePath, File.separator,v2basename );
        File f = new File(path);
        if (f.exists()) {
            System.err.println("EXISTS Basenae ="+path);
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            String header = getHeaderV2();
            writer.write(header + "\n");
            for (V2SmallFileEntry entry : updatedEntries) {
                String row = entry.getRow();
                writer.write(row + "\n");
            }
            writer.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
