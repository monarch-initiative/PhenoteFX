package org.monarchinitiative.phenotefx.gui.riskfactorpopup;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2019 Peter Robinson
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

import base.OntoTerm;
import base.PointValueEstimate;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.*;
import ontology_term.*;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.gui.PopUps;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.gui.WidthAwareTextFields;
import org.monarchinitiative.phenotefx.model.PhenoRow;
import org.monarchinitiative.phenotefx.service.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RiskFactorPresenter implements Initializable {

    private static Logger logger = LoggerFactory.getLogger(RiskFactorPresenter.class);

    private Resources resources;

    private String curatorId;

    private List<Riskfactor> currentRiskFactors = new ArrayList<>();

    private Riskfactor beingEdited = new Riskfactor.Builder().build();

    private boolean isUpdated;

    @FXML
    private ComboBox<Riskfactor.RiskFactorType> riskFactorCombo;

    @FXML
    private TextField riskFactorIdTextField;

    @FXML
    private ComboBox<TimeAwareEffectSize.EffectSizeType> effectSizeTypeComboBox;

    @FXML
    private TextField sizeField;

    @FXML
    private ComboBox<UncertaintyType> effectSizeUncertaintyType;

    @FXML
    private TextField uncertain_left_textfield;

    @FXML
    private TextField uncertain_right_textfield;

    @FXML
    private ComboBox<TimeAwareEffectSize.TrendType> trendTypeComboBox;

    @FXML
    private TextField years_to_onset_textfield;

    @FXML
    private TextField years_to_plateau_textfield;

    @FXML
    private ComboBox<Evidence.EvidenceType> evidenceTypeComboBox;

    @FXML
    private TextField evidenceIdTextField;

    @FXML
    private TableView<RiskFactorRow> riskFactorsTable;

    @FXML
    private TableColumn<RiskFactorRow, String> riskFactorTypeColumn;

    @FXML
    private TableColumn<RiskFactorRow, String> riskFactorIdColumn;

    @FXML
    private TableColumn<RiskFactorRow, String> effectSizeTypeColumn;

    @FXML
    private TableColumn<RiskFactorRow, String> effectSizeColumn;

    @FXML
    private TableColumn<RiskFactorRow, String> trendTypeColumn;

    @FXML
    private TableColumn<RiskFactorRow, String> onsetPlateauColumn;

    @FXML
    private TableColumn<RiskFactorRow, String> evidenceColumn;

    @FXML
    private TableColumn<RiskFactorRow, String> curationMetaColumn;

    //This is a pointer to different maps, depending on the value of riskFactorCombo
    private Map<String, String> riskFactorMap = new HashMap<>();
    private AutoCompletionBinding autoCompletionBinding;

    private Consumer<Signal> confirm;

    private ObservableList<RiskFactorRow> riskFactorRows = FXCollections.observableArrayList();


    public void setResource(Resources injected) {
        resources = injected;
    }

    public void setCuratorId(String curatorId) {
        this.curatorId = curatorId;
    }

    public void setCurrentRiskFactors(Collection<Riskfactor> currentRiskFactors) {

        if (currentRiskFactors != null){
            this.currentRiskFactors.addAll(currentRiskFactors);
        }
        refresh();
    }

    public void setSignal(Consumer<Signal> signal) {
        this.confirm = signal;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        riskFactorCombo.getItems().addAll(Riskfactor.RiskFactorType.values());
        riskFactorCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if( observable != null && newValue != null) {
                riskFactorIdTextField.clear();
                if (autoCompletionBinding != null) {
                    autoCompletionBinding.dispose();
                }
                bindName2IdMap(newValue);
                autoCompletionBinding =
                        WidthAwareTextFields.bindWidthAwareAutoCompletion(riskFactorIdTextField,
                        riskFactorMap.keySet());
                autoCompletionBinding.setVisibleRowCount(10);
            }
        });
        effectSizeTypeComboBox.getItems().addAll(TimeAwareEffectSize.EffectSizeType.values());
        effectSizeUncertaintyType.getItems().addAll(UncertaintyType.values());
        effectSizeUncertaintyType.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (observable != null && newValue != null) {
                if (newValue == UncertaintyType.STDEV){
                    uncertain_left_textfield.setPromptText("standard deviation");
                    uncertain_right_textfield.setDisable(true);
                } else if (newValue == UncertaintyType.CI95){
                    uncertain_right_textfield.setDisable(false);
                    uncertain_left_textfield.setPromptText("2.5% Confidence Interval");
                    uncertain_right_textfield.setPromptText("97.5% Confidence Interval");
                } else{
                    //do nothing
                }
            }
        });
        trendTypeComboBox.getItems().addAll(TimeAwareEffectSize.TrendType.values());
        evidenceTypeComboBox.getItems().addAll(Evidence.EvidenceType.values());
        evidenceTypeComboBox.getSelectionModel().select(Evidence.EvidenceType.PCS);
        initRiskFactorTable();
    }

    private void refresh(){
        riskFactorRows.clear();
        //Add current risk factors into the RiskFactor table, one record a row
        for (Riskfactor riskfactor : currentRiskFactors) { // one riskfactor can occupy multiple rows if they have more then one effectsize annotation
            for (TimeAwareEffectSize effectSize : riskfactor.getEffectSizes()) {
                RiskFactorRow newRow = new RiskFactorRow(riskfactor.getRiskType(),
                        riskfactor.getRiskId().getLabel(),
                        effectSize);
                riskFactorRows.add(newRow);
            }
        }

    }

    private void bindName2IdMap(Riskfactor.RiskFactorType riskType) {

        switch (riskType) {
            case SEX:
                riskFactorMap.clear();
                riskFactorMap.putAll(BiologySex.values().stream()
                        .collect(Collectors.toMap(OntoTerm::getLabel, OntoTerm::getId)));
                break;
            case AGE:
                riskFactorMap.clear();
                riskFactorMap.putAll(LifeStage.values.stream()
                        .collect(Collectors.toMap(OntoTerm::getLabel, OntoTerm::getId)));
                break;
            case ETHNICITY:
                riskFactorMap.clear();
                riskFactorMap.putAll(Ethnicity.values().stream()
                        .collect(Collectors.toMap(OntoTerm::getLabel, OntoTerm::getId)));
                break;
            case LIFESTYLE:
                riskFactorMap.clear();
                riskFactorMap.putAll(LifeStyle.values().stream()
                        .collect(Collectors.toMap(OntoTerm::getLabel, OntoTerm::getId)));
                break;
            case ENVIRONMENT: //bind to environmental exposure terms
                riskFactorMap.clear();
                riskFactorMap.putAll(resources.getEctoName2Id());
                break;
            case DISEASE: //bind to mondo terms
                riskFactorMap.clear();
                riskFactorMap.putAll(resources.getMondoDiseaseName2IdMap());
                break;
            case PHENOTYPE: //bind to hpo terms
                riskFactorMap.clear();
                riskFactorMap.putAll(resources.getHpoSynonym2PreferredLabelMap());
                break;
            case FAMILYHISTORY:
                riskFactorMap.clear();
                riskFactorMap.putAll(FamilyHistory.values().stream()
                        .collect(Collectors.toMap(OntoTerm::getLabel, OntoTerm::getId)));
                break;
            case PRS:
                riskFactorMap.clear();
                riskFactorMap.putAll(PolygenicRiskScore.values().stream()
                        .collect(Collectors.toMap(OntoTerm::getLabel, OntoTerm::getId)));
                break;
            default:
                //do nothing
                return;
        }
    }

    private void initRiskFactorTable() {

        riskFactorsTable.setItems(riskFactorRows);

        riskFactorTypeColumn = new TableColumn<>("risk type");
        riskFactorIdColumn = new TableColumn<>("risk term");
        effectSizeTypeColumn = new TableColumn<>("effectSizeType");
        effectSizeColumn = new TableColumn<>("size");

        trendTypeColumn = new TableColumn<>("trend type");
        onsetPlateauColumn = new TableColumn<>("onsets and plateau");

        evidenceColumn = new TableColumn<>("evidence");
        curationMetaColumn = new TableColumn<>("curation meta");

        riskFactorTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRiskFactorType().toString()));
        riskFactorIdColumn.setCellValueFactory(new PropertyValueFactory<>("riskFactorId"));
        effectSizeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("effectSizeType"));
        effectSizeColumn.setCellValueFactory(new PropertyValueFactory<>("effectSize"));
        trendTypeColumn.setCellValueFactory(new PropertyValueFactory<>("trendType"));
        onsetPlateauColumn.setCellValueFactory(new PropertyValueFactory<>("onsetAndPlateau"));
        evidenceColumn.setCellValueFactory(new PropertyValueFactory<>("evidence"));
        curationMetaColumn.setCellValueFactory(new PropertyValueFactory<>("curationMeta"));

        riskFactorsTable.getColumns().addAll(riskFactorTypeColumn, riskFactorIdColumn, effectSizeTypeColumn, effectSizeColumn, trendTypeColumn, onsetPlateauColumn, evidenceColumn, curationMetaColumn);

    }

    @FXML
    void modifierComboClicked(ActionEvent event) {

    }

    @FXML
    void riskFactorClicked(ActionEvent event) {

    }

    @FXML
    void timeUnitComboClicked(ActionEvent event) {

    }

    @FXML
    void clearClicked(ActionEvent event) {
        event.consume();
        riskFactorCombo.getSelectionModel().clearSelection();
        riskFactorIdTextField.clear();
        effectSizeTypeComboBox.getSelectionModel().clearSelection();
        sizeField.clear();
        effectSizeUncertaintyType.getSelectionModel().clearSelection();
        uncertain_left_textfield.clear();
        uncertain_right_textfield.clear();
        trendTypeComboBox.getSelectionModel().clearSelection();
        years_to_onset_textfield.clear();
        years_to_plateau_textfield.clear();
    }

    @FXML
    void addClicked(ActionEvent event) {

        //QC completeness
        boolean hasError = qcBeforeAdding();
        if (hasError) {
            return;
        }

        Riskfactor.Builder riskfactorBuilder = new Riskfactor.Builder();

        //Get riskFactorType
        Riskfactor.RiskFactorType riskFactorType = riskFactorCombo.getSelectionModel().getSelectedItem();

        //Get riskFactorId
        String riskFactorLabel = riskFactorIdTextField.getText().trim();
        String riskFactorId = riskFactorMap.get(riskFactorLabel);
        OntoTerm riskFactorTerm = getRiskFactor(riskFactorId);

        //Get EffectSizeType
        TimeAwareEffectSize.EffectSizeType effectSizeType = effectSizeTypeComboBox.getSelectionModel().getSelectedItem();

        //Get EffectSize
        PointValueEstimate.Builder effectsizeBuilder = new PointValueEstimate.Builder();
        double effectSize;
        try {
            effectSize = Double.parseDouble(sizeField.getText().trim());
            effectsizeBuilder.mean(effectSize);
        } catch (Exception e){
            PopUps.showInfoMessage("invalid number exception", "ERROR");
            return;
        }

        UncertaintyType uncertaintyType = effectSizeUncertaintyType.getSelectionModel().getSelectedItem();
        double stdev;
        double ci_left;
        double ci_right;
        try {
            if (uncertaintyType == UncertaintyType.STDEV){
                stdev = Double.parseDouble(uncertain_left_textfield.getText().trim());
                effectsizeBuilder.stdev(stdev);
            } else if (uncertaintyType == UncertaintyType.CI95){
                ci_left = Double.parseDouble(uncertain_left_textfield.getText().trim());
                ci_right = Double.parseDouble(uncertain_right_textfield.getText().trim());
                effectsizeBuilder.ci95(ci_left, ci_right);
            }
        } catch (Exception e){
            PopUps.showInfoMessage("invalid numbers for effect size", "ERROR");
            return;
        }
        PointValueEstimate effectsize = effectsizeBuilder.build();

        TimeAwareEffectSize.Builder timeAwareEffectSizeBuilder = new TimeAwareEffectSize.Builder();
        TimeAwareEffectSize.TrendType trendType = trendTypeComboBox.getSelectionModel().getSelectedItem();
        double years_to_onset;
        double years_to_plateau;
        try {
            if (trendType == TimeAwareEffectSize.TrendType.FLAT){
                timeAwareEffectSizeBuilder.effectSizeType(effectSizeType)
                        .effectSize(effectsize)
                        .trend(TimeAwareEffectSize.TrendType.FLAT);
            } else {
                years_to_onset = Double.parseDouble(years_to_onset_textfield.getText().trim());
                years_to_plateau = Double.parseDouble(years_to_plateau_textfield.getText().trim());
                timeAwareEffectSizeBuilder.effectSizeType(effectSizeType)
                        .effectSize(effectsize)
                        .trend(trendType)
                        .yearsToOnset(years_to_onset)
                        .yearsToPlateau(years_to_plateau);
            }
        } catch (Exception e){
            PopUps.showInfoMessage("invalid numbers for trend", "ERROR");
            return;
        }
        //TODO: add evidence fields
        Evidence evidence = new Evidence.Builder()
                .evidenceType(evidenceTypeComboBox.getSelectionModel().getSelectedItem())
                .evidenceId(evidenceIdTextField.getText())
                .build();

        CurationMeta curationMeta = new CurationMeta.Builder()
                .curator(curatorId)
                .timestamp(LocalDate.now())
                .build();

        timeAwareEffectSizeBuilder
                .evidence(evidence)
                .curationMeta(curationMeta);

        TimeAwareEffectSize timeAwareEffectSize = timeAwareEffectSizeBuilder.build();

        Riskfactor riskfactor = riskfactorBuilder.setRiskType(riskFactorType)
                .setRiskId(riskFactorTerm)
                .addEffectSize(timeAwareEffectSize)
                .build();

        currentRiskFactors.add(riskfactor);
        refresh();

        clearClicked(event);

    }

    private boolean qcBeforeAdding(){

        boolean error = false;
        if (riskFactorCombo.getSelectionModel().isEmpty()){
            error = true;
            PopUps.showInfoMessage("RiskFactorType not specified", "ERROR");
            return error;
        }

        if (riskFactorIdTextField.getText().trim().isEmpty()){
            error = true;
            PopUps.showInfoMessage("RiskFactor term is not specified", "ERROR");
            return error;
        }

        if (effectSizeTypeComboBox.getSelectionModel().isEmpty()){
            error = true;
            PopUps.showInfoMessage("EffectSizeType not specified", "ERROR");
            return error;
        }

        if (sizeField.getText().trim().isEmpty()){
            error = true;
            PopUps.showInfoMessage("Effect size not specified", "ERROR");
            return error;
        }

        boolean hasUncertaintyNumbers = (!uncertain_left_textfield.getText().trim().isEmpty()) ||
                (!uncertain_right_textfield.getText().trim().isEmpty());
        if (hasUncertaintyNumbers && effectSizeUncertaintyType.getSelectionModel().isEmpty()){
            error = true;
            PopUps.showInfoMessage("Effect size uncertainty type not specified", "ERROR");
            return error;
        }

        boolean hasTimeCourseNumbers = !years_to_onset_textfield.getText().trim().isEmpty() ||
                !years_to_plateau_textfield.getText().trim().isEmpty();
        if (hasTimeCourseNumbers && trendTypeComboBox.getSelectionModel().isEmpty()){
            error = true;
            PopUps.showInfoMessage("Timecourse TrendType not specified", "ERROR");
            return error;
        }

        if (trendTypeComboBox.getSelectionModel().isEmpty()){
            error = true;
            PopUps.showInfoMessage("At least one type should be specified", "ERROR");
            return error;
        }

        if (evidenceIdTextField.getText().trim().isEmpty() || evidenceTypeComboBox.getSelectionModel().isEmpty()) {
            error = true;
            PopUps.showInfoMessage("Evidence type or id not specified", "ERROR");
        }
        return error;
    }

    private OntoTerm getRiskFactor(String id){
        Riskfactor.RiskFactorType riskFactorType = riskFactorCombo.getSelectionModel().getSelectedItem();
        switch (riskFactorType) {
            case SEX:
                return BiologySex.forId(id);
            case AGE:
                return LifeStage.forId(id);
            case ETHNICITY:
                return Ethnicity.forId(id);
            case LIFESTYLE:
                return LifeStyle.forId(id);
            case ENVIRONMENT:
                Term ectoTerm = resources.getEcto().getTermMap().get(TermId.of(id));
                return new OntoTerm(ectoTerm.getId().getValue(), ectoTerm.getName());
            case PHENOTYPE:
                Term hpoTerm = resources.getHPO().getTermMap().get(TermId.of(id));
                return new OntoTerm(hpoTerm.getId().getValue(), hpoTerm.getName());
            case DISEASE:
                Term mondoTerm = resources.getDiseaseSubOntology().getTermMap().get(TermId.of(id));
                return new OntoTerm(mondoTerm.getId().getValue(), mondoTerm.getName());
            case FAMILYHISTORY:
                return FamilyHistory.forId(id);
            case PRS:
                return PolygenicRiskScore.forId(id);
            default:
                return null;
        }
    }

    @FXML
    void deleteClicked(ActionEvent event) {
        event.consume();
        riskFactorRows.remove(riskFactorsTable.getSelectionModel().getSelectedItem());
    }

    @FXML
    void editClicked(ActionEvent event){
        event.consume();
//        RiskFactorRow removed = riskFactorsTable.getSelectionModel().getSelectedItem();
//        riskFactorRows.remove(removed);
//        for (Riskfactor riskfactor : currentRiskFactors){
//            if (riskfactor.getRiskType().equals(removed.riskFactorType)
//                    && riskfactor.getRiskId().getLabel().equals(removed.riskFactorId.getName())){
//                riskfactor.getEffectSizes().
//            }
//        }

    }

    @FXML
    private void confirmClicked(ActionEvent e) {
        e.consume();

        isUpdated = true;
        riskFactorRows.clear();
        confirm.accept(Signal.DONE);
    }

    @FXML
    private void cancelClicked(ActionEvent e) {
        e.consume();
        confirm.accept(Signal.CANCEL);
        riskFactorRows.clear();
    }

    public boolean isUpdated() {
        return this.isUpdated;
    }

    public List<Riskfactor> updated(){
        return currentRiskFactors;
    }

    public enum SimpleTimeUnit{
        Year,
        Month,
        Day
    }

    enum UncertaintyType {
        STDEV,
        CI95;

        @Override
        public String toString(){
            if (this == STDEV) {
                return "standard deviation";
            }
            if (this == CI95) {
                return "95% confidence interval";
            }
            return "unknown uncertainty type";
        }

        public UncertaintyType of(UncertaintyType uncertaintyType){
            if (uncertaintyType.equals("standard deviation")) {
                return STDEV;
            }
            if (uncertaintyType.equals("95% confidence interval")){
                return CI95;
            }
            return null;
        }
    }

    public class RiskFactorRow {
        private Riskfactor.RiskFactorType riskFactorType;
        private SimpleStringProperty riskFactorId;
        private SimpleStringProperty effectSizeType;
        private SimpleStringProperty effectSize;
        private SimpleStringProperty trendType;
        private SimpleStringProperty onsetAndPlateau;
        private SimpleStringProperty evidence;
        private SimpleStringProperty curationMeta;

        private TimeAwareEffectSize timeAwareEffectSize;

        //the constructor takes in risk factor type, id, and size, and converts them into a version for display
        public RiskFactorRow(Riskfactor.RiskFactorType riskFactorType,
                             String riskfactorId,
                             TimeAwareEffectSize timeAwareEffectSize) {
            this.riskFactorType = riskFactorType;
            this.riskFactorId = new SimpleStringProperty(riskfactorId);
            this.effectSizeType = new SimpleStringProperty(timeAwareEffectSize.getType().toString());
            this.effectSize = new SimpleStringProperty(Double.toString(timeAwareEffectSize.getSize().getMean()));
            this.trendType = new SimpleStringProperty(timeAwareEffectSize.getTrend().toString());
            Double yearsToOnset = timeAwareEffectSize.getYearsToOnset();
            Double yearsToPlateau = timeAwareEffectSize.getYearsToPlateau();

            this.onsetAndPlateau = new SimpleStringProperty(String.format("o:%s; p:%s",
                    yearsToOnset == null? "?": yearsToOnset.doubleValue(), // "?" or years_to_onset
                    yearsToPlateau == null ? "?" : yearsToPlateau.doubleValue())); // "?" or years_to_plateau
            this.evidence = new SimpleStringProperty(timeAwareEffectSize.getEvidence().getEvidenceId());
            this.curationMeta = new SimpleStringProperty(timeAwareEffectSize.getCurationMeta().getCurator());

            this.timeAwareEffectSize = timeAwareEffectSize;
        }

        public Riskfactor.RiskFactorType getRiskFactorType() {
            return riskFactorType;
        }

        public void setRiskFactorType(Riskfactor.RiskFactorType riskFactorType) {
            this.riskFactorType = riskFactorType;
        }

        public String getRiskFactorId() {
            return riskFactorId.get();
        }

        public SimpleStringProperty riskFactorIdProperty() {
            return riskFactorId;
        }

        public void setRiskFactorId(String riskFactorId) {
            this.riskFactorId.set(riskFactorId);
        }

        public String getEffectSizeType() {
            return effectSizeType.get();
        }

        public SimpleStringProperty effectSizeTypeProperty() {
            return effectSizeType;
        }

        public void setEffectSizeType(String effectSizeType) {
            this.effectSizeType.set(effectSizeType);
        }

        public String getEffectSize() {
            return effectSize.get();
        }

        public SimpleStringProperty effectSizeProperty() {
            return effectSize;
        }

        public void setEffectSize(String effectSize) {
            this.effectSize.set(effectSize);
        }

        public String getTrendType() {
            return trendType.get();
        }

        public SimpleStringProperty trendTypeProperty() {
            return trendType;
        }

        public void setTrendType(String trendType) {
            this.trendType.set(trendType);
        }

        public String getOnsetAndPlateau() {
            return onsetAndPlateau.get();
        }

        public SimpleStringProperty onsetAndPlateauProperty() {
            return onsetAndPlateau;
        }

        public void setOnsetAndPlateau(String onsetAndPlateau) {
            this.onsetAndPlateau.set(onsetAndPlateau);
        }

        public String getEvidence() {
            return evidence.get();
        }

        public SimpleStringProperty evidenceProperty() {
            return evidence;
        }

        public void setEvidence(String evidence) {
            this.evidence.set(evidence);
        }

        public String getCurationMeta() {
            return curationMeta.get();
        }

        public SimpleStringProperty curationMetaProperty() {
            return curationMeta;
        }

        public void setCurationMeta(String curationMeta) {
            this.curationMeta.set(curationMeta);
        }
    }

}
