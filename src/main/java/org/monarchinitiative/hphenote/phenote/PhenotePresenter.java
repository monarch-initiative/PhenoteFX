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

import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.formats.hpo.HpoTermRelation;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.hphenote.gui.*;
import org.monarchinitiative.hphenote.gui.help.HelpViewFactory;
import org.monarchinitiative.hphenote.gui.progresspopup.ProgressPopup;
import org.monarchinitiative.hphenote.gui.settings.SettingsViewFactory;
import org.monarchinitiative.hphenote.io.*;
import org.monarchinitiative.hphenote.model.Frequency;
import org.monarchinitiative.hphenote.model.HPOOnset;
import org.monarchinitiative.hphenote.model.PhenoRow;
import org.monarchinitiative.hphenote.model.Settings;
import org.monarchinitiative.hphenote.validation.*;
import com.github.monarchinitiative.hpotextmining.HPOTextMining;
import com.github.monarchinitiative.hpotextmining.TextMiningResult;
import com.github.monarchinitiative.hpotextmining.model.PhenotypeTerm;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by robinp on 5/22/17.
 * Main presenter for the HPO Phenote App.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.2.4 (2017-12-12)
 */
public class PhenotePresenter implements Initializable {
    private static final Logger logger = LogManager.getLogger();
    private static final String settingsFileName = "hphenote.settings";

    private static String HP_OBO_URL ="https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.obo";
    private static final String MEDGEN_URL="ftp://ftp.ncbi.nlm.nih.gov/pub/medgen/MedGen_HPO_OMIM_Mapping.txt.gz";
    private static final String MEDGEN_BASENAME="MedGen_HPO_OMIM_Mapping.txt.gz";

    @FXML private AnchorPane anchorpane;
    /** This is the main border pane of the application. We will inject the table into it in the initialize method. */
    @FXML private BorderPane bpane;
    @FXML private TextField diseaseNameTextField;
    @FXML private TextField hpoNameTextField;
    @FXML private Label diseaseIDlabel;
    /* ------ MENU ---------- */
    @FXML private MenuItem newMenuItem;
    @FXML private MenuItem openFileMenuItem;
    @FXML private MenuItem openByMimMenuItem;
    @FXML private MenuItem exitMenuItem;
    @FXML private MenuItem closeMenuItem;
    @FXML private MenuItem saveMenuItem;
    @FXML private MenuItem saveAsMenuItem;
    @FXML private MenuItem downloadHPOmenuItem;
    @FXML private MenuItem downloadMedgenMenuItem;
    @FXML private MenuItem showSettingsMenuItem;
    @FXML private Button setAllDiseaseNamesButton;
    @FXML private ChoiceBox<String> ageOfOnsetChoiceBox;
    @FXML private RadioButton IEAbutton;
    @FXML private RadioButton ICEbutton;
    @FXML private RadioButton PCSbutton;
    @FXML private RadioButton TASbutton;
    @FXML private TextField frequencyTextField;
    @FXML private ChoiceBox<String> frequencyChoiceBox;
    @FXML private TextField descriptiontextField;
    /** The publication (source) for the annotation (refered to as "pub" in the small files). */
    @FXML private TextField pubTextField;
    @FXML private CheckBox notBox;
    @FXML private Button addAnnotationButton;
    @FXML private Button deleteAnnotationButton;
    @FXML private Button fetchTextMiningButton;
    @FXML Button correctDateFormatButton;
    @FXML private Label lastSourceLabel;
    @FXML private CheckBox lastSourceBox;

    private ToggleGroup evidenceGroup;

    private StringProperty diseaseName, diseaseID;

    private Settings settings = null;

    private Map<String, String> omimName2IdMap;

    private Map<String, String> hponame2idMap;

    private Map<String, String> hpoSynonym2LabelMap;

    private HPOOnset hpoOnset;
    /** Is there unsaved work? */
    private boolean dirty=false;

    /**
     * Ontology used by Text-mining widget. Instantiated at first click in {@link #fetchTextMining()}
     * TODO - refactor to ontolib.
     */
    private static Ontology ontology;
    /** Ontology object with just the phenotypic abnormality terms. */
    private com.github.phenomics.ontolib.ontology.data.Ontology<HpoTerm, HpoTermRelation> abnormalPhenoSubOntology=null;

    private Frequency frequency;
    /**Header of the current Phenote file.*/
    private String header = null;
    /**Base name of the current Phenote file*/
    private String currentPhenoteFileBaseName = null;

    private String currentPhenoteFileFullPath = null;
    /** The last source used, e.g., a PMID (use this to avoid having to re-enter the source)*/
    private StringProperty lastSource = new SimpleStringProperty("");


    /**This is the table where the phenotype data will be shown.*/
    @FXML private TableView<PhenoRow> table = null;
    @FXML private TableColumn<PhenoRow, String> diseaseIDcol;
    @FXML private TableColumn<PhenoRow, String> diseaseNamecol;
    @FXML private TableColumn<PhenoRow, String> phenotypeIDcol;
    @FXML private TableColumn<PhenoRow, String> phenotypeNameCol;
    @FXML private TableColumn<PhenoRow, String> ageOfOnsetIDcol;
    @FXML private TableColumn<PhenoRow, String> ageOfOnsetNamecol;
    @FXML private TableColumn<PhenoRow, String> evidenceIDcol;
    @FXML private TableColumn<PhenoRow, String> frequencyCol;
    @FXML private TableColumn<PhenoRow, String> sexIDcol;
    @FXML private TableColumn<PhenoRow, String> negationCol;
    @FXML private TableColumn<PhenoRow, String> descriptionCol;
    @FXML private TableColumn<PhenoRow, String> pubCol;
    @FXML private TableColumn<PhenoRow, String> assignedByCol;
    @FXML private TableColumn<PhenoRow, String> dateCreatedCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSettings();
        checkReadiness();
        setDefaultHeader();
        inputHPOandMedGen();
        setupAutocomplete();

        anchorpane.setPrefSize(1400, 1000);
        setUpTable();
        table.setItems(getRows());
        // set up buttons
        // TODO extend this to ask about saving unsaved work.
        exitMenuItem.setOnAction(e -> exitGui());
        openFileMenuItem.setOnAction(e -> openPhenoteFile(e));

        this.diseaseNameTextField.setPromptText("Will default to disease name in first row if left empty");
        this.hpoNameTextField.setPromptText("Enter preferred label or synonym (will be automatically converted)");

        evidenceGroup = new ToggleGroup();
        IEAbutton.setToggleGroup(evidenceGroup);
        ICEbutton.setToggleGroup(evidenceGroup);
        PCSbutton.setToggleGroup(evidenceGroup);
        TASbutton.setToggleGroup(evidenceGroup);
        IEAbutton.setSelected(true);
        hpoOnset = HPOOnset.factory();
        ageOfOnsetChoiceBox.setItems(hpoOnset.getOnsetTermList());
        this.frequency = Frequency.factory();
        frequencyChoiceBox.setItems(frequency.getFrequencyTermList());
        this.descriptiontextField.setPromptText("free text description of anything not captured with standards (optional)");
        this.pubTextField.setPromptText("Source of assertion (usually PubMed, OMIM, Orphanet...)");
        this.frequencyTextField.setPromptText("A value such as 7/13 or 54% (leave empty if pulldown used)");
        this.diseaseIDlabel.setTooltip(new Tooltip("Name of a disease (OMIM IDs will be automatically populated)"));

        pubTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                String txt = pubTextField.getText();
                txt = txt.replaceAll("\\s", "");
                pubTextField.setText(txt);
            }
        });

        this.lastSourceLabel.textProperty().bind(this.lastSource);
        setUpKeyAccelerators();
    }

    /** Add short cuts to the menu items. */
    private void setUpKeyAccelerators() {
        this.newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N,KeyCombination.META_DOWN));
        this.openFileMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN));
        this.openByMimMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.META_DOWN));
        this.saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S,KeyCombination.META_DOWN));
        this.saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S,KeyCombination.SHIFT_DOWN,KeyCombination.META_DOWN));
        this.closeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.W,KeyCombination.META_DOWN));
        this.downloadHPOmenuItem.setAccelerator(new KeyCodeCombination(KeyCode.H,KeyCombination.META_DOWN));
        this.exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q,KeyCombination.META_DOWN));
    }

    /**
     * When we create a new annotation file,
     * we need to set the Header line here.
     */
    private void setDefaultHeader() {
        this.header = "Disease ID\tDisease Name\tGene ID\tGene Name\tGenotype\tGene Symbol(s)\tPhenotype ID\tPhenotype Name\tAge of Onset ID\tAge of Onset Name\tEvidence ID\tEvidence Name\tFrequency\tSex ID\tSex Name\tNegation ID\tNegation Name\tDescription\tPub\tAssigned by\tDate Created";
    }

    /**
     * Called by the initialize method. Serves to set up the
     * Maps with HPO and Disease name information for the autocompletes.
     */
    private void inputHPOandMedGen() {
        MedGenParser medGenParser = new MedGenParser();
        omimName2IdMap = medGenParser.getOmimName2IdMap();
        try {
            HPOParser parser2 = new HPOParser();
            hponame2idMap = parser2.getHpoName2IDmap();
            hpoSynonym2LabelMap = parser2.getHpoSynonym2PreferredLabelMap();
            this.abnormalPhenoSubOntology = parser2.getAbnormalPhenoSubOntology();
        } catch (Exception e) {
            int ln = Thread.currentThread().getStackTrace()[1].getLineNumber();
            String msg = String.format("Could not parse ontology file [PhenotePresenter line %d]: %s", ln, e.toString());
            logger.error(msg);
            ErrorDialog.displayException("Error", String.format(msg), e);
        }
    }

    /**
     * Checks if the HPO and medgen files have been downloaded already, and if
     * not shows an alert window.
     */
    private void checkReadiness() {
        StringBuffer sb = new StringBuffer();
        boolean ready = true;
        boolean hpoready = org.monarchinitiative.hphenote.gui.Platform.checkHPOFileDownloaded();
        if (!hpoready) {
            sb.append("HPO File not found. ");
            ready = false;
        }
        boolean medgenready = org.monarchinitiative.hphenote.gui.Platform.checkMedgenFileDownloaded();
        if (!medgenready) {
            sb.append("MedGen_HPO_OMIM_Mapping.txt.gz not found. ");
            ready = false;
        }
        if (!ready) {
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


    /**
     * Write the settings from the current session to file and exit.
     */
    private void exitGui() {
        saveSettings();
        javafx.application.Platform.exit();
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
     * Parse XML file from standard location and return as {@link Settings} bean.
     * @return
     */
    private void loadSettings() {
        File defaultSettingsPath = new File(org.monarchinitiative.hphenote.gui.Platform.getPhenoteFXDir().getAbsolutePath()
                + File.separator + settingsFileName);
        if (!org.monarchinitiative.hphenote.gui.Platform.getPhenoteFXDir().exists()) {
            File fck = new File(org.monarchinitiative.hphenote.gui.Platform.getPhenoteFXDir().getAbsolutePath());
            if (!fck.mkdir()) { // make sure config directory is created, exit if not
                showAlert("Unable to create HRMD-gui config directory.\n"
                        + "Even though this is a serious problem I'm exiting gracefully. Bye.");
                System.exit(1);
            }
        }
        if (!defaultSettingsPath.exists()) {
            this.settings = new Settings();
            return; // create blank new Settings
        }
        this.settings = Settings.factory(defaultSettingsPath.getAbsolutePath());
    }

    /**
     * This method gets called when user chooses to close Gui. Content of
     * {@link Settings} bean is dumped
     * in XML format to platform-dependent default location.
     */
    private void saveSettings() {
        File hrmdDirectory = org.monarchinitiative.hphenote.gui.Platform.getPhenoteFXDir();
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
    /** Uses the {@link WidthAwareTextFields} class to set up autocompletion for the disease name and the HPO name */
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
        this.diseaseName = new SimpleStringProperty(this, "diseaseName", "");
        diseaseIDlabel.textProperty().bindBidirectional(diseaseID);
        diseaseNameTextField.textProperty().bindBidirectional(diseaseName);
        diseaseNameTextField.setOnAction(e -> {
            String name = diseaseName.getValue();
            diseaseID.setValue(omimName2IdMap.get(name));
        });
        if (hpoSynonym2LabelMap != null) {
            WidthAwareTextFields.bindWidthAwareAutoCompletion(hpoNameTextField, hpoSynonym2LabelMap.keySet());
        }
    }

    /**
     * Open a phenote file ("small file") and populate the table with it.
     */
    private void openPhenoteFile(ActionEvent event) {
        if (dirty) {
            boolean discard=PopUps.getBooleanFromUser("Discard unsaved changes?","Unsaved work on current annotation file","Discard unsaved work?");
            if (! discard) return;
        }
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File f = fileChooser.showOpenDialog(stage);
        if (f != null) {
            populateTable(f);
        }
    }

    /**
     * Put rows into the table that represent the disease annotations from the file.
     * @param f "small file" with HPO disease annotations.
     */
    private void populateTable(File f) {
        logger.trace(String.format("About to populate the table from file %s",f.getAbsolutePath()));
        List<String> errors = new ArrayList<>();
        setUpTable();
        ObservableList<PhenoRow> phenolist = FXCollections.observableArrayList();
        this.currentPhenoteFileBaseName = f.getName();
        this.currentPhenoteFileFullPath = f.getAbsolutePath();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = null;
            // Note first line is header
            line = br.readLine();
            if (!line.startsWith("Disease ID")) {
                System.err.println("Error malformed first line: " + line);
                PopUps.showInfoMessage("Error: malformed header line (did not start with \"Disease ID\")",
                        String.format("Exit and Regenerate this file: (line:%s)", line));
            } else {
                header = line;
            }
            while ((line = br.readLine()) != null) {
                //System.err.println(line);
                try {
                    PhenoRow row = PhenoRow.constructFromLine(line);
                    phenolist.add(row);
                } catch (Exception e) {
                    errors.add(e.getMessage()); // skip this line
                }
            }
            logger.trace(String.format("About to add %d lines to the table",phenolist.size()));
            this.table.setItems(phenolist);

        } catch (IOException e) {
            e.printStackTrace();
            this.currentPhenoteFileBaseName = null; // couldnt open this file!
        }
        if (errors.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String e : errors) {
                sb.append(e + "\n");
            }
            ErrorDialog.display("Error",sb.toString());
        }
    }


//    public void launch() {
//    }


    public ObservableList<PhenoRow> getRows() {
        ObservableList<PhenoRow> olist = FXCollections.observableArrayList();
        olist.add(new PhenoRow());
        return olist;
    }


    /** Set up the table and define the behavior of the columns */
    private void setUpTable() {
        table.setEditable(true);

        diseaseIDcol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("diseaseID"));
        diseaseIDcol.setCellFactory(TextFieldTableCell.forTableColumn());
        diseaseIDcol.setOnEditCommit(cee -> cee.getTableView().getItems().get(cee.getTablePosition().getRow()).setDiseaseID(cee.getNewValue()) );

        diseaseNamecol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("diseaseName"));
        diseaseNamecol.setCellFactory(TextFieldTableCell.forTableColumn());
        diseaseNamecol.setOnEditCommit(cee -> cee.getTableView().getItems().get(cee.getTablePosition().getRow()).setDiseaseName(cee.getNewValue()));

        phenotypeIDcol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("phenotypeID"));
        phenotypeIDcol.setCellFactory(TextFieldTableCell.forTableColumn());
        phenotypeIDcol.setEditable(false);
        phenotypeIDcol.setSortable(true);

        phenotypeNameCol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("phenotypeName"));
        phenotypeNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        phenotypeNameCol.setEditable(false);
        phenotypeNameCol.setSortable(true);

        ageOfOnsetIDcol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("ageOfOnsetID"));
        ageOfOnsetIDcol.setCellFactory(TextFieldTableCell.forTableColumn());
        ageOfOnsetIDcol.setEditable(false);

        ageOfOnsetNamecol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("ageOfOnsetName"));
        ageOfOnsetNamecol.setCellFactory(TextFieldTableCell.forTableColumn());
        ageOfOnsetNamecol.setEditable(false);

        evidenceIDcol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("evidenceID"));
        evidenceIDcol.setCellFactory(TextFieldTableCell.forTableColumn());
        evidenceIDcol.setEditable(true);
        evidenceIDcol.setOnEditCommit( event -> {
            String newEvidence = event.getNewValue();
            if (EvidenceValidator.isValid(newEvidence)) {
                ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setEvidenceID(event.getNewValue());
            }
            dirty=true;
            event.getTableView().refresh();
        }
        );
        // do not show evidenceName, it is redundant!

        frequencyCol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("frequency"));
        frequencyCol.setCellFactory(TextFieldTableCell.forTableColumn());
        frequencyCol.setEditable(false);

        sexIDcol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("sexID"));
        sexIDcol.setCellFactory(TextFieldTableCell.forTableColumn());
        sexIDcol.setOnEditCommit( event -> event.getTableView().getItems().get(event.getTablePosition().getRow()).setSexID(event.getNewValue()));
        // do not show sexName, it is redundant!
        negationCol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("negationID"));
        negationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        negationCol.setOnEditCommit( event -> {
                        if (NotValidator.isValid(event.getNewValue())) {
                            ((PhenoRow) event.getTableView().getItems().get(event.getTablePosition().getRow())).setNegationID(event.getNewValue());
                        }
                        dirty=true;
                        event.getTableView().refresh();
                    }
        );
        // do not show negationName, it is redundant!

        descriptionCol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("description"));
        descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionCol.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setDescription(event.getNewValue());
            dirty=true;
        });

        pubCol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("pub"));
        pubCol.setCellFactory(TextFieldTableCell.forTableColumn());
        pubCol.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setPub(event.getNewValue());
            dirty=true;
        });

        assignedByCol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("assignedBy"));
        assignedByCol.setCellFactory(TextFieldTableCell.forTableColumn());
        assignedByCol.setOnEditCommit(event -> event.getTableView().getItems().get(event.getTablePosition().getRow()).setAssignedBy(event.getNewValue()));

        dateCreatedCol.setCellValueFactory(new PropertyValueFactory<PhenoRow, String>("dateCreated"));
        dateCreatedCol.setCellFactory(TextFieldTableCell.forTableColumn());
        dateCreatedCol.setOnEditCommit(event -> event.getTableView().getItems().get(event.getTablePosition().getRow()).setDateCreated(event.getNewValue()));

        // The following makes the table onloy show the defined columns (otherwise, an "extra" column is shown)
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Get path to the .hphenote directory, download the file, and if successful
     * set the path to the file in the settings.
     */
    public void downloadHPO(ActionEvent event) {
        ProgressPopup ppopup = new ProgressPopup("HPO download","downloading hp.obo...");
        ProgressIndicator progressIndicator = ppopup.getProgressIndicator();
        String basename="hp.obo";
        File dir = Platform.getPhenoteFXDir();
        Downloader downloadTask = new Downloader(dir.getAbsolutePath(), HP_OBO_URL, basename, progressIndicator);
        downloadTask.setOnSucceeded( e -> {
            String abspath=(new File(dir.getAbsolutePath() + File.separator + basename)).getAbsolutePath();
            logger.trace("Setting hp.obo path to "+abspath);
            saveSettings();
            this.settings.setHpoFile(abspath);
            ppopup.close();
        });
        downloadTask.setOnFailed(e -> {
            logger.error("Download of hp.obo failed");
            PopUps.showInfoMessage("Download of hp.obo failed","Error");
            ppopup.close();
        });
        try {
            ppopup.startProgress(downloadTask);
        } catch (InterruptedException e) {
            PopUps.showException("Exception","Error","Could not download regulatory build", e);
            logger.error(String.format("Could not download HPO: %s",e.getMessage()));
        }
        event.consume();
    }

    /**
     * Download the medgen file to the .hphenote directory, and if successful
     * set the path to the file in the settings.
     */
    public void downloadMedGen() {
        ProgressPopup ppopup = new ProgressPopup("Medgen download",String.format("downloading %s...",MEDGEN_BASENAME));
        ProgressIndicator progressIndicator = ppopup.getProgressIndicator();
        File dir = Platform.getPhenoteFXDir();
        Downloader downloadTask = new Downloader(dir.getAbsolutePath(), MEDGEN_URL, MEDGEN_BASENAME, progressIndicator);
        downloadTask.setOnSucceeded( e -> {
            String abspath=(new File(dir.getAbsolutePath() + File.separator + MEDGEN_BASENAME)).getAbsolutePath();
            logger.trace(String.format("Setting %s path to %s",MEDGEN_BASENAME,abspath));
            saveSettings();
            this.settings.setMedgenFile(abspath);
            ppopup.close();
        });
        downloadTask.setOnFailed(e -> {
            logger.error(String.format("Download of %s failed",MEDGEN_BASENAME));
            PopUps.showInfoMessage(String.format("Download of %s failed",MEDGEN_BASENAME),"Error");
            ppopup.close();
        });
        try {
            ppopup.startProgress(downloadTask);
        } catch (InterruptedException e) {
            PopUps.showException("Exception","Error","Could not download medgen build", e);
            logger.error(String.format("Could not download %s: %s",MEDGEN_BASENAME,e.getMessage()));
        }
    }

    /**
     * This function intends to set all of the disease names to the name in the text field.
     * We can use this to correct the disease names for legacy files where we are using multiple different
     * disease names. Or in cases that the canonical name was updated. If the textfield is empty, the function
     * quietly does nothing. It assumes that the diseaseID is correct and does not try to change that.
     */
    public void setAllDiseasesNames() {
        List<PhenoRow> phenorows = table.getItems();
        String diseaseName = diseaseNameTextField.getText();
        if (diseaseName == null) {
            return;
        }
        for (PhenoRow pr : phenorows) {
            pr.setDiseaseName(diseaseName);
        }
        table.refresh();
    }

    /**
     * Some of our older files are missing the date created. This function
     * will look at all date entries and set them to today's date if the cell is empty.
     */
    public void setCreatedDateToTodayInAllEmptyRows() {
        List<PhenoRow> phenorows = table.getItems();
        String today = getDate();
        for (PhenoRow pr : phenorows) {
            String olddate = pr.getDateCreated();
            if (olddate == null || olddate.length() < 2)
                pr.setDateCreated(today);
        }
        table.refresh();
    }

    /**
     * This method adds one text-mined annotation as a row in the PhenoteFX table.
     * @param hpoid
     * @param hpoLabel
     * @param pmid
     * @param isNegated
     */
    private void addTextMinedAnnotation(String hpoid,String hpoLabel, String pmid, boolean isNegated) {
        PhenoRow row = new PhenoRow();
        row.setPhenotypeName(hpoLabel);
        row.setPhenotypeID(hpoid);
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
        dirty=true;
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
            evidence = "IEA";
        else if (ICEbutton.isSelected())
            evidence = "ICE";
        else if (PCSbutton.isSelected())
            evidence = "PCS";
        else if (TASbutton.isSelected())
            evidence = "TAS";
        row.setEvidenceID(evidence);
        row.setEvidenceName(evidence); // redundant in our format.
        // Age of onset
        String onsetID, onsetName;
        onsetName = ageOfOnsetChoiceBox.getValue();
        if (onsetName != null) {
            onsetID = hpoOnset.getID(onsetName);
            row.setAgeOfOnsetID(onsetID);
            row.setAgeOfOnsetName(onsetName);
        }
        String frequencyName = null;
        String freq = this.frequencyChoiceBox.getValue();
        if (freq != null) {
            frequencyName = freq;
        } else {
            frequencyName = this.frequencyTextField.getText().trim();
        }
        if (frequencyName != null && frequencyName.length() > 2) {
            row.setFrequency(frequencyName);
        }
        String negation = null;
        if (this.notBox.isSelected()) {
            row.setNegationID("NOT");
            row.setNegationName("NOT");
        }
        String desc = this.descriptiontextField.getText();
        if (desc != null && desc.length() > 2) {
            row.setDescription(desc);
        }

        boolean useLastSource = false;
        if (this.lastSourceBox.isSelected()) {
            useLastSource = true;
            this.lastSourceBox.setSelected(false);
        }
        String src = this.pubTextField.getText();
        if (src != null && src.length() > 2) {
            row.setPub(src);
            this.lastSource.setValue(src);
        } else if (useLastSource && this.lastSource.getValue().length() > 0) {
            row.setPub(this.lastSource.getValue());
        }

        String bcurator = this.settings.getBioCuratorId();
        if (bcurator != null && !bcurator.equals("null")) {
            row.setAssignedBy(bcurator);
        }

        String date = getDate();
        row.setDateCreated(date);

        table.getItems().add(row);
        clearFields();
        dirty=true;
    }

    /**
     * Resets all of the fields after the user has entered a new annotation.
     */
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
        this.lastSource.setValue(null);
    }


    private String getDate() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        return ft.format(dNow);
    }

    /**
     * Delete the marked row of the table.
     */
    @FXML private void deleteAnnotation() {
        ObservableList<PhenoRow> phenoSelected, allPheno;
        allPheno = table.getItems();
        phenoSelected = table.getSelectionModel().getSelectedItems();
        phenoSelected.forEach(allPheno::remove);
        dirty=true;
    }

    /** Create PopUp window with text-mining widget allowing to perform the mining. Process results*/
    @FXML
    public void fetchTextMining() {
        if (ontology == null) {
            if (!Platform.checkHPOFileDownloaded()) {
                System.err.println("Unable to perform text mining, download HP OBO file first");
                return;
            }
            try {
                ontology = parseOntology(settings.getHpoFile());
            } catch (IOException | OBOParserException e) {
                System.err.println(String.format("Unable to perform text mining, error parsing OBO file from location" +
                                " %s",
                        settings.getHpoFile()));
                e.printStackTrace();
                return;
            }
        }
        // at this point we either have ontology, or we printed an error.

        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        String server = "http://phenotyper.monarchinitiative.org:5678/cr/annotate";
        URL url = null;
        try {
            url = new URL(server);
        } catch (MalformedURLException e) {
            System.err.println(String.format("Error parsing url string of text mining server: %s", server));
        }

        HPOTextMining textMiningAnalysis = new HPOTextMining(ontology, url, stage);

        TextMiningResult result = textMiningAnalysis.runAnalysis();

        Set<PhenotypeTerm> approvedTerms = result.getTerms();   // set of terms approved by the curator
        String pmid = result.getPmid();              // PMID of the publication

        approvedTerms.forEach(term -> addTextMinedAnnotation(term.getHpoId(), term.getName(),pmid, !term.isPresent()));
        if (approvedTerms.size()>0) dirty=true;
    }

    /**
     * Parse OBO file into {@link Ontology} object.
     *
     * @param pathToOBOFile String with path to the OBO file
     * @return {@link Ontology} containing content of OBO file
     * @throws IOException        if incorrect path is provided
     * @throws OBOParserException
     */
    private static Ontology parseOntology(String pathToOBOFile) throws IOException, OBOParserException {
        OBOParser parser = new OBOParser(new OBOParserFileInput(pathToOBOFile), OBOParser.PARSE_DEFINITIONS);
        String result = parser.doParse();
        TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser.getDate());
        return Ontology.create(termContainer);
    }

    /** Show the about message */
    public void aboutWindow(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("PhenoteFX");
        alert.setHeaderText("PhenoteFX");
        String s = "A tool for revising and creating\nHPO Annotation files for rare disease.";
        alert.setContentText(s);
        alert.showAndWait();
        e.consume();
    }

    /**
     * @param e event triggered by show help command.
     */
    @FXML public void showHelpWindow(ActionEvent e) {
        logger.trace("Show help window");
        HelpViewFactory.openHelpDialog();
        e.consume();
    }


    private void savePhenoteFileAt(File file) {
        if (file == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("HPO Phenote");
            alert.setHeaderText("Error");
            String s = "Could not retrieve name of file to save";
            alert.setContentText(s);
            alert.showAndWait();
            return;
        }
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(file));
            br.write(header + "\n");
            List<PhenoRow> phenorows = table.getItems();
            for (PhenoRow pr : phenorows) {
                br.write(pr.toString() + "\n");
            }
            br.close();
            dirty=false;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Save the modified file at the original location, showing a file chooser so the user can confirm
     */
    public void savePhenoteFile() {
        if (this.currentPhenoteFileFullPath == null) {
            saveAsPhenoteFile();
            return;
        }
        boolean doWrite = PopUps.getBooleanFromUser("Overwrite original file?",
                String.format("Save to %s", this.currentPhenoteFileFullPath), "Save file?");
        if (doWrite) {
            File f = new File(this.currentPhenoteFileFullPath);
            savePhenoteFileAt(f);
            dirty=false;
        }
    }

    /**
     * Save the modified file at a location chosen by user
     */
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
        dirty=false;
    }

    /** Set the format of the date to yyyy-mm-dd for all rows if we can parse the old date format. */
    @FXML public void correctDateFormat() {
        List<PhenoRow> phenorows = table.getItems();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (PhenoRow pr : phenorows) {
            String olddate = pr.getDateCreated();
            Date newdate = DateUtil.getDate(olddate);
            if (newdate != null)
                pr.setDateCreated(sdf.format(newdate));
        }
        table.refresh();
        dirty=true;
    }


    /** Runs after user clicks Settings/Set biocurator MenuItem and asks user to provide the ID. */
    @FXML void setBiocuratorMenuItemClicked(ActionEvent event) {
        String biocurator = PopUps.getStringFromUser("Biocurator ID",
                "e.g. HPO:rrabbit", "Enter your biocurator ID:");
        if (biocurator != null) {
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

    /**
     * Some old records do not have a valid assigned by. This
     * button will go through all rows and add the current biocurator to
     * the assigned by field. If the evidence code is missing, it will
     * set it to IEA, and it will set the reference to the current
     * disease ID (usually OMIM:123456)
     */
    @FXML
    void setAssignedByButtonClicked() {
        List<PhenoRow> phenorows = table.getItems();
        String bcurator = settings.getBioCuratorId();
        for (PhenoRow pr : phenorows) {
            String oldAssignedBy = pr.getAssignedBy();
            if (oldAssignedBy == null || oldAssignedBy.length() < 2) {
                pr.setAssignedBy(bcurator);
                // check for these rows if the evidence field is set
                String evi = pr.getEvidenceID();
                if (evi == null || evi.length() < 3) {
                    pr.setEvidenceID("IEA");
                }
                String pub = pr.getPub();
                if (pub == null || pub.length() < 5) {
                    String diseaseid = pr.getDiseaseID();
                    pr.setPub(diseaseid);
                }
            }
        }
        table.refresh();
        dirty=true;
    }

    @FXML
    public void showSettings() {
        SettingsViewFactory.showSettings(this.settings);
    }

    @FXML
    public void newFile() {
        if (dirty) {
            boolean discard=PopUps.getBooleanFromUser("Discard unsaved changes?","Unsaved work on current annotation file","Discard unsaved work?");
            if (! discard) return;
        }
        clearFields();
        table.getItems().clear();
        this.currentPhenoteFileFullPath = null;
        this.currentPhenoteFileBaseName = null;
        this.lastSource.setValue("");
        PhenoRow row = new PhenoRow();
        table.getItems().add(row);
    }

    @FXML
    public void openByMIMnumber() {
        if (dirty) {
            boolean discard=PopUps.getBooleanFromUser("Discard unsaved changes?","Unsaved work on current annotation file","Discard unsaved work?");
            if (! discard) return;
        }
        String dirpath = settings.getDefaultDirectory();
        if (dirpath == null) {
            PopUps.showInfoMessage("Please set default Phenote directory\n in Settings menu",
                    "Error: Default directory not set");
            return;
        }
        String mimID = PopUps.getStringFromUser("Enter MIM ID to open",
                "Enter the 6 digit MIM id of the Phenote file to open",
                "MIM id");
        mimID = mimID.trim();
        Integer i = null;
        try {
            i = Integer.parseInt(mimID);
        } catch (NumberFormatException nfe) {
            PopUps.showException("Error getting MIM ID",
                    String.format("Malformed MIM ID entered: %s", mimID),
                    nfe.toString(), nfe);
        }
        if (mimID.length() != 6) {
            PopUps.showInfoMessage(String.format("MIMId needs to be 6 digits (you entered: %s", mimID),
                    "Error: Malformed MIM ID");
            return;
        }

        String basename = String.format("OMIM-%d.tab", i);
        File f = new File(dirpath + File.separator + basename);
        if (!f.exists()) {
            PopUps.showInfoMessage(String.format("Could not find file %s at \n%s", basename, f.getAbsoluteFile()),
                    "Error: Malformed MIM ID");
            return;
        }
        populateTable(f);

    }

    @FXML
    public void setDefaultPhenoteFileDirectory() {
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        File dir = PopUps.selectDirectory(stage, null, "Choose default Phenote file directory");
        this.settings.setDefaultDirectory(dir.getAbsolutePath());
        saveSettings();
    }


}
