package org.monarchinitiative.phenotefx.gui;

import javafx.application.HostServices;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenotefx.RowTallyTool;
import org.monarchinitiative.phenotefx.exception.PhenoteFxException;
import org.monarchinitiative.phenotefx.gui.logviewer.LogViewerFactory;
import org.monarchinitiative.phenotefx.gui.webviewerutil.*;
import org.monarchinitiative.phenotefx.gui.widget.*;
import org.monarchinitiative.phenotefx.gui.progresspopup.ProgressPopup;
import org.monarchinitiative.phenotefx.gui.tablecells.DescriptionTableCell;
import org.monarchinitiative.phenotefx.gui.tablecells.EvidenceTableCell;
import org.monarchinitiative.phenotefx.gui.tablecells.FrequencyTableCell;
import org.monarchinitiative.phenotefx.gui.tablecells.HpoTableCell;
import org.monarchinitiative.phenotefx.gui.tablecells.NegationTableCell;
import org.monarchinitiative.phenotefx.gui.tablecells.OnsetTableCell;
import org.monarchinitiative.phenotefx.gui.tablecells.PublicationTableCell;
import org.monarchinitiative.phenotefx.gui.tablecells.SexTableCell;
import org.monarchinitiative.phenotefx.io.*;
import org.monarchinitiative.phenotefx.model.*;
import org.monarchinitiative.phenotefx.smallfile.SmallFileMerger;
import org.monarchinitiative.phenotefx.validation.SmallFileValidator;
import org.monarchinitiative.phenotefx.worker.HpoaValidityChecker;
import org.monarchinitiative.phenotefx.worker.TermLabelUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by robinp on 5/22/17.
 * Main controller for the HPO Phenote App.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.8.8 (2022-01-31)
 */
@Component
public class PhenoteController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhenoteController.class);
    private static final String HP_JSON_URL = "https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.json";

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
    private CheckBox automaticPmidUpdateBox;
    @FXML
    private Label lastSourceLabel;
    @FXML
    private CheckBox lastSourceBox;


    private Settings settings;

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
     * Ontology used by Text-mining widget.
     */
    private static Ontology ontology;

    /** This gets set to true once the Ontology tree has finished initiatializing. Before that
     * we can check to make sure the user does not try to open a disease before the Ontology is
     * done loading.
     */
    private boolean doneInitializingOntology=false;

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
    private TableView<PhenoRow> table;
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

    private PhenoteModel model;

    private Map<SimpleTerm, Integer> termCountMap = null;
    private int cohortCount = 0;

    /**
     * This will hold list of annotations
     */
    private final ObservableList<PhenoRow> phenolist = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        this.settings = Settings.fromDefaultPath();
        this.model = new PhenoteModel();
        this.model.setBiocuratorId(settings.getBioCuratorId());
        termCountMap = new HashMap<>();
        cohortCount = 0;
        boolean ready = checkReadiness();
        LOGGER.info("Phenocontroller -- ready? {}", ready);
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
        task.setOnSucceeded(event -> {
            this.doneInitializingOntology = true;
        });

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.progressProperty().bind(task.progressProperty());
        progressIndicator.setMinHeight(70);
        progressIndicator.setMinWidth(70);
        progressIndicator.setMaxHeight(70);
        progressIndicator.setMaxWidth(70);
        anchorpane.setPrefSize(1400, 1000);
        setUpTable();
        SortedList<PhenoRow> sortedData = new SortedList<>(phenolist);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        // Set the initial sort column to Phenotype Name (A-Z)
        phenotypeNameCol.setSortType(TableColumn.SortType.ASCENDING);
        table.getSortOrder().add(phenotypeNameCol);
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
        // this is often too broad, and we generally do not want to see all of it
        descriptionCol.setPrefWidth(50);
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
     * Add shortcuts to the menu items. Note--adding accelerator="Shortcut+M" to the fxml is portable across
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
 * This method is intended to add new rows to the existing HPOA files
 */
    public void importHpoa(ActionEvent ae) throws PhenoteFxException{
        ae.consume();
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        List<PhenoRow> additionalRows = getAdditionalHpoaFile();
        SmallFileMerger merger = new SmallFileMerger(table.getItems(), additionalRows);
        if (merger.hasError()) {
            String html = merger.getErrorHtml();
            WebViewerPopup popup = new PlainPopup(html, stage );
            popup.popup();
            return;
        }
        List<PhenoRow> novelAdditionalRows = merger.getNovelAdditionalRows();
        phenolist.addAll(novelAdditionalRows);
        markDuplicates();
    }

       private List<PhenoRow>  getAdditionalHpoaFile() throws PhenoteFxException {
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        File file = PopUps.selectFileToOpen(stage, new File("."), "Choose pyphetools HPO file");
        if (file == null  || !file.isFile()) {
            PopUps.showErrorMessage("Could not get pyphetools HPOA file");
            LOGGER.warn("Could not get pyphetools HPOA file");
            return List.of();
        }
        SmallfileParser parser = new SmallfileParser(file, ontology);
        return parser.parseList();

    }

    public void checkHpoaValidity(ActionEvent actionEvent) {
        String smallfilepath = settings.getAnnotationFileDirectory();
        if (ontology == null) {
            initResources(null);
        }
        HpoaValidityChecker checker = new HpoaValidityChecker(smallfilepath,ontology);
        checker.printErros();
    }

    /** Mark duplicates in color so the user can merge */
    private void markDuplicates() {
        Map<HpoIdAndPmidPair, Integer> diseasePmidMap = new HashMap<>();
        for (var prow : table.getItems()) {
            HpoIdAndPmidPair pair = prow.getDiseaseIdAndPmidPair();
            diseasePmidMap.putIfAbsent(pair, 0);
            int count = 1 + diseasePmidMap.get(pair);
            diseasePmidMap.put(pair, count);
        }
        for (var prow : table.getItems()) {
            HpoIdAndPmidPair pair = prow.getDiseaseIdAndPmidPair();
            int count = diseasePmidMap.get(pair);
            if (count > 1) {
                prow.setDuplicate(true);
            } else {
                prow.setDuplicate(false);
            }
        }
        table.setRowFactory(tableView -> new TableRow<>() {
            @Override
            protected void updateItem(PhenoRow prow, boolean empty) {
                super.updateItem(prow, empty);
                if (empty) {
                    setStyle("");
                } else if (prow.isDuplicate()) {
                    setStyle("-fx-background-color:aqua;");
                } else {
                    setStyle("-fx-background-color:white;");
                }
            }
        });
        table.refresh();
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
        LOGGER.info("initResources");
        HPOParser hpoParser = new HPOParser();
        LOGGER.error("initResources hpoParser");
        cohortCount = 0;
        LOGGER.info("Done HPOParser CTOR");
        if (progress != null) {
            progress.setValue(75);
        }

        long end = System.currentTimeMillis();
        //multi threading does not seem to help. Concurrency probably does not work for IO operations.
        //https://stackoverflow.com/questions/902425/does-multithreading-make-sense-for-io-bound-operations
        LOGGER.info(String.format("time cost for parsing resources: %ds",  (end - start)/1000));
        start = end;
        ontology = hpoParser.getHpoOntology();
        hponame2idMap = hpoParser.getHpoName2IDmap();
        hpoSynonym2LabelMap = hpoParser.getHpoSynonym2PreferredLabelMap();
        hpoModifer2idMap = hpoParser.getModifierMap();

        if (hpoModifer2idMap == null) {
            LOGGER.error("hpoModifer2idMap is NULL");
        } else {
            setupAutocomplete();
        }
        end = System.currentTimeMillis();
        LOGGER.info(String.format("Done input HPO: time for parsing OMIM, ontology, synonysm, modifiers: %ds",  (end - start)/1000));
    }

    /**
     * Checks if the HPO file has been downloaded already, and if
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
        if (!ready) {
            sb.append("You need to download the hp.json file before working with annotation data.");
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
        LOGGER.info("On exit: curator={}, hpo={}, dir={}",
            settings.getBioCuratorId(), settings.getHpoFile(), settings.getAnnotationFileDirectory());
        settings.saveToFile();
        boolean clean = savedBeforeExit();
        if (clean) {
            javafx.application.Platform.exit();
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
        if (settings == null) {
            PopUps.showInfoMessage("Attempt to save settings but Settings object is null", "Error");
        } else {
            settings.saveToFile();
        }
        return true;
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
        this.automaticPmidUpdateBox.setSelected(false);
        termCountMap = new HashMap<>();
        cohortCount = 0;
        table.getItems().clear();
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File f = fileChooser.showOpenDialog(stage);
        if (f != null) {
            LOGGER.trace("Opening file {}", f.getAbsolutePath());
            populateTable(f);
            initializeDiseaseIdAndLabel();
        }
        this.lastSource.setValue("");
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
        phenolist.clear();
        tableTitleLabel.setText("");
        dirty = false;
        event.consume();
        this.lastSource.setValue("");
        this.cohortSizeTextField.clear();
    }

    /**
     * Put rows into the table that represent the disease annotations from the file.
     *
     * @param f "small file" with HPO disease annotations.
     */
    private void populateTable(File f) {
        LOGGER.trace(String.format("About to populate the table from file %s", f.getAbsolutePath()));
        List<String> errors = new ArrayList<>();
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
            this.currentPhenoteFileBaseName = null; // couldn't open this file!
        }
        if (!errors.isEmpty()) {
            String s = String.join("\n", errors);
            ErrorDialog.display("Error", s);
        }
    }


    /**
     * Set up the table and define the behavior of the columns
     */
    private void setUpTable() {
        table.setEditable(true);

        phenotypeNameCol.setCellValueFactory(cellData -> cellData.getValue().phenotypeNameProperty()); // adjust to match exact getter name
        phenotypeNameCol.setCellFactory(column -> new HpoTableCell(new HpoTableCell.ControllerBridge() {
            @Override
            public Ontology getOntology() { return ontology; } // references your controller's field
            @Override
            public String getNewBiocurationEntry() { return PhenoteController.this.getNewBiocurationEntry(); }
            @Override
            public void showInfoMessage(String message, String title) { PopUps.showInfoMessage(message, title); }
        }));

        phenotypeNameCol.setEditable(false);
        phenotypeNameCol.setSortable(true);

        ageOfOnsetNamecol.setCellValueFactory(cellData -> cellData.getValue().onsetNameProperty()); // Adjust name if different
        ageOfOnsetNamecol.setCellFactory(column -> new OnsetTableCell());
        ageOfOnsetNamecol.setEditable(false);

        //frequency is saved as HPO termid or numbers. if it is shown as a termid, it is displayed as the term name

        frequencyCol.setCellFactory(column -> new FrequencyTableCell(new FrequencyTableCell.ControllerBridge() {
            @Override
            public String getCurrentPercentage() { return model.getCurrentPercentage(); }
            @Override
            public void setCurrentPercentage(String pct) { model.setCurrentPercentage(pct); }
            @Override
            public String getNewBiocurationEntry() { return PhenoteController.this.getNewBiocurationEntry(); } // Maps cleanly now
            @Override
            public boolean isAutomaticPmidUpdateSelected() { return automaticPmidUpdateBox.isSelected(); }
            @Override
            public String getLastSource() { return lastSource.get(); }
            @Override
            public void removePhenoItems(java.util.List<PhenoRow> items) { phenolist.removeAll(items); }
            @Override
            public void markDuplicates() { PhenoteController.this.markDuplicates(); }
            @Override
            public void setDirty(boolean isDirty) { dirty = isDirty; }
        }));

        frequencyCol.setCellValueFactory(param -> {
            String frequencyId = param.getValue().getFrequency();
            Optional<String> frequencyName = frequency.getName(frequencyId);
            return new SimpleStringProperty(frequencyName.orElse(frequencyId));
        });
        frequencyCol.setEditable(false);


        sexCol.setCellValueFactory(cellData -> cellData.getValue().sexProperty());
        sexCol.setCellFactory(column -> new SexTableCell());
        sexCol.setEditable(false);
        
        negationCol.setCellValueFactory(cellData -> cellData.getValue().negationProperty());
        negationCol.setCellFactory(column -> new NegationTableCell());
        negationCol.setEditable(false);
       
        modifierCol.setCellValueFactory(new PropertyValueFactory<>("modifier"));
        modifierCol.setCellFactory(TextFieldTableCell.forTableColumn());
        modifierCol.setEditable(true);

        descriptionCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty()); // adjust to match exact getter name
        descriptionCol.setCellFactory(column -> new DescriptionTableCell(() -> getNewBiocurationEntry()));
        descriptionCol.setEditable(false);

        pubCol.setCellValueFactory(new PropertyValueFactory<>("publication"));
        pubCol.setCellFactory(TextFieldTableCell.forTableColumn());
       
        evidencecol.setCellValueFactory(cellData -> cellData.getValue().evidenceProperty()); 
        evidencecol.setCellFactory(column -> new EvidenceTableCell());
        evidencecol.setEditable(false);

        biocurationCol.setCellValueFactory(new PropertyValueFactory<>("biocuration"));
        biocurationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        biocurationCol.setOnEditCommit(event ->
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setBiocuration(event.getNewValue())
        );
        pubCol.setCellValueFactory(cellData -> cellData.getValue().publicationProperty()); // adjust to match exact getter name
        pubCol.setCellFactory(column -> new PublicationTableCell(new PublicationTableCell.ControllerBridge() {
            @Override
            public String getLastSource() { return lastSource.get(); }
            @Override
            public void setLastSource(String source) { lastSource.setValue(source); }
            @Override
            public String getDiseaseId() { return model.getDiseaseId(); }
            @Override
            public String getNewBiocurationEntry() { return PhenoteController.this.getNewBiocurationEntry(); }
        }));
        pubCol.setEditable(false);

        // The following makes the table only show the defined columns (otherwise, an "extra" column is shown)
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN); // do not show "extra column"
        table.getSelectionModel().setCellSelectionEnabled(true);

        
      
    }



    private String getNewBiocurationEntry() {
        String biocurator = this.model.getBiocuratorId();
        if (biocurator == null) {
            PopUps.showErrorMessage("No biocurator id found");
        }
        return String.format("%s[%s]", biocurator, getDate());
    }


    /**
     * This is called from the Edit menu and allows the user to import a local copy of
     * hp.obo (usually because the local copy is newer than the official release version of hp.obo).
     *
     * @param e event
     */
    @FXML
    private void importLocalHpJson(ActionEvent e) {
        e.consume();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import local hp.json file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HPO JSON file (hp.json)", "*.json");
        chooser.getExtensionFilters().add(extFilter);
        File f = chooser.showOpenDialog(null);
        if (f == null) {
            LOGGER.error("Unable to obtain path to local HPO JSON file");
            PopUps.showInfoMessage("Unable to obtain path to local HPO JSON file", "Error");
            return;
        }
        String hpoJsonPath = f.getAbsolutePath();
        try {
            HPOParser hpoParser = new HPOParser(hpoJsonPath);
            ontology = hpoParser.getHpoOntology();
            hponame2idMap = hpoParser.getHpoName2IDmap();
            hpoSynonym2LabelMap = hpoParser.getHpoSynonym2PreferredLabelMap();
            hpoModifer2idMap = hpoParser.getModifierMap();
            if (hpoModifer2idMap == null) {
                LOGGER.error("hpoModifer2idMap is NULL");
            }
            setupAutocomplete();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.error("Unable to parse local HPO OBO file");
            PopUps.showException("Error", "Unable to parse local hp.obo file", ex.getMessage(), ex);
        }
    }


    /**
     * Get path to the .phenotefx directory, download the file, and if successful
     * set the path to the file in the settings.
     */
    public void downloadHPO(ActionEvent event) {
        ProgressPopup ppopup = new ProgressPopup("HPO download", "downloading hp.json...");
        ProgressIndicator progressIndicator = ppopup.getProgressIndicator();
        String basename = "hp.json";
        File dir = Platform.getPhenoteFXDir();
        LOGGER.info("Going to download {}", HP_JSON_URL);
        Downloader downloadTask = new Downloader(dir.getAbsolutePath(), HP_JSON_URL, basename, progressIndicator);
        downloadTask.setOnSucceeded(e -> {
            String abspath = (new File(dir.getAbsolutePath() + File.separator + basename)).getAbsolutePath();
            LOGGER.trace("Setting hp.json path to {}", abspath);
            this.settings.setHpoFile(abspath);
            ppopup.close();
        });
        downloadTask.setOnFailed(e -> {
            LOGGER.error("Download of hp.json failed");
            PopUps.showInfoMessage("Download of hp.json failed", "Error");
            ppopup.close();
        });
        ppopup.startProgress(downloadTask);
        event.consume();
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
        String smallfilepath = settings.getAnnotationFileDirectory();
        if (ontology == null) {
            initResources(null);
        }
        TermLabelUpdater updater = new TermLabelUpdater(smallfilepath, ontology);
        updater.replaceOutOfDateLabels();
    }


    /**
     * All entries in the table should have the same disease name, except for entries with rows from
     * different dates where OMIM might have used different names. In this case, an error message is displayed.
     */
    private void initializeDiseaseIdAndLabel() {
        Set<String> diseaseIdSet = phenolist.stream()
                .map(PhenoRow::getDiseaseID)
                .collect(Collectors.toSet());
        Set<String> diseaseNameSet = phenolist.stream()
                .map(PhenoRow::getDiseaseName)
                .collect(Collectors.toSet());
        if (diseaseIdSet.size() > 1) {
            String label = String.join("; ", diseaseIdSet);
            PopUps.showInfoMessage(String.format("Multiple disease IDs in file:\n %s", label),"Multiple disease names");
        }
        String diseaseId =  diseaseIdSet.stream().findAny().orElse("No disease id found!");
        this.model.setDiseaseId(diseaseId);

        if (diseaseNameSet.size() > 1) {
            String label = String.join("; ", diseaseNameSet);
            PopUps.showInfoMessage(String.format("Multiple disease names in file:\n %s", label),"Multiple disease names");
        }
        String diseaseName =  diseaseNameSet.stream().findAny().orElse("No disease name found!");
        this.model.setDiseaseLabel(diseaseName);
    }



    public void addAnnotation() {
        PhenoRow row = new PhenoRow();
        // Disease ID (OMIM)
        String diseaseID = model.getDiseaseId();
        String diseaseName = model.getDiseaseLabel();
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

        String desc = this.descriptiontextField.getText();
        if (desc != null && desc.length() > 2) {
            row.setDescription(desc);
        }

        boolean useLastSource = lastSource.get() != null && lastSource.get().startsWith("PMID");
        String src = this.pubTextField.getText();
        if (src != null && src.length() > 2) {
            row.setPublication(src);
            this.lastSource.setValue(src);
            if (src.startsWith("PMID")) {
                row.setEvidence("PCS");
            }
        } else if (useLastSource && !this.lastSource.getValue().isEmpty()) {
            row.setPublication(this.lastSource.getValue());
            row.setEvidence("PCS");
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
        String bcurator = model.getBiocuratorId();
        if (bcurator == null) {
            PopUps.showErrorMessage( "Could not get curation id");
        }
        if (bcurator != null && !bcurator.equals("null")) {
            String biocuration = String.format("%s[%s]", bcurator, getDate());
            row.setBiocuration(biocuration);
        }

        table.getItems().add(row);
        clearFields();
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
        boolean deleteIt = PopUps.getBooleanFromUser("Really delete marked annotation?","Confirm deletion", "delete?");
        if (! deleteIt) {
            return;
        }
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        phenolist.removeAll(table.getSelectionModel().getSelectedItems());
        markDuplicates();
       dirty = true;
    }

    @FXML
    public void showLog(ActionEvent e) {
        LogViewerFactory factory = new LogViewerFactory();
        factory.display();
        e.consume();
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
        HelpViewFactory.display();
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
            LOGGER.error(e.getMessage());
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
        String defaultdir = settings.getAnnotationFileDirectory();
        if (defaultdir == null) {
            // should never happen
            LOGGER.error("Could not retrieve default directory for saving small file");
            PopUps.showErrorMessage("Could not retrieve default directory for saving small file");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        String initialFileName = null;
        // get default name if possible
        String diseaseId = this.model.getDiseaseId();
        if (diseaseId != null) {
            String id = diseaseId;
            id = id .replace(":", "-");
            initialFileName =  id + ".tab" ;
        }
        LOGGER.info("Saving file to {}", defaultdir);
        LOGGER.info("currentPhenoteFileBaseName {}", currentPhenoteFileBaseName);
        LOGGER.info("initialFileName {}", initialFileName);
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TAB/TSV files (*.tab)", "*.tab");
        fileChooser.getExtensionFilters().add(extFilter);
        if (this.currentPhenoteFileBaseName != null) {
            fileChooser.setInitialFileName(this.currentPhenoteFileBaseName);
        } else if (initialFileName != null) {
            fileChooser.setInitialFileName(initialFileName);
        }
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
                "e.g. ORCID:0000-0000-1234-5678", "Enter your biocurator ORCID ID:");
        if (biocurator != null) {
            this.settings.setBioCuratorId(biocurator);
            this.model.setBiocuratorId(biocurator);
            PopUps.showInfoMessage(String.format("Biocurator ID set to \n\"%s\"",
                    biocurator), "Success");
        } else {
            PopUps.showInfoMessage("Biocurator ID not set.",
                    "Information");
        }
        event.consume();

    }




    @FXML
    public void showSettings() {
        LOGGER.info("Showing settings");
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        WebViewerPopup webViewerPopup = new SettingsPopup(this.settings, stage);
        webViewerPopup.popup();
    }
    @FXML
    public void showHpoVersion() {
        if (ontology == null) {
            LOGGER.error("Cannot show HPO version becase HPO ontologys object is null");
            return;
        }
        String version = ontology.version().orElse("could not extract version");
        PopUps.showInfoMessage( version, "HPO Version");
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
        this.automaticPmidUpdateBox.setSelected(false);
        table.getItems().clear();
        this.currentPhenoteFileFullPath = null;
        this.currentPhenoteFileBaseName = null;
        this.lastSource.setValue(null);
        String curator= model.getBiocuratorId();
        if (curator == null) {
            PopUps.showErrorMessage( "Could not get biocurator ID for new file");
            return;
        }
        Optional<DiseaseIdAndLabelPair> diseasIdAndLabelOpt = NewDiseaseEntryFactory.getDiseaseIdAndLabel();
        if (diseasIdAndLabelOpt.isEmpty()) {
            PopUps.showInfoMessage("Error", "Could not retrieve new disease data");
            return;
        } else {
            System.out.println(diseasIdAndLabelOpt.get().diseaseId());
        }
        DiseaseIdAndLabelPair pair = diseasIdAndLabelOpt.get();
        this.model.setDiseaseId(pair.diseaseId());
        this.model.setDiseaseLabel(pair.diseaseLabel());
        String diseaseIdName = String.format("%s\t%s",pair.diseaseId(), pair.diseaseLabel());
        tableTitleLabel.setText(diseaseIdName);
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
        String dirpath = settings.getAnnotationFileDirectory();
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
        this.automaticPmidUpdateBox.setSelected(false);
        table.getItems().clear();
        populateTable(f);
        initializeDiseaseIdAndLabel();
    }

    @FXML
    public void setDefaultPhenoteFileDirectory() {
        Stage stage = (Stage) this.anchorpane.getScene().getWindow();
        File dir = PopUps.selectDirectory(stage, null, "Choose default Phenote file directory");
        this.settings.setDefaultDirectory(dir.getAbsolutePath());
    }

    @FXML
    private void findPercentage(ActionEvent e) {
        e.consume();
        String guess = PercentageFinder.show();
        this.model.setCurrentPercentage(guess);
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

    /**
     * Add one more patient to n/m cohort
     */
    @FXML
    private  void nextFromCohort(ActionEvent e) {
        e.consume();

        System.err.println("No longer implemented");
        

        
    }

    public void finishCohort(ActionEvent actionEvent) {
        if (termCountMap == null) {
            PopUps.showInfoMessage("Error", "Empty termCountMap");
            return;
        }
        if (termCountMap.isEmpty()) {
            PopUps.showInfoMessage("Error", "No terms entered");
            return;
        }
        for (Map.Entry<SimpleTerm, Integer> entry : termCountMap.entrySet()) {
            SimpleTerm term = entry.getKey();
            int count = entry.getValue();
            PhenoRow textMinedRow = new PhenoRow();
            String hpoLabel = term.label();
            String hpoid = term.tid().getValue();
            textMinedRow.setPhenotypeName(hpoLabel);
            textMinedRow.setPhenotypeID(hpoid);
            String pmid = "UNKNOWN";
            if (pubTextField.getText().trim().startsWith("PMID")) {
                pmid = lastSource.get();
            } else if (lastSource.get().startsWith("PMID")) {
                pmid = lastSource.get();
            }
            textMinedRow.setPublication(pmid);
            textMinedRow.setEvidence("PCS");
            String curation = this.model.getBiocuratorId();
            if (curation == null) {
                PopUps.showErrorMessage( "Could not get biocurator. Stop curation and fix");
                return;
            }
            String freq = String.format("%d/%d", count, cohortCount);
            textMinedRow.setFrequency(freq);
            String biocuration = String.format("%s[%s]", curation, getDate());
            textMinedRow.setBiocuration(biocuration);
            /* If there is data in the table already, use it to fill in the disease ID and Name. */
            List<PhenoRow> phenorows = table.getItems();
            if (phenorows != null && !phenorows.isEmpty()) {
                PhenoRow firstrow = phenorows.get(0);
                textMinedRow.setDiseaseName(firstrow.getDiseaseName());
                textMinedRow.setDiseaseID(firstrow.getDiseaseID());
            }
            /* These annotations will always be PMIDs, so we use the code PCS */
            textMinedRow.setEvidence("PCS");
            table.getItems().add(textMinedRow);
            dirty = true;
        }
        // reset
        termCountMap = new HashMap<>();
        cohortCount = 0;
        cohortSizeTextField.setText("");
    }





}
