package org.monarchinitiative.phenotefx.gui;

/*
 * #%L
 * PhenoteFX
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


import javafx.application.HostServices;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.monarchinitiative.hpotextmining.gui.controller.HpoTextMining;
import org.monarchinitiative.hpotextmining.gui.controller.Main;
import org.monarchinitiative.hpotextmining.gui.controller.OntologyTree;
import org.monarchinitiative.hpotextmining.core.miners.TermMiner;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoOnsetTermIds;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.RowTallyTool;
import org.monarchinitiative.phenotefx.exception.PhenoteFxException;
import org.monarchinitiative.phenotefx.gui.annotationcheck.AnnotationCheckFactory;
import org.monarchinitiative.phenotefx.gui.webviewerutil.HelpViewFactory;
import org.monarchinitiative.phenotefx.gui.logviewer.LogViewerFactory;
import org.monarchinitiative.phenotefx.gui.newitem.NewDiseaseEntryFactory;
import org.monarchinitiative.phenotefx.gui.webviewerutil.OnsetPopup;
import org.monarchinitiative.phenotefx.gui.progresspopup.ProgressPopup;
import org.monarchinitiative.phenotefx.gui.webviewerutil.SettingsPopup;
import org.monarchinitiative.phenotefx.gui.webviewerutil.WebViewerPopup;
import org.monarchinitiative.phenotefx.io.*;
import org.monarchinitiative.phenotefx.model.Frequency;
import org.monarchinitiative.phenotefx.model.HPOOnset;
import org.monarchinitiative.phenotefx.model.PhenoRow;
import org.monarchinitiative.phenotefx.model.Settings;
import org.monarchinitiative.phenotefx.service.Resources;
import org.monarchinitiative.phenotefx.validation.NotValidator;
import org.monarchinitiative.phenotefx.validation.SmallFileValidator;
import org.monarchinitiative.phenotefx.worker.TermLabelUpdater;
import org.monarchinitiative.fenominal.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Created by robinp on 5/22/17.
 * Main presenter for the HPO Phenote App.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.2.5 (2018-05-12)
 */
@Component
public class PhenoteController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhenoteController.class);


    private static final String HP_OBO_URL = "https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.obo";
    private static final String MEDGEN_URL = "ftp://ftp.ncbi.nlm.nih.gov/pub/medgen/MedGen_HPO_OMIM_Mapping.txt.gz";
    private static final String MEDGEN_BASENAME = "MedGen_HPO_OMIM_Mapping.txt.gz";
    private static final String EMPTY_STRING = "";
    private static final BooleanProperty validate = new SimpleBooleanProperty(false);

    @FXML
    private AnchorPane anchorpane;
    @FXML
    private TextField hpoNameTextField;
    @FXML
    private Label diseaseIDlabel;
    /* ------ MENU ---------- */
    @FXML
    private MenuItem newMenuItem;
    @FXML
    private MenuItem openFileMenuItem;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private MenuItem closeMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem saveAsMenuItem;
    @FXML
    private MenuItem openByMimMenuItem;
    @FXML
    private MenuItem updateDiseaseNameMenuItem;
    @FXML
    private ChoiceBox<String> ageOfOnsetChoiceBox;
    @FXML
    private RadioButton IEAbutton;
    @FXML
    private RadioButton PCSbutton;
    @FXML
    private RadioButton TASbutton;
    @FXML
    private TextField frequencyTextField;
    @FXML
    private TextField cohortSizeTextField;
    @FXML
    private ChoiceBox<String> frequencyChoiceBox;
    @FXML
    private TextField modifiertextField;
    @FXML
    private TextField descriptiontextField;
    /**
     * The publication (source) for the annotation (refered to as "pub" in the small files).
     */
    @FXML
    private TextField pubTextField;
    @FXML
    private CheckBox notBox;
    @FXML
    private CheckBox oneOfOneBox;
    @FXML
    private Label lastSourceLabel;
    @FXML
    private CheckBox lastSourceBox;

    @Autowired
    private Settings settings;

    @Autowired
    private ExecutorService executorService;

    private HostServices hostServices;

    private Map<String, String> hponame2idMap;

    private Map<String, String> hpoModifer2idMap;

    private Map<String, String> hpoSynonym2LabelMap;

    private HPOOnset hpoOnset;
    /**
     * Is there unsaved work?
     */
    private boolean dirty = false;
    /**
     * Ontology used by Text-mining widget. Instantiated at first click in {@link #fetchTextMining()}
     */
    private static Ontology ontology;

    private OntologyTree ontologyTree;
    /** This gets set to true once the Ontology tree has finished initiatializing. Before that
     * we can check to make sure the user does not try to open a disease before the Ontology is
     * done loading.
     */
    private boolean doneInitializingOntology=false;

    /**
     * A shared resource service class. To replace other resource objects such HpoOntology
     */
    private static Resources resources;

    private Frequency frequency;
    /**
     * Header of the current Phenote file.
     */
    private String header = null;
    /**
     * Base name of the current Phenote file
     */
    private String currentPhenoteFileBaseName = null;

    private String currentPhenoteFileFullPath = null;
    /**
     * The last source used, e.g., a PMID (use this to avoid having to re-enter the source)
     */
    private final StringProperty lastSource = new SimpleStringProperty("");
    /**
     * This is the table where the phenotype data will be shown.
     */
    @FXML
    private Label tableTitleLabel;
    @FXML
    private TableView<PhenoRow> table = null;
    @FXML
    private TableColumn<PhenoRow, String> phenotypeNameCol;
    @FXML
    private TableColumn<PhenoRow, String> ageOfOnsetNamecol;
    @FXML
    private TableColumn<PhenoRow, String> frequencyCol;
    @FXML
    private TableColumn<PhenoRow, String> sexCol;
    @FXML
    private TableColumn<PhenoRow, String> negationCol;
    @FXML
    private TableColumn<PhenoRow, String> modifierCol;
    @FXML
    private TableColumn<PhenoRow, String> descriptionCol;
    @FXML
    private TableColumn<PhenoRow, String> pubCol;
    @FXML
    private TableColumn<PhenoRow, String> evidencecol;
    @FXML
    private TableColumn<PhenoRow, String> biocurationCol;

    // should the app appear in the common disease module
    private BooleanProperty commonDiseaseModule;
    @FXML
    private StackPane ontologyTreeView;


    /**
     * This will hold list of annotations
     */
    private final ObservableList<PhenoRow> phenolist = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        boolean ready = checkReadiness();
        setDefaultHeader();
        if (!ready) {
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
                progress.addListener((obj, oldvalue, newvalue) -> updateProgress(newvalue.doubleValue(), 100) );
                initResources(progress);
                updateProgress(100, 100);
                return null;
            }
        };
        new Thread(task).start();

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.progressProperty().bind(task.progressProperty());
        progressIndicator.setMinHeight(70);
        progressIndicator.setMinWidth(70);
        progressIndicator.setMaxHeight(70);
        progressIndicator.setMaxWidth(70);
        ontologyTreeView.setMinWidth(250);
        Label initOntoLabel=new Label("initializing HPO browser");

        task.setOnRunning(event -> {
            ontologyTreeView.getChildren().addAll(progressIndicator,initOntoLabel);
            StackPane.setAlignment(progressIndicator, Pos.CENTER);
        });

        task.setOnSucceeded(event -> {
            ontologyTreeView.getChildren().clear();
            ontologyTreeView.getChildren().remove(initOntoLabel);
            setupAutocomplete();
            setupOntologyTreeView();
            doneInitializingOntology=true;
        });

        anchorpane.setPrefSize(1400, 1000);
        setUpTable();
        table.setItems(phenolist);
        // set up buttons
        exitMenuItem.setOnAction(e -> exitGui());
        openFileMenuItem.setOnAction(this::openPhenoteFile);
        closeMenuItem.setOnAction(this::closePhenoteFile);
        this.hpoNameTextField.setPromptText("Enter preferred label or synonym (will be automatically converted)");

        ToggleGroup evidenceGroup = new ToggleGroup();
        IEAbutton.setToggleGroup(evidenceGroup);
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
        this.modifiertextField.setPromptText("Autocomplete label of HPO modifier term");
        /* The following removes whitespace if the user pastes in a PMID */
        pubTextField.textProperty().addListener( // ChangeListener
                (observable, oldValue, newValue) -> {
                    String txt = pubTextField.getText();
                    txt = txt.replaceAll("\\s", "");
                    pubTextField.setText(txt);
                });

        this.lastSourceLabel.textProperty().bind(this.lastSource);
        setUpKeyAccelerators();
        tableTitleLabel.setText("");
        phenolist.addListener((ListChangeListener<PhenoRow>) c -> {
            dirty = true;
            //set table title
            if (!phenolist.isEmpty()) {
                String diseaseIdName = String.format("%s\t%s",
                        phenolist.get(0).getDiseaseID(), phenolist.get(0).getDiseaseName());
                tableTitleLabel.textProperty().set(diseaseIdName);
            }
        });
    }

    @FXML private void refreshTable( ActionEvent e ) {
        e.consume();
        table.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getColumns().forEach( (column) ->
        {
            //Minimal width = columnheader
            Text t = new Text( column.getText() );
            double max = t.getLayoutBounds().getWidth();
            for ( int i = 0; i < table.getItems().size(); i++ ) {
                if ( column.getCellData( i ) != null ) {
                    t = new Text( column.getCellData( i ).toString() );
                    double calcwidth = t.getLayoutBounds().getWidth();
                    if ( calcwidth > max ) {
                        max = calcwidth;
                    }
                }
            }
            column.setPrefWidth( max + 10.0d );
        } );
    }

    private void phenoRowDirtyListener(PhenoRow row) {
        row.frequencyProperty().addListener((r, o, n) -> dirty = true);
        row.biocurationProperty().addListener((r, o, n) -> dirty = true);
        row.descriptionProperty().addListener((r, o, n) -> dirty = true);
        row.evidenceProperty().addListener((r, o, n) -> dirty = true);
        row.modifierProperty().addListener((r, o, n) -> dirty = true);
        row.negationProperty().addListener((r, o, n) -> dirty = true);
        row.onsetIDProperty().addListener((r, o, n) -> dirty = true);
        row.publicationProperty().addListener((r, o, n) -> dirty = true);
        row.sexProperty().addListener((r, o, n) -> dirty = true);
    }

    /**
     * Add short cuts to the menu items. Note--adding accelerator="Shortcut+M" to the fxml is portable across
     * Mac and Windows and Linux.
     */
    private void setUpKeyAccelerators() {
        this.newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        this.openFileMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        this.openByMimMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.SHORTCUT_DOWN));
        this.saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        this.saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
        this.closeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
    }

    /**
     * When we create a new annotation file,
     * we need to set the Header line here.
     */
    private void setDefaultHeader() {
        this.header = SmallfileParser.getStandardHeaderLine();
    }

    /**
     * Called by the initialize method. Serves to set up the
     * Maps with HPO and Disease name information for the autocompletes.
     */
    private void initResources(DoubleProperty progress) {
        long start = System.currentTimeMillis();
        MedGenParser medGenParser = new MedGenParser();
        if (progress != null) {
            progress.setValue(25);
        }
        HPOParser hpoParser = new HPOParser();
        if (progress != null) {
            progress.setValue(75);
        }
        resources = new Resources(medGenParser, hpoParser);

        long end = System.currentTimeMillis();
        //multi threading does not seem to help. Concurrency probably does not work for IO operations.
        //https://stackoverflow.com/questions/902425/does-multithreading-make-sense-for-io-bound-operations
        LOGGER.info(String.format("time cost for parsing resources: %ds",  (end - start)/1000));
        start = end;
        ontology = resources.getHPO();
        hponame2idMap = resources.getHpoName2IDmap();
        hpoSynonym2LabelMap = resources.getHpoSynonym2PreferredLabelMap();
        hpoModifer2idMap = resources.getModifierMap();
        if (hpoModifer2idMap == null) {
            LOGGER.error("hpoModifer2idMap is NULL");
        }
        end = System.currentTimeMillis();
        LOGGER.info(String.format("time for parsing OMIM, ontology, synonysm, modifiers: %ds",  (end - start)/1000));
        LOGGER.trace("Done input HPO/MedGen");
    }

    /**
     * Checks if the HPO and medgen files have been downloaded already, and if
     * not shows an alert window.
     */
    private boolean checkReadiness() {
        StringBuffer sb = new StringBuffer();
        boolean ready = true;
        boolean hpoready = org.monarchinitiative.phenotefx.gui.Platform.checkHPOFileDownloaded();
        if (!hpoready) {
            sb.append("HPO File not found. ");
            ready = false;
        }
        boolean medgenready = org.monarchinitiative.phenotefx.gui.Platform.checkMedgenFileDownloaded();
        if (!medgenready) {
            sb.append("MedGen_HPO_OMIM_Mapping.txt.gz not found. ");
            ready = false;
        }
        if (!ready) {
            sb.append("You need to download the files before working with annotation data.");
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(sb.toString());
                    alert.setContentText("Download the files with the commands in the Setup menu! Then restart this app");
                    alert.showAndWait();

                    return null;
                }
            };
            task.run();
        }
        return ready;
    }

    /**
     * This is called if the user chooses Edit|update disease name
     * It is intended to update out-of-date disease labels that now are changed in OMIM.
     * It will change the name in all rows of the small file, and will also update the
     * disease name as shown in the GUI (also including the OMIM id).
     */
    @FXML
    private void updateDiseaseName() {
        String diseaseName = PopUps.getStringFromUser("Enter new disease name",
                "disease name", "Replace disease name (label) with new name" );
        diseaseName=diseaseName.trim();
        String diseaseID=null;
        for (PhenoRow row : this.table.getItems()){
            row.setDiseaseName(diseaseName);
            if (diseaseID==null) diseaseID=row.getDiseaseID();
        }
        table.refresh();
        String diseaseIdName = String.format("%s\t%s",diseaseID, diseaseName);
        tableTitleLabel.setText(diseaseIdName);
    }


    /**
     * Write the settings from the current session to file and exit.
     */
    @FXML
    private void exitGui() {
        boolean clean = savedBeforeExit();
        if (clean) {
            javafx.application.Platform.exit();
        } else {
        }

    }

    public boolean savedBeforeExit() {
        if (dirty && !phenolist.isEmpty()) {
            boolean discard = PopUps.getBooleanFromUser("Discard unsaved changes?", "Unsaved work on current annotation file", "Discard unsaved work?");
            if (discard) {
                dirty = true;
            } else {
                return false;
            }
        }
        saveSettings();
        return true;
    }

    /**
     * This method gets called when user chooses to close Gui. Content of
     * {@link Settings} bean is dumped
     * in XML format to platform-dependent default location.
     */
    private void saveSettings() {
        if (settings == null) {
            PopUps.showInfoMessage("Attempt to save settings but Settings object is null", "Error");
            return;
        }
        settings.saveToFile();
    }

    /**
     * Uses the {@link WidthAwareTextFields} class to set up autocompletion for the disease name and the HPO name
     */
    private void setupAutocomplete() {
        if (hpoSynonym2LabelMap != null) {
            WidthAwareTextFields.bindWidthAwareAutoCompletion(hpoNameTextField, hpoSynonym2LabelMap.keySet());
        }
        if (hpoModifer2idMap != null) {
            WidthAwareTextFields.bindWidthAwareAutoCompletion(modifiertextField, hpoModifer2idMap.keySet());
        }
    }

    /**
     * Open a main file ("small file") and populate the table with it.
     */
    private void openPhenoteFile(ActionEvent event) {
        if (dirty && !phenolist.isEmpty()) {
            boolean discard = PopUps.getBooleanFromUser("Discard unsaved changes?", "Unsaved work on current annotation file", "Discard unsaved work?");
            if (discard) {
                dirty = true;
            } else {
                return;
            }
        }
        clearFields();
        table.getItems().clear();
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File f = fileChooser.showOpenDialog(stage);
        if (f != null) {
            LOGGER.trace("Opening file " + f.getAbsolutePath());
            populateTable(f);
        }
        event.consume();
    }

    private void closePhenoteFile(ActionEvent event) {
        if (dirty && !phenolist.isEmpty()) {
            boolean discard = PopUps.getBooleanFromUser("Discard unsaved changes?", "Unsaved work on current annotation file", "Discard unsaved work?");
            if (discard) {
                dirty = true;
            } else {
                return;
            }
        }
        table.getItems().clear();
        tableTitleLabel.setText("");
        dirty = false;
        event.consume();
        this.lastSource.setValue("");
    }

    /**
     * Put rows into the table that represent the disease annotations from the file.
     *
     * @param f "small file" with HPO disease annotations.
     */
    private void populateTable(File f) {
        LOGGER.trace(String.format("About to populate the table from file %s", f.getAbsolutePath()));
        List<String> errors = new ArrayList<>();
        //setUpTable();
        this.currentPhenoteFileBaseName = f.getName();
        this.currentPhenoteFileFullPath = f.getAbsolutePath();
        try {
            SmallfileParser parser = new SmallfileParser(f, ontology);
            phenolist.addAll(parser.parse());
            //adding terms to phenolist will cause it to change to dirty, but in this case it is unnecessary
            // so reset it to false
            phenolist.forEach(this::phenoRowDirtyListener);
            dirty = false;
            LOGGER.trace(String.format("About to add %d lines to the table", phenolist.size()));
        } catch (PhenoteFxException e) {
            PopUps.showException("Parse error",
                    "Could not parse small file",
                    String.format("Could not parse file %s", f.getAbsolutePath()),
                    e);
            errors.add(e.getMessage());
            this.currentPhenoteFileBaseName = null; // couldnt open this file!
        }
        if (errors.size() > 0) {
            String s = String.join("\n", errors);
            ErrorDialog.display("Error", s);
        }
    }

    private void addFenominalDataToTable(File f) {
        LOGGER.trace(String.format("About to add fenominal file (%s) to the table", f.getAbsolutePath()));
        List<String> errors = new ArrayList<>();
        String fenominalFilePath = f.getAbsolutePath();
        try {
            SmallfileParser parser = new SmallfileParser(f, ontology);
            phenolist.addAll(parser.parse());
            phenolist.forEach(this::phenoRowDirtyListener);
            //adding new terms that need to be saved if they are ok
            dirty = true;
            LOGGER.trace(String.format("About to add %d lines to the table", phenolist.size()));
        } catch (PhenoteFxException e) {
            PopUps.showException("Parse error",
                    "Could not parse fenominal file",
                    String.format("Could not parse file %s", fenominalFilePath),
                    e);
            errors.add(e.getMessage());
        }
        if (errors.size() > 0) {
            String s = String.join("\n", errors);
            ErrorDialog.display("Error", s);
        }
    }


    /**
     * Set up the table and define the behavior of the columns
     */
    private void setUpTable() {
        table.setEditable(true);

        phenotypeNameCol.setCellValueFactory(new PropertyValueFactory<>("phenotypeName"));
        phenotypeNameCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PhenoRow, String> call(TableColumn<PhenoRow, String> p) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setTooltip(null);
                            setText(null);
                        } else {
                            Tooltip tooltip = new Tooltip();
                            PhenoRow myModel = getTableView().getItems().get(getTableRow().getIndex());
                            tooltip.setText(myModel.getPhenotypeID());
                            setTooltip(tooltip);
                            setText(item);
                        }
                    }
                };
            }
        });
        phenotypeNameCol.setEditable(false);
        phenotypeNameCol.setSortable(true);

        ageOfOnsetNamecol.setCellValueFactory(new PropertyValueFactory<>("onsetName"));
        ageOfOnsetNamecol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PhenoRow, String> call(TableColumn<PhenoRow, String> p) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setTooltip(null);
                            setText(null);
                        } else {
                            Tooltip tooltip = new Tooltip();
                            PhenoRow myModel = getTableView().getItems().get(getTableRow().getIndex());
                            tooltip.setText(myModel.getOnsetID());
                            setTooltip(tooltip);
                            setText(item);
                        }
                    }
                };
            }
        });
        ageOfOnsetNamecol.setEditable(false);

        //frequency is saved as HPO termid or numbers. if it is shown as a termid, it is displayed as the term name
        frequencyCol.setCellValueFactory(param -> {
            String frequencyId = param.getValue().getFrequency();
            Optional<String> frequencyName = frequency.getName(frequencyId);
            return new SimpleStringProperty(frequencyName.orElse(frequencyId));
        });
        frequencyCol.setEditable(false);

        sexCol.setCellValueFactory(new PropertyValueFactory<>("sex"));
        sexCol.setCellFactory(TextFieldTableCell.forTableColumn());
        sexCol.setOnEditCommit(event -> event.getTableView().getItems().get(event.getTablePosition().getRow()).setSex(event.getNewValue()));

        negationCol.setCellValueFactory(new PropertyValueFactory<>("negation"));
        negationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        negationCol.setOnEditCommit(event -> {
                    if (NotValidator.isValid(event.getNewValue())) {
                        event.getTableView().getItems().get(event.getTablePosition().getRow()).setNegation(event.getNewValue());
                    }
                    event.getTableView().refresh();
                }
        );

        modifierCol.setCellValueFactory(new PropertyValueFactory<>("modifier"));
        modifierCol.setCellFactory(TextFieldTableCell.forTableColumn());
        modifierCol.setEditable(true);

        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());


        pubCol.setCellValueFactory(new PropertyValueFactory<>("publication"));
        pubCol.setCellFactory(TextFieldTableCell.forTableColumn());


        evidencecol.setCellValueFactory(new PropertyValueFactory<>("evidence"));
        evidencecol.setCellFactory(TextFieldTableCell.forTableColumn());
        evidencecol.setEditable(true);

        biocurationCol.setCellValueFactory(new PropertyValueFactory<>("biocuration"));
        biocurationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        biocurationCol.setOnEditCommit(event ->
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setBiocuration(event.getNewValue())
        );

        // The following makes the table only show the defined columns (otherwise, an "extra" column is shown)
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setUpEvidenceContextMenu();
        setUpPublicationPopupDialog();
        setUpDescriptionPopupDialog();
        setUpSexContextMenu();
        setUpHpoContextMenu();
        setUpOnsetContextMenu();
        setUpNOTContextMenu();
        setUpFrequencyPopupDialog();
    }


    /**
     * Set up the popup of the evidence menu.
     */
    private void setUpEvidenceContextMenu() {
        //enable individual cells to be selected, instead of entire rows, call
        table.getSelectionModel().setCellSelectionEnabled(true);
        // The following sets up a context menu JUST for the evidence column.
        evidencecol.setCellFactory( //Callback
                (column) -> {
                    final TableCell<PhenoRow, String> cell = new TableCell<>();
                    cell.itemProperty().addListener(// ChangeListener
                            (obs, oldValue, newValue) -> {
                                if (newValue != null) {
                                    final ContextMenu cellMenu = new ContextMenu();
                                    MenuItem ieaMenuItem = new MenuItem("IEA");
                                    ieaMenuItem.setOnAction(e -> {
                                        PhenoRow item = cell.getTableRow().getItem();
                                        item.setEvidence("IEA");
                                        table.refresh();
                                    });
                                    MenuItem pcsMenuItem = new MenuItem("PCS");
                                    pcsMenuItem.setOnAction(e -> {
                                        PhenoRow item = cell.getTableRow().getItem();
                                        item.setEvidence("PCS");
                                        table.refresh();
                                    });
                                    MenuItem tasMenuItem = new MenuItem("TAS");
                                    tasMenuItem.setOnAction(e -> {
                                        //PhenoRow item = (PhenoRow) cell.getTableRow().getItem();
                                        //use the follow way to get row item to avoid bug. By Aaron Zhang
                                        PhenoRow item = cell.getTableView().getItems().get(cell.getIndex());
                                        item.setEvidence("TAS");
                                        table.refresh();

                                    });
                                    cellMenu.getItems().addAll(ieaMenuItem, pcsMenuItem, tasMenuItem);
                                    cell.setContextMenu(cellMenu);

                                } else {
                                    cell.setContextMenu(null);
                                }

                            });
                    cell.textProperty().bind(cell.itemProperty());
                    return cell;
                });
    }

    /**
     * Allow users to set the NOT (negation) field with a right click
     */
    private void setUpNOTContextMenu() {
        //enable individual cells to be selected, instead of entire rows, call
        table.getSelectionModel().setCellSelectionEnabled(true);
        // The following sets up a context menu JUST for the evidence column.
        negationCol.setCellFactory( // Callback
                (column) -> {
                    final TableCell<PhenoRow, String> cell = new TableCell<>();
                    cell.itemProperty().addListener(// ChangeListener
                            (observableValue, oldValue, newValue) -> {
                                final ContextMenu cellMenu = new ContextMenu();
                                final TableRow<?> row = cell.getTableRow();
                                final ContextMenu rowMenu;
                                if (row != null) {
                                    rowMenu = cell.getTableRow().getContextMenu();
                                    if (rowMenu != null) {
                                        cellMenu.getItems().addAll(rowMenu.getItems());
                                        cellMenu.getItems().add(new SeparatorMenuItem());
                                    } else {
                                        final ContextMenu tableMenu = cell.getTableView().getContextMenu();
                                        if (tableMenu != null) {
                                            cellMenu.getItems().addAll(tableMenu.getItems());
                                            cellMenu.getItems().add(new SeparatorMenuItem());
                                        }
                                    }
                                }
                                MenuItem notMenuItem = new MenuItem("NOT");
                                notMenuItem.setOnAction(e -> {
                                    PhenoRow item = cell.getTableRow().getItem();
                                    item.setNegation("NOT");
                                    table.refresh();
                                });
                                MenuItem clearMenuItem = new MenuItem("Clear");
                                clearMenuItem.setOnAction(e -> {
                                    //PhenoRow item = (PhenoRow) cell.getTableRow().getItem();
                                    //use the follow way to get row item to avoid bug. By Aaron Zhang
                                    PhenoRow item = cell.getTableView().getItems().get(cell.getIndex());
                                    item.setNegation(EMPTY_STRING);
                                    table.refresh();
                                });
                                cellMenu.getItems().addAll(notMenuItem, clearMenuItem);
                                cell.setContextMenu(cellMenu);

                            });
                    cell.textProperty().bind(cell.itemProperty());
                    return cell;
                });
    }


    /**
     * Set up the popup of the evidence menu.
     */
    private void setUpSexContextMenu() {
        //enable individual cells to be selected, instead of entire rows, call
        table.getSelectionModel().setCellSelectionEnabled(true);
        // The following sets up a context menu JUST for the evidence column.
        sexCol.setCellFactory( // Callback
                (column) -> {
                    final TableCell<PhenoRow, String> cell = new TableCell<>();
                    cell.itemProperty().addListener(// ChangeListener
                            (observableValue, oldValue, newValue) -> {
                                if (newValue != null) {
                                    final ContextMenu cellMenu = new ContextMenu();
                                    final TableRow<?> row = cell.getTableRow();
                                    final ContextMenu rowMenu;
                                    if (row != null) {
                                        rowMenu = cell.getTableRow().getContextMenu();
                                        if (rowMenu != null) {
                                            cellMenu.getItems().addAll(rowMenu.getItems());
                                            cellMenu.getItems().add(new SeparatorMenuItem());
                                        } else {
                                            final ContextMenu tableMenu = cell.getTableView().getContextMenu();
                                            if (tableMenu != null) {
                                                cellMenu.getItems().addAll(tableMenu.getItems());
                                                cellMenu.getItems().add(new SeparatorMenuItem());
                                            }
                                        }
                                    }
                                    MenuItem maleMenuItem = new MenuItem("MALE");
                                    maleMenuItem.setOnAction(e -> {
                                        //PhenoRow item = (PhenoRow) cell.getTableRow().getItem();
                                        //use the follow way to get row item to avoid bug. By Aaron Zhang
                                        PhenoRow item = cell.getTableView().getItems().get(cell.getIndex());
                                        item.setSex("MALE");
                                        table.refresh();
                                    });
                                    MenuItem femaleMenuItem = new MenuItem("FEMALE");
                                    femaleMenuItem.setOnAction(e -> {
                                        PhenoRow item = cell.getTableRow().getItem();
                                        item.setSex("FEMALE");
                                        table.refresh();
                                    });
                                    MenuItem clearMenuItem = new MenuItem("Clear");
                                    clearMenuItem.setOnAction(e -> {
                                        PhenoRow item = cell.getTableRow().getItem();
                                        item.setSex(EMPTY_STRING);
                                        table.refresh();
                                    });
                                    cellMenu.getItems().addAll(maleMenuItem, femaleMenuItem, clearMenuItem);
                                    cell.setContextMenu(cellMenu);
                                } else {
                                    cell.setContextMenu(null);
                                }
                            });
                    cell.textProperty().bind(cell.itemProperty());
                    return cell;
                });
    }


    /**
     * Set up the popup of the onset menu. If the users acitivates the menu, this updates the data in the
     * corresponding {@link PhenoRow} (annotation) object.
     */
    private void setUpOnsetContextMenu() {
        //enable individual cells to be selected, instead of entire rows, call
        table.getSelectionModel().setCellSelectionEnabled(true);
        // The following sets up a context menu JUST for the evidence column.
        ageOfOnsetNamecol.setCellFactory( // Callback
                (column) -> {
                    final TableCell<PhenoRow, String> cell = new TableCell<>();
                    cell.itemProperty().addListener(// ChangeListener
                            (obs, oldValue, newValue) -> {
                                if (newValue != null) {
                                final ContextMenu cellMenu = new ContextMenu();
                                final PhenoRow phenoRow = cell.getTableView().getItems().get(cell.getIndex());
                                MenuItem anteNatalOnsetItem = new MenuItem("Antenatal onset");
                                anteNatalOnsetItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(HpoOnsetTermIds.ANTENATAL_ONSET.getValue());
                                    phenoRow.setOnsetName("Antenatal onset");
                                    table.refresh();
                                });
                                MenuItem embryonalOnsetItem = new MenuItem("Embryonal onset");
                                embryonalOnsetItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(HpoOnsetTermIds.EMBRYONAL_ONSET.getValue());
                                    phenoRow.setOnsetName("Embryonal onset");
                                    table.refresh();
                                });
                                MenuItem fetalOnsetItem = new MenuItem("Fetal onset");
                                fetalOnsetItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(HpoOnsetTermIds.FETAL_ONSET.getValue());
                                    phenoRow.setOnsetName("Fetal onset");
                                    table.refresh();
                                });
                                MenuItem congenitalOnsetItem = new MenuItem("Congenital onset");
                                congenitalOnsetItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(HpoOnsetTermIds.CONGENITAL_ONSET.getValue());
                                    phenoRow.setOnsetName("Congenital onset");
                                    table.refresh();
                                });
                                MenuItem neonatalOnsetItem = new MenuItem("Neonatal onset");
                                neonatalOnsetItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(HpoOnsetTermIds.NEONATAL_ONSET.getValue());
                                    phenoRow.setOnsetName("Neonatal onset");
                                    table.refresh();
                                });
                                MenuItem infantileOnsetItem = new MenuItem("Infantile onset");
                                infantileOnsetItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(HpoOnsetTermIds.INFANTILE_ONSET.getValue());
                                    phenoRow.setOnsetName("Infantile onset");
                                    table.refresh();
                                });
                                MenuItem childhoodOnsetItem = new MenuItem("Childhood onset");
                                childhoodOnsetItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(HpoOnsetTermIds.CHILDHOOD_ONSET.getValue());
                                    phenoRow.setOnsetName("Childhood onset");
                                    table.refresh();
                                });
                                MenuItem juvenileOnsetItem = new MenuItem("Juvenile onset");
                                juvenileOnsetItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(HpoOnsetTermIds.JUVENILE_ONSET.getValue());
                                    phenoRow.setOnsetName("Juvenile onset");
                                    table.refresh();
                                });
                                MenuItem adultOnsetItem = new MenuItem("Adult onset");
                                adultOnsetItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(HpoOnsetTermIds.ADULT_ONSET.getValue());
                                    phenoRow.setOnsetName("Adult onset");
                                    table.refresh();
                                });
                                MenuItem youngAdultOnsetItem = new MenuItem("Young adult onset");
                                youngAdultOnsetItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(HpoOnsetTermIds.YOUNG_ADULT_ONSET.getValue());
                                    phenoRow.setOnsetName("Young adult onset");
                                    table.refresh();
                                });
                                MenuItem middleAgeOnsetItem = new MenuItem("Middle age onset");
                                middleAgeOnsetItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(HpoOnsetTermIds.MIDDLE_AGE_ONSET.getValue());
                                    phenoRow.setOnsetName("Middle age onset");
                                    table.refresh();
                                });
                                MenuItem lateOnsetItem = new MenuItem("Late onset");
                                lateOnsetItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(HpoOnsetTermIds.LATE_ONSET.getValue());
                                    phenoRow.setOnsetName("Late onset");
                                    table.refresh();
                                });
                                MenuItem clearMenuItem = new MenuItem("Clear");
                                clearMenuItem.setOnAction(e -> {
                                    phenoRow.setOnsetID(EMPTY_STRING);
                                    phenoRow.setOnsetName(EMPTY_STRING);
                                    table.refresh();
                                });
                                cellMenu.getItems().addAll(anteNatalOnsetItem,
                                        embryonalOnsetItem,
                                        fetalOnsetItem,
                                        congenitalOnsetItem,
                                        neonatalOnsetItem,
                                        infantileOnsetItem,
                                        childhoodOnsetItem,
                                        juvenileOnsetItem,
                                        adultOnsetItem,
                                        youngAdultOnsetItem,
                                        middleAgeOnsetItem,
                                        lateOnsetItem,
                                        clearMenuItem);
                                cell.setContextMenu(cellMenu);
//                            } else {
//                                cell.setContextMenu(null);
                            }
                            });
                    cell.textProperty().bind(cell.itemProperty());
                    return cell;
                });
    }

    private String getNewBiocurationEntry() {
        return String.format("%s[%s]", this.settings.getBioCuratorId(), getDate());
    }


    /**
     * Set up the popup of the evidence menu.
     */
    private void setUpHpoContextMenu() {
        //enable individual cells to be selected, instead of entire rows, call
        table.getSelectionModel().setCellSelectionEnabled(true);
        // The following sets up a context menu JUST for the evidence column.
        phenotypeNameCol.setCellFactory(// Callback
                (column) -> {
                    final TableCell<PhenoRow, String> cell = new TableCell<>();
                    cell.itemProperty().addListener(// ChangeListener
                            (observableValue, oldValue, newValue) -> {
                                if (newValue != null) {
                                    final ContextMenu cellMenu = new ContextMenu();
                                    MenuItem hpoUpdateMenuItem = new MenuItem("Update to current ID(not shown) and name");
                                    hpoUpdateMenuItem.setOnAction(e -> {
                                        //PhenoRow item = (PhenoRow) cell.getTableRow().getItem();
                                        //use a different way to get a row to avoid potential problems. By Aaron Zhang
                                        PhenoRow item = cell.getTableView().getItems().get(cell.getIndex());
                                        String id = item.getPhenotypeID();
                                        if (ontology == null) {
                                            LOGGER.error("Ontology null");
                                            return;
                                        }
                                        org.monarchinitiative.phenol.ontology.data.TermId tid = TermId.of(id);
                                        try {
                                            Term term = ontology.getTermMap().get(tid);
                                            String label = term.getName();
                                            item.setPhenotypeID(term.getId().getValue());
                                            item.setPhenotypeName(label);
                                            item.setNewBiocurationEntry(getNewBiocurationEntry());
                                        } catch (Exception exc) {
                                            exc.printStackTrace();
                                        }
                                        table.refresh();
                                    });

                                    MenuItem hpoIdMenuItem = new MenuItem("show HPO id of this term");
                                    hpoIdMenuItem.setOnAction(e -> {
                                        PhenoRow item = cell.getTableRow().getItem();
                                        String label = item.getPhenotypeName();
                                        String id = item.getPhenotypeID();
                                        if (ontology == null) {
                                            LOGGER.error("Ontology null");
                                            return;
                                        }
                                        org.monarchinitiative.phenol.ontology.data.TermId tid = TermId.of(id);
                                        try {
                                            String msg = String.format("%s [%s]", label, id);
                                            PopUps.showInfoMessage(msg, "Term Id");
                                        } catch (Exception exc) {
                                            exc.printStackTrace();
                                        }
                                        table.refresh();
                                    });
                                    cellMenu.getItems().addAll(hpoUpdateMenuItem, hpoIdMenuItem);
                                    cell.setContextMenu(cellMenu);
                                } else {
                                    cell.setContextMenu(null);
                                }
                            });
                    cell.textProperty().bind(cell.itemProperty());
                    return cell;
                });
    }

    /**
     * Allow the user to update the publication if they right-click on the publication field.
     */
    private void setUpPublicationPopupDialog() {
        // The following sets up a popup dialog JUST for the publication column.
        pubCol.setCellFactory(// Callback
                (column) -> {
                    final TableCell<PhenoRow, String> cell = new TableCell<>();
                    cell.itemProperty().addListener(// ChangeListener
                            (observableValue, oldValue, newValue) -> {
                                final ContextMenu cellMenu = new ContextMenu();
                                MenuItem pubDummyMenuItem = new MenuItem("Update publication");
                                PhenoRow phenoRow = cell.getTableRow().getItem();
                                if (phenoRow == null) {
                                    //happens at application start up--we can skip it
                                    return;
                                }
                                pubDummyMenuItem.setOnAction(e -> {
                                    TextInputDialog dialog = new TextInputDialog();
                                    dialog.setTitle("Input publication data");
                                    dialog.setHeaderText("Publication");
                                    dialog.setContentText("Please enter PMID/OMIM id:");
                                    Optional<String> opt = dialog.showAndWait();
                                    if (opt.isPresent()){
                                        String text = opt.get().replaceAll(" ", "");
                                        LOGGER.info("Got new publication: \"{}\"", text);
                                        table.getItems().get(cell.getIndex()).setPublication(text);
                                        table.getItems().get(cell.getIndex()).setNewBiocurationEntry(getNewBiocurationEntry());
                                        table.refresh();
                                    }
                                });
                                MenuItem latestPubSourceMenuItem = new MenuItem("Set to latest publication");
                                latestPubSourceMenuItem.setOnAction(e -> {
                                    String latest = this.lastSource.get();
                                    if (latest != null && latest.startsWith("PMID")) {
                                        table.getItems().get(cell.getIndex()).setPublication(latest);
                                        table.getItems().get(cell.getIndex()).setEvidence("PCS");
                                        table.getItems().get(cell.getIndex()).setNewBiocurationEntry(getNewBiocurationEntry());
                                        table.refresh();
                                    }
                                });
                                cellMenu.getItems().addAll(pubDummyMenuItem, latestPubSourceMenuItem);
                                cell.setContextMenu(cellMenu);
                            });
                    cell.textProperty().bind(cell.itemProperty());
                    return cell;
                });

    }


    /**
     * Allow the user to update the publication if they right-click on the publication field.
     */
    private void setUpDescriptionPopupDialog() {
        // The following sets up a popup dialog JUST for the Description column.
        descriptionCol.setCellFactory( // Callback
                (column) -> {
                    final TableCell<PhenoRow, String> cell = new TableCell<>();
                    cell.itemProperty().addListener(// ChangeListener
                            (observableValue, oldValue, newValue) -> {
                                final ContextMenu cellMenu = new ContextMenu();
                                final TableRow<PhenoRow> tableRow = cell.getTableRow();
                                final PhenoRow phenoRow = tableRow.getItem();
                                if (phenoRow == null) {
                                    return; // happens during initial population of table
                                }
                                MenuItem updateDescriptionMenuItem = new MenuItem("Update description");
                                updateDescriptionMenuItem.setOnAction(e -> {
                                    TextInputDialog dialog = new TextInputDialog();
                                    dialog.setTitle("Input description");
                                    String current = String.format("Current description: %s", phenoRow.getDescription());
                                    dialog.setHeaderText(current);
                                    dialog.setContentText("Description");
                                    Optional<String> opt = dialog.showAndWait();
                                    if (opt.isPresent()) {
                                        String text = opt.get();
                                        table.getItems().get(cell.getIndex()).setDescription(text);
                                        table.getItems().get(cell.getIndex()).setNewBiocurationEntry(getNewBiocurationEntry());
                                        table.refresh();
                                    }
                                });
                                MenuItem clearDescriptionMenuItem = new MenuItem("Clear");
                                clearDescriptionMenuItem.setOnAction(e -> {
                                    phenoRow.setDescription(EMPTY_STRING);
                                    table.refresh();
                                });
                                cellMenu.getItems().addAll(updateDescriptionMenuItem, clearDescriptionMenuItem);
                                cell.setContextMenu(cellMenu);
                            });
                    cell.textProperty().bind(cell.itemProperty());
                    return cell;
                });
    }


    /**
     * Allow the user to update the frequency if they right-click on the frequency field.
     */
    private void setUpFrequencyPopupDialog() {
        // The following sets up a popup dialog JUST for the publication column.
        frequencyCol.setCellFactory(// Callback
                (col) -> {
                    final TableCell<PhenoRow, String> cell = new TableCell<>();
                    cell.itemProperty().addListener( // ChangeListener
                            (observableValue, oldValue, newValue) -> {
                                final ContextMenu cellMenu = new ContextMenu();
                                final TableRow<PhenoRow> tableRow = cell.getTableRow();
                                final PhenoRow phenoRow = tableRow.getItem();
                                MenuItem updateFrequencyMenuItem = new MenuItem("Update frequency");
                                if (phenoRow == null) {
                                    return;
                                }
                                updateFrequencyMenuItem.setOnAction(e -> {
                                    LOGGER.info("phenol row: {}; index: {}", phenoRow, cell.getIndex());
                                    TextInputDialog dialog = new TextInputDialog();
                                    dialog.setTitle("Input frequency as m/m");
                                    dialog.setHeaderText("Frequency");
                                    String fr = phenoRow.getFrequency();
                                    String current = String.format("Current frequency: %s", fr != null
                                            && fr.length()>0 ? fr : "n/a");
                                    dialog.setContentText(current);
                                    Optional<String> opt = dialog.showAndWait();
                                    if (opt.isPresent()) {
                                        String text = opt.get();
                                        //A strange "bug" is that phenoRow seems to not sit in the row where the mouse is clicked
                                        //phenoRow.setFrequency(text);
                                        //phenoRow.setNewBiocurationEntry(getNewBiocurationEntry());
                                        //using cell.getIndex seem to be correct. Added by Aaron Zhang
                                        table.getItems().get(cell.getIndex()).setFrequency(text);
                                        table.getItems().get(cell.getIndex()).setNewBiocurationEntry(getNewBiocurationEntry());
                                        table.refresh();
                                    }
                                    e.consume();
                                });
                                MenuItem clearFrequencyMenuItem = new MenuItem("Clear");
                                clearFrequencyMenuItem.setOnAction(e -> {
                                    phenoRow.setFrequency(EMPTY_STRING);
                                    phenoRow.setNewBiocurationEntry(getNewBiocurationEntry());
                                    table.refresh();
                                });
                                cellMenu.getItems().addAll(updateFrequencyMenuItem, clearFrequencyMenuItem);
                                cell.setContextMenu(cellMenu);
                            });
                    cell.textProperty().bind(cell.itemProperty());
                    return cell;
                });
    }


    /**
     * This is called from the Edit menu and allows the user to import a local copy of
     * hp.obo (usually because the local copy is newer than the official release version of hp.obo).
     *
     * @param e event
     */
    @FXML
    private void importLocalHpObo(ActionEvent e) {
        e.consume();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import local hp.obo file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HPO OBO file (*.obo)", "*.obo");
        chooser.getExtensionFilters().add(extFilter);
        File f = chooser.showOpenDialog(null);
        if (f == null) {
            LOGGER.error("Unable to obtain path to local HPO OBO file");
            PopUps.showInfoMessage("Unable to obtain path to local HPO OBO file", "Error");
            return;
        }
        String hpoOboPath = f.getAbsolutePath();
        try {
            HPOParser parser = new HPOParser(hpoOboPath);
            hponame2idMap = parser.getHpoName2IDmap();
            hpoSynonym2LabelMap = parser.getHpoSynonym2PreferredLabelMap();
            setupAutocomplete();
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Unable to parse local HPO OBO file");
            PopUps.showException("Error", "Unable to parse local hp.obo file", ex.getMessage(), ex);
        }
    }


    /**
     * Get path to the .phenotefx directory, download the file, and if successful
     * set the path to the file in the settings.
     */
    public void downloadHPO(ActionEvent event) {
        ProgressPopup ppopup = new ProgressPopup("HPO download", "downloading hp.obo...");
        ProgressIndicator progressIndicator = ppopup.getProgressIndicator();
        String basename = "hp.obo";
        File dir = Platform.getPhenoteFXDir();
        Downloader downloadTask = new Downloader(dir.getAbsolutePath(), HP_OBO_URL, basename, progressIndicator);
        downloadTask.setOnSucceeded(e -> {
            String abspath = (new File(dir.getAbsolutePath() + File.separator + basename)).getAbsolutePath();
            LOGGER.trace("Setting hp.obo path to " + abspath);
            saveSettings();
            this.settings.setHpoFile(abspath);
            ppopup.close();
        });
        downloadTask.setOnFailed(e -> {
            LOGGER.error("Download of hp.obo failed");
            PopUps.showInfoMessage("Download of hp.obo failed", "Error");
            ppopup.close();
        });
        ppopup.startProgress(downloadTask);
        event.consume();
    }

    /**
     * Download the medgen file to the .phenotefx directory, and if successful
     * set the path to the file in the settings.table.getItems().add(row);
     */
    public void downloadMedGen() {
        ProgressPopup ppopup = new ProgressPopup("Medgen download", String.format("downloading %s...", MEDGEN_BASENAME));
        ProgressIndicator progressIndicator = ppopup.getProgressIndicator();
        File dir = Platform.getPhenoteFXDir();
        Downloader downloadTask = new Downloader(dir.getAbsolutePath(), MEDGEN_URL, MEDGEN_BASENAME, progressIndicator);
        downloadTask.setOnSucceeded(e -> {
            String abspath = (new File(dir.getAbsolutePath() + File.separator + MEDGEN_BASENAME)).getAbsolutePath();
            LOGGER.trace(String.format("Setting %s path to %s", MEDGEN_BASENAME, abspath));
            this.settings.setMedgenFile(abspath);
            saveSettings();
            ppopup.close();
        });
        downloadTask.setOnFailed(e -> {
            LOGGER.error(String.format("Download of %s failed", MEDGEN_BASENAME));
            PopUps.showInfoMessage(String.format("Download of %s failed", MEDGEN_BASENAME), "Error");
            ppopup.close();
        });
        ppopup.startProgress(downloadTask);
    }

    private boolean needsMoreTimeToInitialize() {
        if (! this.doneInitializingOntology) {
            PopUps.showInfoMessage("PhenoteFX needs more time to initialize","Warning");
            return true;
        } else {
            return false;
        }
    }

    @FXML
    private void updateAllOutdatedTermLabels(ActionEvent e) {
        System.out.println("Updating outdated labels");
        String smallfilepath = settings.getDefaultDirectory();
        if (ontology == null) {
            initResources(null);
        }
        TermLabelUpdater updater = new TermLabelUpdater(smallfilepath, ontology);
        updater.replaceOutOfDateLabels();
    }


    /**
     * This method adds one text-mined annotation as a row in the PhenoteFX table.
     *
     * @param hpoid     ID of newly added annotation
     * @param hpoLabel  term label of newly added annotation
     * @param pmid      PubMed id supporting annotation
     * @param isNegated if true, this is a NOT annotation.
     */
    private void addTextMinedAnnotation(String hpoid, String hpoLabel, String pmid, boolean isNegated, boolean oneOfOne) {
        if (needsMoreTimeToInitialize()) return;
        PhenoRow textMinedRow = new PhenoRow();
        textMinedRow.setPhenotypeName(hpoLabel);
        textMinedRow.setPhenotypeID(hpoid);

        if (pmid == null || pmid.length() == 0) {
            PopUps.showInfoMessage("Warning-attempting to update annotation without valid PMID. A default value (\"UNKNOWN\") is used", "PubMed Id malformed");
            //return;
            pmid = "UNKNOWN";
        }

        if (!pmid.startsWith("PMID"))
            pmid = String.format("PMID:%s", pmid);
        textMinedRow.setPublication(pmid);
        if (isNegated) {
            textMinedRow.setNegation("NOT");
        }
        if (oneOfOne) {
            textMinedRow.setFrequency("1/1");
        }
        textMinedRow.setEvidence("PCS");
        String biocuration = String.format("%s[%s]", this.settings.getBioCuratorId(), getDate());
        textMinedRow.setBiocuration(biocuration);
        /* If there is data in the table already, use it to fill in the disease ID and Name. */
        List<PhenoRow> phenorows = table.getItems();
        if (phenorows != null && phenorows.size() > 0) {
            PhenoRow firstrow = phenorows.get(0);
            textMinedRow.setDiseaseName(firstrow.getDiseaseName());
            textMinedRow.setDiseaseID(firstrow.getDiseaseID());
        }
        /* These annotations will always be PMIDs, so we use the code PCS */
        textMinedRow.setEvidence("PCS");
        // Now see if we have seen this annotation before!
        boolean textMinedItemNotCurrentlyInTable = true;
        for (int idx = 0; idx < table.getItems().size(); idx++) {
            PhenoRow currentTableRow = table.getItems().get(idx);
            if (currentTableRow.getPhenotypeID().equals(textMinedRow.getPhenotypeID()) &&
                currentTableRow.getPublication().equals(textMinedRow.getPublication())) {
                AnnotationCheckFactory factory = new AnnotationCheckFactory();
                PhenoRow candidateRow = factory.showDialog(currentTableRow, textMinedRow);
                if (factory.updateAnnotation()) {
                    table.getItems().set(idx, candidateRow);
                    //dirty = true;
                    textMinedItemNotCurrentlyInTable = false;
                }
            }
        }
        if (textMinedItemNotCurrentlyInTable) {// not a duplicate -- just add the new annotation
            table.getItems().add(textMinedRow);
            //dirty = true;
        }
    }

    /**
     * All entries in the table should have the same disease name, except for entries with rows from
     * different dates where OMIM might have used different names. In this case, an error message is displayed.
     * @return Unique disease name
     */
    private String getDiseaseName() {
        Map<String, Long> countForId = table
                .getItems()
                .stream()
                .map(PhenoRow::getDiseaseName)
                .collect(Collectors.groupingBy(String::valueOf, Collectors.counting()));
        if (countForId.size() > 1) {
            String label = String.join("; ", countForId.keySet());
            PopUps.showInfoMessage(String.format("Multiple disease names in file:\n %s", label),"Multiple disease names");
        }
        return countForId.keySet().stream().findAny().orElse("No disease name found!");
    }

    private String getDiseaseId() {
        Map<String, Long> countForId = table
                .getItems()
                .stream()
                .map(PhenoRow::getDiseaseID)
                .collect(Collectors.groupingBy(String::valueOf, Collectors.counting()));
        if (countForId.size() > 1) {
            String label = String.join("; ", countForId.keySet());
            PopUps.showInfoMessage(String.format("Multiple disease names in file:\n %s", label),"Multiple disease names");
        }
        return countForId.keySet().stream().findAny().orElse("No disease id found!");
    }



    public void addAnnotation() {
        PhenoRow row = new PhenoRow();
        // Disease ID (OMIM)
        String diseaseID = getDiseaseId();
        String diseaseName = getDiseaseName();
        row.setDiseaseID(diseaseID);
        row.setDiseaseName(diseaseName);
        // HPO Id
        String hpoId;
        String hpoSynonym = this.hpoNameTextField.getText().trim();
        if (!hpoSynonym.isEmpty()) {
            String hpoPreferredLabel = this.hpoSynonym2LabelMap.get(hpoSynonym);
            hpoId = this.hponame2idMap.get(hpoPreferredLabel);

            row.setPhenotypeID(hpoId);
            row.setPhenotypeName(hpoPreferredLabel);
        }

        String evidence = "?";
        if (IEAbutton.isSelected())
            evidence = "IEA";
        else if (PCSbutton.isSelected())
            evidence = "PCS";
        else if (TASbutton.isSelected())
            evidence = "TAS";
        row.setEvidence(evidence);
        // Age of onset
        String onsetID, onsetName;
        onsetName = ageOfOnsetChoiceBox.getValue();
        if (onsetName != null) {
            onsetID = hpoOnset.getID(onsetName);
            row.setOnsetID(onsetID);
            row.setOnsetName(onsetName);
        }
        String frequencyName;
        String freq = this.frequencyChoiceBox.getValue();
        if (freq != null) {
            frequencyName = freq;
            row.setFrequency(this.frequency.getID(frequencyName));
        } else {
            frequencyName = this.frequencyTextField.getText().trim();
            // for the following, the user has the cohort size and just enters the numerator in the
            // frequency text box
            if (! this.cohortSizeTextField.getText().isEmpty() && ! frequencyName.contains("/")) {
                frequencyName = frequencyName + "/" + this.cohortSizeTextField.getText().trim();
            }
            row.setFrequency(frequencyName);
        }
        if (this.notBox.isSelected()) {
            row.setNegation("NOT");
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
            row.setPublication(src);
            this.lastSource.setValue(src);
        } else if (useLastSource && this.lastSource.getValue().length() > 0) {
            row.setPublication(this.lastSource.getValue());
        } else if (diseaseID != null) { // this will be activated if the user does not indicate the source otherwise
            String lastPmid = this.lastSource.get();
            if (lastPmid == null) {
                lastPmid = "n/a";
            }
            String [] choices = {diseaseID, lastPmid };
            String choice = PopUps.getToggleChoiceFromUser(choices,
                    "Should we use the OMIM ID or the last-used PMID?",
                    "No citation found");
            if (choice==null) {
                return;
            } else if (choice.startsWith("PMID")) {
                row.setEvidence("PCS");
                row.setPublication(choice);
            } else {
                row.setEvidence("TAS");
                row.setPublication(diseaseID);
            }
        }

        String modifier = this.modifiertextField.getText();
        if (modifier != null && modifier.length() > 1 && this.hpoModifer2idMap.containsKey(modifier)) {
            row.setModifier(hpoModifer2idMap.get(modifier));
        }

        String bcurator = this.settings.getBioCuratorId();
        if (bcurator != null && !bcurator.equals("null")) {
            String biocuration = String.format("%s[%s]", bcurator, getDate());
            row.setBiocuration(biocuration);
        }

        table.getItems().add(row);
        clearFields();
        //dirty = true;
        phenoRowDirtyListener(row);
    }

    /**
     * Resets all of the fields after the user has entered a new annotation.
     * Do not clear cohortSize -- we want to keep it for multiple entries for curating
     * papers where the number is always the same
     */
    private void clearFields() {
        this.hpoNameTextField.clear();
        this.IEAbutton.setSelected(true);
        this.frequencyTextField.clear();
        this.notBox.setSelected(false);
        this.descriptiontextField.clear();
        this.pubTextField.clear();
        this.frequencyChoiceBox.setValue(null);
        this.ageOfOnsetChoiceBox.setValue(null);
        this.modifiertextField.clear();
    }


    private String getDate() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        return ft.format(dNow);
    }

    /**
     * Delete the marked row of the table.
     */
    @FXML
    private void deleteAnnotation() {
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        phenolist.removeAll(table.getSelectionModel().getSelectedItems());
       dirty = true;
    }

    @FXML
    public void showLog(ActionEvent e) {
        LogViewerFactory factory = new LogViewerFactory();
        factory.display();
        e.consume();
    }

    /**
     * Create PopUp window with text-mining widget allowing to perform the mining. Process results
     */
    @FXML
    public void fetchTextMining() {
        if (needsMoreTimeToInitialize()) return;
        boolean oneOfOne = oneOfOneBox.isSelected(); // is this an annotation for one patient in a case report study?
        // if true, then we set the frequency to 1/1
        //ExecutorService executorService = Executors.newSingleThreadExecutor();

        TermMiner fenominalMiner = (TermMiner)new FenominalTermMiner(ontology);
        try {
            HpoTextMining hpoTextMining = HpoTextMining.builder()
                    .withTermMiner(fenominalMiner)
                    .withOntology(ontology)
                    .withExecutorService(executorService)
                    .withPhenotypeTerms(new HashSet<>())
                    .build();

            // show the text mining analysis dialog in the new stage/window
            Stage secondary = new Stage();
            Stage primaryStage = (Stage) this.ageOfOnsetChoiceBox.getScene().getWindow();
            secondary.initOwner(primaryStage);
            secondary.setTitle("HPO text mining analysis");
            secondary.setScene(new Scene(hpoTextMining.getMainParent()));
            secondary.showAndWait();

            Set<Main.PhenotypeTerm> approvedTerms = hpoTextMining.getApprovedTerms();   // set of terms approved by the curator
            String source;
            if (lastSourceBox.isSelected()) {
                source = lastSource.get();
                lastSourceBox.setSelected(false);
            } else {
                source = pubTextField.getText();
                lastSource.setValue(source);
            }
            approvedTerms.forEach(term -> addTextMinedAnnotation(term.getTerm().getId().getValue(),
                    term.getTerm().getName(),
                    source,
                    !term.isPresent(),
                    oneOfOne));

            if (approvedTerms.size() > 0) dirty = true;
            secondary.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        oneOfOneBox.setSelected(false);
    }


    /**
     * Show the about message
     */
    public void aboutWindow(ActionEvent e) {
        String title = "PhenoteFX";
        String msg = "A tool for revising and creating\nHPO Annotation files for rare disease.";
        PopUps.alertDialog(title, msg);
        e.consume();
    }

    /**
     * @param e event triggered by show help command.
     */
    @FXML
    public void showHelpWindow(ActionEvent e) {
        LOGGER.trace("Show help window");
        HelpViewFactory.openHelpDialog();
        e.consume();
    }

    /**
     * Check the contents of the table rows and make sure the format is valid before we start to save the file.
     *
     * @return true if the phenorows are all valid.
     */
    private boolean checkFileValidity() {
        List<PhenoRow> phenorows = table.getItems();
        SmallFileValidator validator = new SmallFileValidator(phenorows);
        if (validator.isValid()) {
            return true;
        } else {
            PopUps.showInfoMessage(validator.errorMessage(), "Please correct error in annotation data");
            return false;
        }
    }


    private void savePhenoteFileAt(File file) {
        if (!checkFileValidity()) return;
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
            dirty = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Save the modified file at the original location, showing a file chooser so the user can confirm
     */
    @FXML
    private void savePhenoteFile(ActionEvent e) {
        if (!checkFileValidity()) return;
        if (this.currentPhenoteFileFullPath == null) {
            saveAsPhenoteFile();
            return;
        }
        boolean doWrite = PopUps.getBooleanFromUser("Overwrite original file?",
                String.format("Save to %s", this.currentPhenoteFileFullPath), "Save file?");
        if (doWrite) {
            File f = new File(this.currentPhenoteFileFullPath);
            savePhenoteFileAt(f);
            dirty = false;
        }
        e.consume();
    }

    @FXML
    private void saveAndClosePhenoteFile(ActionEvent e) {
        if (!checkFileValidity()) return;
        if (this.currentPhenoteFileFullPath == null) {
            saveAsPhenoteFile();
            return;
        }
        boolean doWrite = PopUps.getBooleanFromUser("Overwrite original file?",
                String.format("Save to %s", this.currentPhenoteFileFullPath), "Save file?");
        if (doWrite) {
            File f = new File(this.currentPhenoteFileFullPath);
            savePhenoteFileAt(f);
            dirty = false;
        }
        this.closePhenoteFile(e);
    }

    /**
     * Save the modified file at a location chosen by user
     */
    public void saveAsPhenoteFile() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        String defaultdir = settings.getDefaultDirectory();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TAB/TSV files (*.tab)", "*.tab");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName(this.currentPhenoteFileBaseName);
        fileChooser.setInitialDirectory(new File(defaultdir));
        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);
        savePhenoteFileAt(file);
        this.currentPhenoteFileFullPath = file.getAbsolutePath();
        dirty = false;
    }


    /**
     * Runs after user clicks Settings/Set biocurator MenuItem and asks user to provide the ID.
     */
    @FXML
    void setBiocuratorMenuItemClicked(ActionEvent event) {
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

    @FXML
    void addFenominalFile(ActionEvent e) {
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File f = fileChooser.showOpenDialog(stage);
        if (f != null) {
            LOGGER.trace("Opening file " + f.getAbsolutePath());
            addFenominalDataToTable(f);
        }
        e.consume();
    }


    @FXML
    public void showSettings() {
        LOGGER.info("Showing settings");
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        WebViewerPopup webViewerPopup = new SettingsPopup(this.settings, stage);
        webViewerPopup.popup();
    }

    @FXML
    public void showOnset() {
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        WebViewerPopup webViewerPopup = new OnsetPopup(stage);
        webViewerPopup.popup();
    }

    @FXML
    public void newFile(ActionEvent event) {
        if (needsMoreTimeToInitialize()) return;
        if (dirty) {
            boolean discard = PopUps.getBooleanFromUser("Discard unsaved changes?", "Unsaved work on current annotation file", "Discard unsaved work?");
            if (discard) {
                dirty = false;
            } else {
                return;
            }
        }
        clearFields();
        table.getItems().clear();
        this.currentPhenoteFileFullPath = null;
        this.currentPhenoteFileBaseName = null;
        this.lastSource.setValue(null);

        NewDiseaseEntryFactory factory = new NewDiseaseEntryFactory(this.settings.getBioCuratorId(), getDate());
        Optional<PhenoRow> opt = factory.showDialog();
        if (opt.isPresent()) {
            PhenoRow row = opt.get();
            LOGGER.info("Got new disease entry {}", row);
            String diseaseId = row.getDiseaseID();
            if (diseaseId.contains(":")) {
                int i = diseaseId.indexOf(":");
                String prefix = diseaseId.substring(0, i);// part before ":"
                String number = diseaseId.substring(i + 1);// part after ":"
                this.currentPhenoteFileBaseName = String.format("%s-%s.tab", prefix, number);
            }
            table.getItems().add(row);
        }
        event.consume();
    }

    @FXML
    public void openByMIMnumber() {
        if (needsMoreTimeToInitialize()) return;
        if (dirty && !phenolist.isEmpty()) {
            boolean discard = PopUps.getBooleanFromUser("Discard unsaved changes?", "Unsaved work on current annotation file", "Discard unsaved work?");
            if (discard) {
                dirty = true;
            } else {
                return;
            }
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
        if (mimID == null) { // user canceled action
            return;
        }
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
        clearFields();
        table.getItems().clear();
        populateTable(f);

    }

    @FXML
    public void setDefaultPhenoteFileDirectory() {
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        File dir = PopUps.selectDirectory(stage, null, "Choose default Phenote file directory");
        this.settings.setDefaultDirectory(dir.getAbsolutePath());
        saveSettings();
    }

    @FXML
    private void findPercentage(ActionEvent e) {
        e.consume();
        PercentageFinder pfinder = new PercentageFinder();
    }

    /**
     * Call this to ingest a spreadsheet with phenotype findings with individuals in rows
     * and phenotypes in columns. Simple version for now
     * @param e an action event
     */
    @FXML
    private void tallyPhenotypeSpreadsheet(ActionEvent e) {
        e.consume();
        SpreadsheetTallyTool tool = new SpreadsheetTallyTool();
        tool.calculateTally();
    }

    /**
     * Call this to ingest a spreadsheet with phenotype findings with individuals in rows
     * and phenotypes in columns. Simple version for now
     * @param e an action event
     */
    @FXML
    private void tallyPhenotypeRow(ActionEvent e) {
        e.consume();
        String cp = PopUps.getCopyPasteTallyRow();
        RowTallyTool tool = new RowTallyTool(cp);
        tool.showTable();
    }

    private void addPhenotypeTerm(Main.PhenotypeTerm phenotypeTerm) {
        hpoNameTextField.setText(phenotypeTerm.getTerm().getName());
        notBox.setSelected(!phenotypeTerm.isPresent());
    }

    private void setupOntologyTreeView() {
        Consumer<Main.PhenotypeTerm> addHook = (this::addPhenotypeTerm);
        this.ontologyTree = new OntologyTree(ontology, addHook);
        FXMLLoader ontologyTreeLoader = new FXMLLoader(OntologyTree.class.getResource("OntologyTree.fxml"));
        ontologyTreeLoader.setControllerFactory(clazz -> this.ontologyTree);
        try {
            ontologyTreeView.getChildren().add(ontologyTreeLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the system browser to the HPO Page for the current disease.
     */
    @FXML
    private void openHpoBrowser() {
        String disease=currentPhenoteFileBaseName; // this is something like OMIM-162200.tab
        if (disease.endsWith(".tab")) {
            disease=disease.replace(".tab","");
        }
        disease=disease.replace("-",":");
        String url=String.format("https://hpo.jax.org/app/browse/disease/%s",disease );
        Hyperlink hyper = new Hyperlink(url);
        if (hostServices == null) {
            LOGGER.error("Could not open HPO Webpage because hostServices not initialized");
            return;
        }
        hostServices.showDocument(hyper.getText());
    }
}
