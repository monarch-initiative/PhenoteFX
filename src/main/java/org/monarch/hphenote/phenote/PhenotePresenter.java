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
import com.sun.org.apache.bcel.internal.generic.POP;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
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

import org.monarch.hphenote.gui.ExceptionDialog;
import org.monarch.hphenote.gui.PopUps;
import org.monarch.hphenote.gui.ProgressForm;
import org.monarch.hphenote.io.*;
import org.monarch.hphenote.model.Frequency;
import org.monarch.hphenote.model.HPOOnset;
import org.monarch.hphenote.model.PhenoRow;
import org.monarch.hphenote.model.Settings;
import org.monarch.hphenote.validation.*;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by robinp on 5/22/17.
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

    private StringProperty diseaseName,diseaseID;

    /* ------ MENU ---------- */
    @FXML
    MenuItem openFileMenuItem;

    @FXML MenuItem exitMenuItem;

    @FXML MenuItem downloadHPOmenuItem;

    @FXML MenuItem downloadMedgenMenuItem;

    @FXML Button setAllDiseaseNamesButton;

    @FXML ChoiceBox<String> ageOfOnsetChoiceBox;

    @FXML RadioButton IEAbutton;
    @FXML RadioButton ICEbutton;
    @FXML RadioButton PCSbutton;
    @FXML RadioButton TASbutton;

    @FXML TextField frequencyTextField;

    @FXML TextField descriptiontextField;
    /** The publication (source) for the annotation (refered to as "pub" in the small files).*/
    @FXML TextField pubTextField;

    @FXML CheckBox notBox;

    @FXML Button addAnnotationButton;
    @FXML Button deleteAnnotationButton;
    @FXML Button fetchTextMiningButton;

    @FXML Button correctDateFormatButton;

    private ToggleGroup evidenceGroup;



    private Settings settings=null;

    private Map<String,String> omimName2IdMap;

    private Map<String,String> hponame2idMap;

    private HPOOnset hpoOnset;

    private Frequency frequency;
    /** Header of the current Phenote file. */
    private String header=null;
    /** Base name of the current Phenote file */
    private String currentPhenoteFileBaseName =null;

    private String currentPhenoteFileFullPath=null;



    /** This is the table where the phenotype data will be shown. */
    TableView<PhenoRow> table=null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSettings();
        checkReadiness();
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

        evidenceGroup = new ToggleGroup();
        IEAbutton.setToggleGroup(evidenceGroup);
        ICEbutton.setToggleGroup(evidenceGroup);
        PCSbutton.setToggleGroup(evidenceGroup);
        TASbutton.setToggleGroup(evidenceGroup);
        IEAbutton.setSelected(true);

        // todo getUserData is returning Null.
        evidenceGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle){
                    if (evidenceGroup.getSelectedToggle() != null) {
                        System.out.println(evidenceGroup.getSelectedToggle().getUserData());
                    }
                }
        });
        hpoOnset = HPOOnset.factory();
        ageOfOnsetChoiceBox.setItems(hpoOnset.getOnsetTermList());
        // promopt
        this.descriptiontextField.setPromptText("free text description of anything not captured with standards (optional)");
        this.pubTextField.setPromptText("Source of assertion (usually PubMed, OMIM, Orphanet...)");
        this.frequencyTextField.setPromptText("One of the HPO Terms or a specific value sich as 7/13 or 54%");

        //Tooltips
        this.diseaseIDlabel.setTooltip(new Tooltip("Name of a disease (OMIM IDs will be automatically populated)"));
        //this.hpo


    }

    private void inputHPOandMedGen() {
        MedGenParser parser = new MedGenParser();
        omimName2IdMap = parser.getOmimName2IdMap();
        HPOParser parser1 = new HPOParser();
        hponame2idMap = parser1.getHpoName2IDmap();

    }

    private void checkReadiness() {
        StringBuffer sb = new StringBuffer();
        boolean ready = true;
        boolean hpoready = org.monarch.hphenote.gui.Platform.checkHPOFileDownloaded();
        if (! hpoready) {
            sb.append("HPO File not found. ");
            ready = false;
        }
        boolean medgenready = org.monarch.hphenote.gui.Platform.checkMedgenFileDownloaded();
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
     * {@link org.monarch.hphenote.model.Settings} bean.
     * @return
     */
    private void loadSettings() {
        File defaultSettingsPath = new File(org.monarch.hphenote.gui.Platform.getHPhenoteDir().getAbsolutePath()
                + File.separator + settingsFileName);
        if (!org.monarch.hphenote.gui.Platform.getHPhenoteDir().exists()) {
            File fck = new File(org.monarch.hphenote.gui.Platform.getHPhenoteDir().getAbsolutePath());
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
     * {@link org.monarch.hphenote.model.Settings} bean is dumped
     * in XML format to platform-dependent default location.
     */
    private void saveSettings() {
        File hrmdDirectory = org.monarch.hphenote.gui.Platform.getHPhenoteDir();
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
        if (omimName2IdMap != null)
            TextFields.bindAutoCompletion(diseaseNameTextField, omimName2IdMap.keySet());
        this.diseaseID = new SimpleStringProperty(this, "diseaseID", "");
        this.diseaseName = new SimpleStringProperty(this,"diseaseName","");
        diseaseIDlabel.textProperty().bindBidirectional(diseaseID);
        diseaseNameTextField.textProperty().bindBidirectional(diseaseName);
        diseaseNameTextField.setOnAction( e-> {
            String name = diseaseName.getValue();
            diseaseID.setValue(omimName2IdMap.get(name));
        });
        if (hponame2idMap != null) {
            TextFields.bindAutoCompletion(hpoNameTextField, hponame2idMap.keySet());
        }
        frequency = Frequency.factory();
        TextFields.bindAutoCompletion(frequencyTextField,frequency.getFrequencyTermList());


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
                System.exit(1);
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
        phenotypeIDcol.setOnEditCommit(
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
        );
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

    }

    /** This function intends to set all of the disease names to the name in the text field.
     * We can use this to correct the disease names for legacy files where we are using multiple different
     * disease names. Or in cases that the canonical name was updated. If the textfield is empty, the function
     * quietly does nothing.
     */
    public void setAllDiseasesNames() {
        List<PhenoRow> phenorows = table.getItems();
        String diseaseName = diseaseNameTextField.getText();
        String diseaseID = this.omimName2IdMap.get(diseaseName);
        if (diseaseID==null) {
            return;
        }
        diseaseID = String.format("OMIM:%s",diseaseID);
        for (PhenoRow pr :phenorows) {
            pr.setDiseaseID(diseaseID);
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

    public void addAnnotation() {
        PhenoRow row = new PhenoRow();
        // Disease ID (OMIM)
        String diseaseID;
        String diseaseName = this.diseaseNameTextField.getText().trim();
        diseaseID = this.omimName2IdMap.get(diseaseName);
        if (diseaseID == null) diseaseID = "?";
        row.setDiseaseID(diseaseID);
        row.setDiseaseName(diseaseName);
        // HPO Id
        String hpoId;
        String hpoName = this.hpoNameTextField.getText().trim();
        hpoId = this.hponame2idMap.get(hpoName);
        row.setPhenotypeID(hpoId);
        row.setPhenotypeName(hpoName);
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
        frequencyName = this.frequencyTextField.getText().trim();
        if (frequencyName != null && frequencyName.length()>2) {
            //String frequencyID = this.frequency.getID(frequencyName);
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
        String src = this.pubTextField.getText();
        if (src != null && src.length() >2) {
            row.setPub(src);
        }

        String bcurator = this.settings.getBiocurator().getBioCuratorId();
        if (bcurator != null && ! bcurator.equals("null")) {
            row.setAssignedBy(bcurator);
        }

        String date = getDate();
        row.setDateCreated(date);

        table.getItems().add(row);
        //nameInput.clear();
        //priceInput.clear();
        //quantityInput.clear();
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
        System.err.println("fetch textmining TODO");
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
                "e.g. HPO:wwhite", "Enter your biocurator ID:");
        if (biocurator!=null) {
            this.settings.getBiocurator().setBioCuratorId(biocurator);

            PopUps.showInfoMessage(String.format("Biocurator ID set to \n\"%s\"",
                    biocurator), "Success");
            return;
        }
        PopUps.showInfoMessage("Biocurator ID not set.",
                "Information");
        event.consume();
    }


}
