package org.monarch.hphenote.phenote;

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
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.monarch.hphenote.model.PhenoRow;
import org.monarch.hphenote.ptable.PTableView;

import javafx.scene.control.TableView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by robinp on 5/22/17.
 */
public class PhenotePresenter implements Initializable {

    @FXML
    AnchorPane anchorpane;

    /** This is the main border pane of the application. We will inject the table into it in the initialize method */
    @FXML BorderPane bpane;



    @FXML
    MenuItem openFileMenuItem;

    @FXML MenuItem exitMenuItem;



    /** This is the table where the phenotype data will be shown. */
    TableView<PhenoRow> table=null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //PTableView view = new PTableView((f) -> null);
       // view.getViewAsync(bpane.getCenter());
        //view.getViewAsync(bpane.getCenter().getParent()::addEventFilter);
        anchorpane.setPrefSize(1400,1000);
        setUpTable();
        table.setItems(getRows());
        VBox vbox = new VBox();
        vbox.getChildren().addAll(table);
        vbox.setVgrow(table, Priority.ALWAYS);

        // set up buttons
        // TODO extend this to ask about saving unsaved work.
        exitMenuItem.setOnAction( e -> Platform.exit());
        openFileMenuItem.setOnAction(e -> openPhenoteFile(e));


        bpane.setCenter(vbox);
        //fetched from followme.properties
        //this.theVeryEnd = rb.getString("theEnd");
    }

    /** Open a phenote file ("small file") and populate the table with it.
     * TODO check if there is unsaved work before opening the file */
    private void openPhenoteFile(ActionEvent event) {
        //Stage stage = Stage.class.cast(PhenotePresenter.class.cast(event.getSource()).get);
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File f = fileChooser.showOpenDialog(stage);
        if (f!=null) {
            populateTable(f);
        }
    }

    private void populateTable(File f) {
        setUpTable();
        ObservableList<PhenoRow> phenolist = FXCollections.observableArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line=null;
            while ((line=br.readLine())!=null){
                //System.err.println(line);
                try {
                    PhenoRow row = PhenoRow.constructFromLine(line);
                    phenolist.add(row);
                } catch (Exception e) {
                    System.err.println(e.getMessage()); // skip this line
                }
            }
            System.err.println("Size opf phenolist:"+phenolist.size());
            table.setItems(phenolist);
            System.err.println("Size opf items : "+ table.getItems().size());
            table.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void launch() {
        //message.setText("Date: " + date + " -> " + prefix + tower.readyToTakeoff() + happyEnding + theVeryEnd
        //);
    }


    public ObservableList<PhenoRow> getRows(){
        ObservableList<PhenoRow> olist = FXCollections.observableArrayList();
        olist.add(new PhenoRow());
        return olist;
    }



    /* Sets up the table but does not add anything to the rows. */
    private void setUpTable() {
        this.table=new TableView<>();
        TableColumn<PhenoRow,String> diseaseIDcol = new TableColumn<>("Disease ID");
        diseaseIDcol.setMinWidth(100);
        diseaseIDcol.setCellValueFactory(new PropertyValueFactory<>("diseaseID"));
        TableColumn<PhenoRow,String> diseaseNamecol = new TableColumn<>("Disease Name");
        TableColumn<PhenoRow,String> geneIDcol = new TableColumn<>("Gene ID");
        TableColumn<PhenoRow,String> geneNamecol = new TableColumn<>("Gene Name");
        TableColumn<PhenoRow,String> genotypeCol = new TableColumn<>("Genotype");
        TableColumn<PhenoRow,String> geneSymbolCol = new TableColumn<>("Gene symbol");
        TableColumn<PhenoRow,String> phenotypeIDcol = new TableColumn<PhenoRow,String>("HPO ID");
        phenotypeIDcol.setMinWidth(100);
        phenotypeIDcol.setCellValueFactory(new PropertyValueFactory<>("phenotypeID"));
        TableColumn<PhenoRow,String> phenotypeNameCol = new TableColumn<>("HPO Name");
        TableColumn<PhenoRow,String> ageOfOnsetIDcol = new TableColumn<>("Age of Onset ID");
        TableColumn<PhenoRow,String> ageOfOnsetNamecol = new TableColumn<>("Age of Onset Name");
        TableColumn<PhenoRow,String> evidenceIDcol = new TableColumn<>("evidence ID");
        evidenceIDcol.setMinWidth(50);
        evidenceIDcol.setCellValueFactory(new PropertyValueFactory<>("evidenceID"));
        // do not show evidenceName, it is redundant!
        TableColumn<PhenoRow,String> frequencyCol = new TableColumn<>("Frequency");
        frequencyCol.setMinWidth(100);
        frequencyCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        TableColumn<PhenoRow,String> sexCol = new TableColumn<>("Sex");
        frequencyCol.setMinWidth(15);
        frequencyCol.setCellValueFactory(new PropertyValueFactory<>("sexID"));
        // do not show sexName, it is redundant!
        TableColumn<PhenoRow,String> negationCol = new TableColumn<>("Not?");
        negationCol.setMinWidth(15);
        negationCol.setCellValueFactory(new PropertyValueFactory<>("negationID"));
        // do not show negationName, it is redundant!
        TableColumn<PhenoRow,String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setMinWidth(50);
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<PhenoRow,String> pubCol = new TableColumn<>("Pub");
        pubCol.setMinWidth(50);
        pubCol.setCellValueFactory(new PropertyValueFactory<>("pub"));
        TableColumn<PhenoRow,String> assignedByCol = new TableColumn<>("Assigned By");
        assignedByCol.setMinWidth(50);
        assignedByCol.setCellValueFactory(new PropertyValueFactory<>("assignedBy"));

        TableColumn<PhenoRow,String> dateCreatedCol = new TableColumn<>("Date Created");
        dateCreatedCol.setMinWidth(50);
        dateCreatedCol.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));


        table.getColumns().addAll(diseaseIDcol,diseaseNamecol,phenotypeIDcol,phenotypeNameCol,ageOfOnsetIDcol,ageOfOnsetNamecol,evidenceIDcol,frequencyCol,negationCol,
                descriptionCol,pubCol,assignedByCol,dateCreatedCol);
        table.setMinSize(1400,1000);
        table.setPrefSize(2000,800);
        table.setMaxSize(2400,1800);
        //
        anchorpane.setTopAnchor(table,10.0);
        anchorpane.setBottomAnchor(table,10.0);
        AnchorPane.setLeftAnchor(anchorpane,10.0);
        AnchorPane.setRightAnchor(anchorpane,10.0);

    }



}
