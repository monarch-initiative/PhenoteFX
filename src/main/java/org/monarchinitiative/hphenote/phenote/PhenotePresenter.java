package org.monarchinitiative.hphenote.phenote;

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

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.controlsfx.control.textfield.TextFields;

import org.monarchinitiative.hphenote.biolark.BiolarkAnalysis;
import org.monarchinitiative.hphenote.biolark.TextMiningAnalyzer;
import org.monarchinitiative.hphenote.gui.ExceptionDialog;
import org.monarchinitiative.hphenote.gui.PopUps;
import org.monarchinitiative.hphenote.gui.ProgressForm;
import org.monarchinitiative.hphenote.gui.WidthAwareTextFields;
import org.monarchinitiative.hphenote.io.*;
import org.monarchinitiative.hphenote.io.*;
import org.monarchinitiative.hphenote.model.Frequency;
import org.monarchinitiative.hphenote.model.HPOOnset;
import org.monarchinitiative.hphenote.model.PhenoRow;
import org.monarchinitiative.hphenote.model.Settings;
import org.monarchinitiative.hphenote.validation.*;
import org.monarchinitiative.hphenote.validation.*;
import org.monarchinitiative.hpotextmining.TextMiningAnalysis;
import org.monarchinitiative.hpotextmining.model.Term;
import org.monarchinitiative.hpotextmining.model.TextMiningResult;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by robinp on 5/22/17.
 * Main presenter for the HPO Phenote App.
 */
public class PhenotePresenter implements Initializable {

    private static final String settingsFileName="hphenote.settings";

    @FXML
    AnchorPane anchorpane;

    /** This is the main border pane of the application. We will inject the table into it in the initialize method */
    @FXML BorderPane bpane;
    /** This is the HBox that will contain the dynamically generated TableView of Phenotypes. */
    @FXML
    HBox tablebox;

    /** For OMIM/Orphanet Disease names */
    @FXML
    TextField diseaseNameTextField;

    @FXML TextField hpoNameTextField;

    @FXML Label diseaseIDlabel;

    /* ------ MENU ---------- */
    @FXML
    MenuItem openFileMenuItem;

    @FXML MenuItem exitMenuItem;

    @FXML MenuItem downloadHPOmenuItem;

    @FXML MenuItem downloadMedgenMenuItem;

    @FXML MenuItem showSettingsMenuItem;

    @FXML Button setAllDiseaseNamesButton;

    @FXML ChoiceBox<String> ageOfOnsetChoiceBox;

    @FXML RadioButton IEAbutton;
    @FXML RadioButton ICEbutton;
    @FXML RadioButton PCSbutton;
    @FXML RadioButton TASbutton;

    @FXML TextField frequencyTextField;

    @FXML ChoiceBox<String> frequencyChoiceBox;

    @FXML TextField descriptiontextField;
    /** The publication (source) for the annotation (refered to as "pub" in the small files).*/
    @FXML TextField pubTextField;

    @FXML CheckBox notBox;

    @FXML Button addAnnotationButton;
    @FXML Button deleteAnnotationButton;
    @FXML Button fetchTextMiningButton;

    @FXML Button correctDateFormatButton;

    @FXML Label lastSourceLabel;
    @FXML CheckBox lastSourceBox;

    private ToggleGroup evidenceGroup;

    private StringProperty diseaseName,diseaseID;

    private Settings settings=null;

    private Map<String,String> omimName2IdMap;

    private Map<String,String> hponame2idMap;

    private Map<String,String> hpoSynonym2LabelMap;

    private HPOOnset hpoOnset;

    private Frequency frequency;
    /** Header of the current Phenote file. */
    private String header=null;
    /** Base name of the current Phenote file */
    private String currentPhenoteFileBaseName =null;

    private String currentPhenoteFileFullPath=null;
    /** The last source used, e.g., a PMID (use this to avoid having to re-enter the source) */
    private StringProperty lastSource=new SimpleStringProperty("");


    /** This is the table where the phenotype data will be shown. */
    TableView<PhenoRow> table=null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSettings();
        checkReadiness();
        setDefaultHeader();
        inputHPOandMedGen();
        setupAutocomplete();

        anchorpane.setPrefSize(1400,1000);
        setUpTable();
        table.setItems(getRows());
        this.tablebox.getChildren().add(table);
        this.tablebox.setHgrow(table, Priority.ALWAYS);


        // set up buttons
        // TODO extend this to ask about saving unsaved work.
        exitMenuItem.setOnAction( e -> exitGui());
        openFileMenuItem.setOnAction(e -> openPhenoteFile(e));

        this.diseaseNameTextField.setPromptText("Will default to disease name in first row if left empty");
        this.hpoNameTextField.setPromptText("Enter preferred label or synonym (will be automatically converted)");

        evidenceGroup = new ToggleGroup();
        IEAbutton.setToggleGroup(evidenceGroup);
        ICEbutton.setToggleGroup(evidenceGroup);
        PCSbutton.setToggleGroup(evidenceGroup);
        TASbutton.setToggleGroup(evidenceGroup);
        IEAbutton.setSelected(true);

        // todo getUserData is returning Null.
       /* evidenceGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle){
                    if (evidenceGroup.getSelectedToggle() != null) {
                        System.out.println("[PhenotePresenter.java] Evidence group="+evidenceGroup.getSelectedToggle().getUserData());
                    }
                }
        });*/
        hpoOnset = HPOOnset.factory();
        ageOfOnsetChoiceBox.setItems(hpoOnset.getOnsetTermList());
        this.frequency = Frequency.factory();
        frequencyChoiceBox.setItems(frequency.getFrequencyTermList());
        // prompt
        this.descriptiontextField.setPromptText("free text description of anything not captured with standards (optional)");
        this.pubTextField.setPromptText("Source of assertion (usually PubMed, OMIM, Orphanet...)");
        this.frequencyTextField.setPromptText("A value such as 7/13 or 54% (leave empty if pulldown used)");

        //Tooltips
        this.diseaseIDlabel.setTooltip(new Tooltip("Name of a disease (OMIM IDs will be automatically populated)"));

        pubTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                String txt = pubTextField.getText();
                txt = txt.replaceAll("\\s","");
                pubTextField.setText(txt);
            }
        });

        this.lastSourceLabel.textProperty().bind(this.lastSource);

    }

    /** When we create a new annotation file,
     * we need to set the Header line here.
     */
    private void setDefaultHeader() {
        this.header="Disease ID\tDisease Name\tGene ID\tGene Name\tGenotype\tGene Symbol(s)\tPhenotype ID\tPhenotype Name\tAge of Onset ID\tAge of Onset Name\tEvidence ID\tEvidence Name\tFrequency\tSex ID\tSex Name\tNegation ID\tNegation Name\tDescription\tPub\tAssigned by\tDate Created";
    }

    /** Called by the initialize method. Serves to set up the
     * Maps with HPO and Disease name information for the autocompletes.
     */
    private void inputHPOandMedGen() {
        MedGenParser medGenParser = new MedGenParser();
        omimName2IdMap = medGenParser.getOmimName2IdMap();
        HPOParser hpoParser = new HPOParser();
        hponame2idMap = hpoParser.getHpoName2IDmap();
        hpoSynonym2LabelMap = hpoParser.getHpoSynonym2PreferredLabelMap();
    }

    /** Checks if the HPO and medgen files have been downloaded already, and if
     * not shows an alert window.
     */
    private void checkReadiness() {
        StringBuffer sb = new StringBuffer();
        boolean ready = true;
        boolean hpoready = org.monarchinitiative.hphenote.gui.Platform.checkHPOFileDownloaded();
        if (! hpoready) {
            sb.append("HPO File not found. ");
            ready = false;
        }
        boolean medgenready = org.monarchinitiative.hphenote.gui.Platform.checkMedgenFileDownloaded();
        if (! medgenready) {
            sb.append("MedGen_HPO_OMIM_Mapping.txt.gz not found. ");
            ready = false;
        }
        if (! ready) {
            sb.append("You need to download the files before working with annotation data.");
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(sb.toString());
                    alert.setContentText("Download the files with the commands in the Settings menu!");
                    alert.show();

                    return null;
                }
            };
            task.run();

        }

    }


    /** Write the settings from the current session to file and exit. */
    private void exitGui() {
        saveSettings();
        Platform.exit();
    }


    private static void showAlert(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error occured");
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
        a.show();//setAlwaysOnTop(true);
    }

    /**
     * Parse XML file from standard location and return as
     * {@link Settings} bean.
     * @return
     */
    private void loadSettings() {
        File defaultSettingsPath = new File(org.monarchinitiative.hphenote.gui.Platform.getHPhenoteDir().getAbsolutePath()
                + File.separator + settingsFileName);
        if (!org.monarchinitiative.hphenote.gui.Platform.getHPhenoteDir().exists()) {
            File fck = new File(org.monarchinitiative.hphenote.gui.Platform.getHPhenoteDir().getAbsolutePath());
            if (!fck.mkdir()) { // make sure config directory is created, exit if not
                showAlert("Unable to create HRMD-gui config directory.\n"
                        + "Even though this is a serious problem I'm exiting gracefully. Bye.");
                System.exit(1);}
        }
        if (!defaultSettingsPath.exists()) {
            this.settings = new Settings(); return; // create blank new Settings
        }
        this.settings = Settings.factory(defaultSettingsPath.getAbsolutePath());
    }

    /**
     * This method gets called when user chooses to close Gui. Content of
     * {@link Settings} bean is dumped
     * in XML format to platform-dependent default location.
     */
    private void saveSettings() {
        File hrmdDirectory = org.monarchinitiative.hphenote.gui.Platform.getHPhenoteDir();
        File parentDir = hrmdDirectory.getParentFile();
        if (!parentDir.exists()) {
            if (!parentDir.mkdir()) {
                showAlert("Error saving settings. Settings not saved.");
                return;
            }
        }
        if (!hrmdDirectory.exists()) {
            try {
                hrmdDirectory.createNewFile();
            } catch (IOException e) {
                showAlert("Error saving settings. Settings not saved.");
                return;
            }
        }
        File settingsFile = new File(hrmdDirectory.getAbsolutePath()
                + File.separator + settingsFileName);
        if (!Settings.saveToFile(settings, settingsFile)) {
            return;
        }
    }

    private void setupAutocomplete() {
        if (omimName2IdMap != null) {
            WidthAwareTextFields.bindWidthAwareAutoCompletion(diseaseNameTextField, omimName2IdMap.keySet());
        }
        diseaseNameTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                diseaseID.setValue("");
            }
        }));


        this.diseaseID = new SimpleStringProperty(this, "diseaseID", "");
        this.diseaseName = new SimpleStringProperty(this,"diseaseName","");
        diseaseIDlabel.textProperty().bindBidirectional(diseaseID);
        diseaseNameTextField.textProperty().bindBidirectional(diseaseName);
        diseaseNameTextField.setOnAction( e-> {
            String name = diseaseName.getValue();
            diseaseID.setValue(omimName2IdMap.get(name));
        });
        if (hpoSynonym2LabelMap != null) {
            //TextFields.bindAutoCompletion
            WidthAwareTextFields.bindWidthAwareAutoCompletion(hpoNameTextField, hpoSynonym2LabelMap.keySet());
        }
    }

    /** Open a phenote file ("small file") and populate the table with it.
     * TODO check if there is unsaved work before opening a new file */
    private void openPhenoteFile(ActionEvent event) {
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File f = fileChooser.showOpenDialog(stage);
        if (f!=null) {
            populateTable(f);
        }
    }

    private void populateTable(File f) {
        List<String> errors = new ArrayList<>();
        setUpTable();
        ObservableList<PhenoRow> phenolist = FXCollections.observableArrayList();
        this.currentPhenoteFileBaseName = f.getName();
        this.currentPhenoteFileFullPath = f.getAbsolutePath();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line=null;
            // Note first line is header
            line = br.readLine();
            if (! line.startsWith("Disease ID")) {
                System.err.println("Error malformed first line: "+line);
                PopUps.showInfoMessage("Error: malformed header line (did not start with \"Disease ID\")",
                        String.format("Exit and Regenerate this file: (line:%s)",line));
            } else {
                header=line;
            }
            while ((line=br.readLine())!=null){
                //System.err.println(line);
                try {
                    PhenoRow row = PhenoRow.constructFromLine(line);
                    phenolist.add(row);
                } catch (Exception e) {
                    errors.add(e.getMessage()); // skip this line
                }
            }
            table.setItems(phenolist);
            this.tablebox.getChildren().clear();
            this.tablebox.getChildren().add(table);

        } catch (IOException e) {
            e.printStackTrace();
            this.currentPhenoteFileBaseName =null; // couldnt open this file!
        }
        if (errors.size()>0) {
            StringBuilder sb = new StringBuilder();
            for (String e : errors) { sb.append(e+"\n"); }
            ExceptionDialog.display(sb.toString());
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
        table.setEditable(true);
        TableColumn<PhenoRow,String> diseaseIDcol = new TableColumn<>("Disease ID");
        diseaseIDcol.setMinWidth(70);
        diseaseIDcol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("diseaseID"));
        diseaseIDcol.setCellFactory(TextFieldTableCell.forTableColumn());
        diseaseIDcol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDiseaseID(event.getNewValue());
                    }
                }
        );
        TableColumn<PhenoRow,String> diseaseNamecol = new TableColumn<>("Disease Name");
        diseaseNamecol.setMinWidth(150);
        diseaseNamecol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("diseaseName"));
        diseaseNamecol.setCellFactory(TextFieldTableCell.forTableColumn());
        diseaseNamecol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDiseaseName(event.getNewValue());
                    }
                }
        );
        TableColumn<PhenoRow,String> geneIDcol = new TableColumn<>("Gene ID");
        TableColumn<PhenoRow,String> geneNamecol = new TableColumn<>("Gene Name");
        TableColumn<PhenoRow,String> genotypeCol = new TableColumn<>("Genotype");
        TableColumn<PhenoRow,String> geneSymbolCol = new TableColumn<>("Gene symbol");
        TableColumn<PhenoRow,String> phenotypeIDcol = new TableColumn<PhenoRow,String>("HPO ID");
        phenotypeIDcol.setMinWidth(100);
        phenotypeIDcol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("phenotypeID"));
        phenotypeIDcol.setCellFactory(TextFieldTableCell.forTableColumn());
        /*phenotypeIDcol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        String hpoid = event.getNewValue();
                        if (HPOValidator.isValid(hpoid)) {
                            ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPhenotypeID(event.getNewValue());
                        }
                        event.getTableView().refresh();
                    }
                }
        );*/
        phenotypeIDcol.setOnEditCommit( event -> {
            String hpoid=event.getNewValue();
            if (HPOValidator.isValid(hpoid)) {
                ((PhenoRow)event.getTableView().getItems().get(event.getTablePosition().getRow())).setPhenotypeID(event.getNewValue());
            }
            event.getTableView().refresh();
        });
        TableColumn<PhenoRow,String> phenotypeNameCol = new TableColumn<>("HPO Name");
        phenotypeNameCol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("phenotypeName"));
        phenotypeNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        phenotypeNameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPhenotypeName(event.getNewValue());
                    }
                }
        );
        TableColumn<PhenoRow,String> ageOfOnsetIDcol = new TableColumn<>("Age of Onset ID");
        ageOfOnsetIDcol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("ageOfOnsetID"));
        ageOfOnsetIDcol.setCellFactory(TextFieldTableCell.forTableColumn());
        ageOfOnsetIDcol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setAgeOfOnsetID(event.getNewValue());
                    }
                }
        );
        TableColumn<PhenoRow,String> ageOfOnsetNamecol = new TableColumn<>("Age of Onset Name");
        ageOfOnsetNamecol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("ageOfOnsetName"));
        ageOfOnsetNamecol.setCellFactory(TextFieldTableCell.forTableColumn());
        ageOfOnsetNamecol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setAgeOfOnsetName(event.getNewValue());
                    }
                }
        );
        TableColumn<PhenoRow,String> evidenceIDcol = new TableColumn<>("evidence ID");
        evidenceIDcol.setMinWidth(50);
        evidenceIDcol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("evidenceID"));
        evidenceIDcol.setCellFactory(TextFieldTableCell.forTableColumn());
        evidenceIDcol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        String newEvidence = event.getNewValue();
                        if (EvidenceValidator.isValid(newEvidence)) {
                            ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setEvidenceID(event.getNewValue());
                        }
                        event.getTableView().refresh();
                    }
                }
        );
        // do not show evidenceName, it is redundant!
        TableColumn<PhenoRow,String> frequencyCol = new TableColumn<>("Frequency");
        frequencyCol.setMinWidth(100);
        frequencyCol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("frequency"));
        frequencyCol.setCellFactory(TextFieldTableCell.forTableColumn());
        frequencyCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        String frequency = event.getNewValue();
                        if (FrequencyValidator.isValid(frequency)) {
                            ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setFrequency(event.getNewValue());
                        }
                        event.getTableView().refresh();
                    }
                }
        );
        TableColumn<PhenoRow,String> sexIDcol = new TableColumn<>("Sex");
        sexIDcol.setMinWidth(15);
        sexIDcol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("sexID"));
        sexIDcol.setCellFactory(TextFieldTableCell.forTableColumn());
        sexIDcol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setSexID(event.getNewValue());
                    }
                }
        );

        // do not show sexName, it is redundant!
        TableColumn<PhenoRow,String> negationCol = new TableColumn<>("Not?");
        negationCol.setMinWidth(15);
        negationCol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("negationID"));
        negationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        negationCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        if (NotValidator.isValid(event.getNewValue())) {
                            ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setNegationID(event.getNewValue());
                        }
                        event.getTableView().refresh();
                    }
                }
        );
        // do not show negationName, it is redundant!
        TableColumn<PhenoRow,String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setMinWidth(50);
        descriptionCol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("description"));
        descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDescription(event.getNewValue());
                    }
                }
        );
        TableColumn<PhenoRow,String> pubCol = new TableColumn<>("Pub");
        pubCol.setMinWidth(50);
        pubCol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("pub"));
        pubCol.setCellFactory(TextFieldTableCell.forTableColumn());
        pubCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPub(event.getNewValue());
                    }
                }
        );
        TableColumn<PhenoRow,String> assignedByCol = new TableColumn<>("Assigned By");
        assignedByCol.setMinWidth(50);
        assignedByCol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("assignedBy"));
        assignedByCol.setCellFactory(TextFieldTableCell.forTableColumn());
        assignedByCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setAssignedBy(event.getNewValue());
                    }
                }
        );
        TableColumn<PhenoRow,String> dateCreatedCol = new TableColumn<>("Date Created");
        dateCreatedCol.setMinWidth(50);
        dateCreatedCol.setCellValueFactory(new PropertyValueFactory<PhenoRow,String>("dateCreated"));
        dateCreatedCol.setCellFactory(TextFieldTableCell.forTableColumn());
        dateCreatedCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PhenoRow, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PhenoRow, String> event) {
                        ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDateCreated(event.getNewValue());
                    }
                }
        );

        table.getColumns().addAll(diseaseIDcol,diseaseNamecol,phenotypeIDcol,phenotypeNameCol,ageOfOnsetIDcol,ageOfOnsetNamecol,evidenceIDcol,frequencyCol,sexIDcol,negationCol,
                descriptionCol,pubCol,assignedByCol,dateCreatedCol);

        table.setMinSize(1800,400);
        table.setPrefSize(2000,400);
        table.setMaxSize(2400,500);
        table.setEditable(true);
        //
        anchorpane.setTopAnchor(table,10.0);
        anchorpane.setBottomAnchor(table,10.0);
        AnchorPane.setLeftAnchor(anchorpane,10.0);
        AnchorPane.setRightAnchor(anchorpane,10.0);

    }

    /** Get path to the .hphenote directory, download the file, and if successful
     * set the path to the file in the settings.
     */
    public void  downloadHPO() {

        Downloader downloader = new HPODownloader();
        ProgressBar pb = new ProgressBar();
        pb.setProgress(0);
        pb.progressProperty().unbind();
        Task<Void> task = downloader.download();
        pb.progressProperty().bind(task.progressProperty());
        ProgressForm pForm = new ProgressForm();
        pForm.activateProgressBar(task);

        task.run();
        this.settings.setHpoFile(downloader.getLocalFilePath());
        saveSettings();
    }
    /** Get path to the .hphenote directory, download the medgen file, and if successful
     * set the path to the file in the settings.
     */
    public void downloadMedGen() {
        Downloader downloader = new MedGenDownloader();
        ProgressBar pb = new ProgressBar();
        pb.setProgress(0);
        pb.progressProperty().unbind();
        Task<Void> task = downloader.download();
        pb.progressProperty().bind(task.progressProperty());
        ProgressForm pForm = new ProgressForm();
        pForm.activateProgressBar(task);

        task.run();
        this.settings.setMedgenFile(downloader.getLocalFilePath());
        saveSettings();
    }

    /** This function intends to set all of the disease names to the name in the text field.
     * We can use this to correct the disease names for legacy files where we are using multiple different
     * disease names. Or in cases that the canonical name was updated. If the textfield is empty, the function
     * quietly does nothing. It assumes that the diseaseID is correct and does not try to change that.
     */
    public void setAllDiseasesNames() {
        List<PhenoRow> phenorows = table.getItems();
        String diseaseName = diseaseNameTextField.getText();
        if (diseaseName==null) {
            return;
        }
        for (PhenoRow pr :phenorows) {
            pr.setDiseaseName(diseaseName);
        }
        table.refresh();
    }

    /** SOme of our older files are missing the date created. This function
     * will look at all date entries and set them to today's date if the cell is empty.
     */
    public void setCreatedDateToTodayInAllEmptyRows() {
        List<PhenoRow> phenorows = table.getItems();
        String today = getDate();
        for (PhenoRow pr :phenorows) {
            String olddate = pr.getDateCreated();
            if (olddate==null || olddate.length()<2)
            pr.setDateCreated(today);
        }
        table.refresh();
    }


    private void addTextMinedAnnotation(String hpoLabel, String pmid, boolean isNegated) {
        PhenoRow row = new PhenoRow();
        String hpoId = this.hponame2idMap.get(hpoLabel);
        row.setPhenotypeName(hpoLabel);
        row.setPhenotypeID(hpoId);
        if (! pmid.startsWith("PMID"))
            pmid=String.format("PMID:%s",pmid);
        row.setPub(pmid);
        if (isNegated) {
            row.setNegationID("NOT");
            row.setNegationName("NOT");
        }
        /** If there is data in the table already, use it to fill in the disease ID and Name. */
        List<PhenoRow> phenorows = table.getItems();
        if (phenorows!=null && phenorows.size()>0) {
            PhenoRow firstrow=phenorows.get(0);
            row.setDiseaseName(firstrow.getDiseaseName());
            row.setDiseaseID(firstrow.getDiseaseID());
        }
        /** These annotations will always be PMIDs, so we use the code PCS */
        row.setEvidenceID("PCS");
        row.setEvidenceName("PCS");
        row.setAssignedBy(settings.getBioCuratorId());
        String date = getDate();
        row.setDateCreated(date);
        table.getItems().add(row);
    }


    public void addAnnotation() {
        PhenoRow row = new PhenoRow();
        // Disease ID (OMIM)
        String diseaseID=null;
        String diseaseName = this.diseaseNameTextField.getText().trim();
        // default to the disease name in the first row of the table's current entry
        if (diseaseName == null || diseaseName.length()<3) {
            if (table.getItems().size() > 0) {
                diseaseName = table.getItems().get(0).getDiseaseName();
                diseaseID = table.getItems().get(0).getDiseaseID();
            }
        } else {
            diseaseID = this.omimName2IdMap.get(diseaseName);
            if (diseaseID == null) {
                diseaseID = "?";
            } else {/* the map mcontains items such as 612342, but we want OMIM:612342 */
                diseaseID=String.format("OMIM:%s",diseaseID);
            }
        }

        row.setDiseaseID(diseaseID);
        row.setDiseaseName(diseaseName);
        // HPO Id
        String hpoId;
        String hpoSynonym = this.hpoNameTextField.getText().trim();
        String hpoPreferredLabel = this.hpoSynonym2LabelMap.get(hpoSynonym);
        hpoId = this.hponame2idMap.get(hpoPreferredLabel);
        row.setPhenotypeID(hpoId);
        row.setPhenotypeName(hpoPreferredLabel);
        String evidence = "?";
        if (IEAbutton.isSelected())
            evidence="IEA";
        else if (ICEbutton.isSelected())
            evidence="ICE";
        else if (PCSbutton.isSelected())
            evidence="PCS";
        else if (TASbutton.isSelected())
            evidence="TAS";
        row.setEvidenceID(evidence);
        row.setEvidenceName(evidence); // redundant in our format.
        // Age of onset
        String onsetID,onsetName;
        onsetName = ageOfOnsetChoiceBox.getValue();
        if (onsetName != null) {
            onsetID = hpoOnset.getID(onsetName);
            row.setAgeOfOnsetID(onsetID);
            row.setAgeOfOnsetName(onsetName);
        }
        String frequencyName=null;
        String freq=this.frequencyChoiceBox.getValue();
        if (freq !=null) {
            frequencyName=freq;
        } else {
            frequencyName = this.frequencyTextField.getText().trim();
        }
        if (frequencyName != null && frequencyName.length()>2) {
            row.setFrequency(frequencyName);
        }
        String negation=null;
        if (this.notBox.isSelected()) {
            row.setNegationID("NOT");
            row.setNegationName("NOT");
        }
        String desc = this.descriptiontextField.getText();
        if (desc != null && desc.length()>2) {
            row.setDescription(desc);
        }

        boolean useLastSource=false;
        if (this.lastSourceBox.isSelected()) {
            useLastSource=true;
            this.lastSourceBox.setSelected(false);
        }
        String src = this.pubTextField.getText();
        if (src != null && src.length() >2) {
            row.setPub(src); this.lastSource.setValue(src);
        } else if (useLastSource && this.lastSource.getValue().length()>0) {
            row.setPub(this.lastSource.getValue());
        }

        String bcurator = this.settings.getBioCuratorId();
        if (bcurator != null && ! bcurator.equals("null")) {
            row.setAssignedBy(bcurator);
        }

        String date = getDate();
        row.setDateCreated(date);

        table.getItems().add(row);
        clearFields();
    }

    /** Resets all of the fields after the user has entered a new annotation.*/
    private void clearFields() {
        this.diseaseNameTextField.clear();
        this.hpoNameTextField.clear();
        this.IEAbutton.setSelected(true);
        this.frequencyTextField.clear();
        this.notBox.setSelected(false);
        this.descriptiontextField.clear();
        this.pubTextField.clear();
        this.frequencyChoiceBox.setValue(null);
        this.ageOfOnsetChoiceBox.setValue(null);
    }


    private String getDate() {
        Date dNow = new Date( );
        SimpleDateFormat ft =
                new SimpleDateFormat ("yyyy-MM-dd");
        return ft.format(dNow);
    }

    /** Delete the marked row of the table. */
    public void deleteAnnotation () {
        ObservableList<PhenoRow> phenoSelected, allPheno;
            allPheno = table.getItems();
            phenoSelected = table.getSelectionModel().getSelectedItems();

            phenoSelected.forEach(allPheno::remove);
    }


    public void fetchTextMining() {
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        Optional<TextMiningResult> textMiningResult = TextMiningAnalysis.run(stage);
        if (textMiningResult.isPresent()) {
            // data container with results
            TextMiningResult result = textMiningResult.get();

            Set<Term> yesTerms = result.getYesTerms();   // set of YES terms approved by the curator
            Set<Term> notTerms = result.getNotTerms();   // set of NOT terms approved by the curator
            String pmid = result.getPMID();              // PMID of the publication

            for (Term t : yesTerms) {
                addTextMinedAnnotation(t.getLabel(), pmid, false);
            }
            for (Term t : notTerms) {
                addTextMinedAnnotation(t.getLabel(), pmid, true);
            }
        }

        /*
        TextMiningAnalyzer analyzer = new BiolarkAnalysis();
        if (analyzer.getStatus()) {
            Set<String> yesTerms = analyzer.getYesTerms();
            Set<String> notTerms = analyzer.getNotTerms();
            String pmid = analyzer.getPmid();

            for (String label : yesTerms) {
                addTextMinedAnnotation(label, pmid, false);
            }
            for (String label : notTerms) {
                addTextMinedAnnotation(label, pmid, true);
            }
        }*/
    }

    public void aboutWindow() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("HPO Phenote");
        alert.setHeaderText("Phenote for Human Phenotype Ontology");
        String s ="This is a simple tool for revising and creating\nHPO Annotation files for rare disease.";
        alert.setContentText(s);
        alert.showAndWait();
    }


    private void savePhenoteFileAt(File file) {
        if(file == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("HPO Phenote");
            alert.setHeaderText("Error");
            String s ="Could not retrieve name of file to save";
            alert.setContentText(s);
            alert.showAndWait();
            return;
        }
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(file));
            br.write(header+ "\n");
            List<PhenoRow> phenorows = table.getItems();
            for (PhenoRow pr : phenorows) {
                br.write(pr.toString() + "\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /** Save the modified file at the original location, showing a file chooser so the user can confirm */
    public void savePhenoteFile() {
        if (this.currentPhenoteFileFullPath==null) {
            saveAsPhenoteFile();
            return;
        }
        boolean doWrite = PopUps.getBooleanFromUser("Overwrite original file?",
                String.format("Save to %s",this.currentPhenoteFileFullPath),"Save file?");
        if (doWrite) {
            File f = new File(this.currentPhenoteFileFullPath);
            savePhenoteFileAt(f);
        }
    }
    /** Save the modified file at a location chosen by user */
    public void saveAsPhenoteFile() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TAB/TSV files (*.tab)", "*.tab");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName(this.currentPhenoteFileBaseName);
        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);
        savePhenoteFileAt(file);


    }

    /** Set the format of the date to yyyy-mm-dd for all rows if we can parse the old date format */
    public void correctDateFormat() {
        List<PhenoRow> phenorows = table.getItems();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (PhenoRow pr :phenorows) {
            String olddate = pr.getDateCreated();
            Date newdate = DateUtil.getDate(olddate);
            if (newdate != null)
                pr.setDateCreated(sdf.format(newdate));
        }
        table.refresh();
    }


    /**
     * Runs after user clicks Settings/Set biocurator MenuItem and asks user to provide the ID.
     */
    @FXML
    void setBiocuratorMenuItemClicked(ActionEvent event) {
        String biocurator = PopUps.getStringFromUser("Biocurator ID",
                "e.g. HPO:rrabbit", "Enter your biocurator ID:");
        if (biocurator!=null) {
            this.settings.setBioCuratorId(biocurator);

            PopUps.showInfoMessage(String.format("Biocurator ID set to \n\"%s\"",
                    biocurator), "Success");

        } else {
            PopUps.showInfoMessage("Biocurator ID not set.",
                    "Information");
        }
        event.consume();
        saveSettings();
    }

    /** Some old records do not have a valid assigned by. This
     * button will go through all rows and add the current biocurator to
     * the assigned by field. If the evidence code is missing, it will
     * set it to IEA, and it will set the reference to the current
     * disease ID (usually OMIM:123456)
     */
    @FXML
    void setAssignedByButtonClicked() {
        List<PhenoRow> phenorows = table.getItems();
        String bcurator = settings.getBioCuratorId();
        for (PhenoRow pr :phenorows) {
            String oldAssignedBy = pr.getAssignedBy();
            if (oldAssignedBy==null || oldAssignedBy.length()<2) {
                pr.setAssignedBy(bcurator);
            // check for these rows if the evidence field is set
                String evi = pr.getEvidenceID();
                if (evi == null || evi.length()<3) {
                    pr.setEvidenceID("IEA");
                }
                String pub = pr.getPub();
                if (pub==null || pub.length()<5) {
                    String diseaseid = pr.getDiseaseID();
                    pr.setPub(diseaseid);
                }
            }
        }
        table.refresh();
    }

    @FXML
    public void showSettings() {
        String set = settings.toString();
        PopUps.showInfoMessage(set,"Current settings");
    }

    @FXML
    public void newFile() {
        clearFields();
        table.getItems().clear();
        this.currentPhenoteFileFullPath=null;
        this.currentPhenoteFileBaseName=null;
        this.lastSource.setValue("");
    }

    @FXML
    public void openByMIMnumber() {
        String dirpath = settings.getDefaultDirectory();
        if (dirpath==null) {
            PopUps.showInfoMessage("Please set default Phenote directory\n in Settings menu",
                    "Error: Default directory not set");
            return;
        }
        String mimID = PopUps.getStringFromUser("Enter MIM ID to open",
                "Enter the 6 digit MIM id of the Phenote file to open",
                "MIM id");
        mimID=mimID.trim();
        Integer i=null;
        try{
            i= Integer.parseInt(mimID);
        } catch (NumberFormatException nfe) {
            PopUps.showException("Error getting MIM ID",
                    String.format("Malformed MIM ID entered: %s",mimID),
                    nfe.toString(),nfe);
        }
        if (mimID.length() != 6) {
            PopUps.showInfoMessage(String.format("MIMId needs to be 6 digits (you entered: %s",mimID),
                    "Error: Malformed MIM ID");
            return;
        }

        String basename=String.format("OMIM-%d.tab",i);
        File f = new File(dirpath + File.separator + basename);
        if (! f.exists()) {
            PopUps.showInfoMessage(String.format("Could not find file %s at \n%s",basename,f.getAbsoluteFile()),
                    "Error: Malformed MIM ID");
            return;
        }
        populateTable(f);

    }

    @FXML
    public void setDefaultPhenoteFileDirectory() {
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        File dir = PopUps.selectDirectory(stage,null, "Choose default Phenote file directory");
        this.settings.setDefaultDirectory(dir.getAbsolutePath());
        saveSettings();
    }


}
