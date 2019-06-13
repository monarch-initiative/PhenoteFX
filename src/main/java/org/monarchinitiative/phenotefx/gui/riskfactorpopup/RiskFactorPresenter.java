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
import com.fasterxml.jackson.databind.ObjectMapper;
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
import model.*;
import ontology_term.*;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.gui.PopUps;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.gui.WidthAwareTextFields;
import org.monarchinitiative.phenotefx.gui.effectsizepopup.TimeAwareEffectSizeFactory;
import org.monarchinitiative.phenotefx.service.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//The presenter manages two tasks:
//Editting what kind of risk factor
//Editting an effectsize for it
//TODO: we probably should slit it
public class RiskFactorPresenter implements Initializable {

    private static Logger logger = LoggerFactory.getLogger(RiskFactorPresenter.class);

    @FXML
    private ComboBox<Riskfactor.RiskFactorType> riskFactorCombo;

    @FXML
    private TextField labelField;

    @FXML
    private TextField idField;

    @FXML
    private TableView<Riskfactor> riskfactorTable;
    @FXML
    private TableColumn<Riskfactor, String> riskTypeColumn;
    @FXML
    private TableColumn<Riskfactor, String> riskIdColumn;
    @FXML
    private TableColumn<Riskfactor, String> riskfactorEZrecordNumCol;
    @FXML
    private TableColumn<Riskfactor, String> ezJsonStringCol;


    private Resources resources;

    private String curatorId;

    //This is a pointer to different maps, depending on the value of riskFactorCombo
    private Map<String, String> riskFactorMap = new HashMap<>();
    private AutoCompletionBinding autoCompletionBinding;

    private ObjectMapper mapper = new ObjectMapper();

    private Consumer<Signal> signals;
    private boolean isUpdated;

    private Riskfactor beingEditedRiskFactor = new Riskfactor.Builder().build();

    private ObservableList<Riskfactor> riskfactorObservableList = FXCollections.observableArrayList();


    public void setResource(Resources injected) {
        resources = injected;
    }

    public void setCuratorId(String curatorId) {
        this.curatorId = curatorId;
    }

    public void setCurrentRiskFactors(Collection<Riskfactor> currentRiskFactors) {
        if (currentRiskFactors != null){
            riskfactorObservableList.addAll(currentRiskFactors);
        }
    }

    public void setSignal(Consumer<Signal> signal) {
        this.signals = signal;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        riskFactorCombo.getItems().addAll(Riskfactor.RiskFactorType.values());
        riskFactorCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if( observable != null && newValue != null) {
                idField.clear();
                if (autoCompletionBinding != null) {
                    autoCompletionBinding.dispose();
                }
                bindName2IdMap(newValue);
                autoCompletionBinding =
                        WidthAwareTextFields.bindWidthAwareAutoCompletion(labelField,
                                riskFactorMap.keySet());
                autoCompletionBinding.setVisibleRowCount(10);
            }
        });

        labelField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (observable != null && newValue != null){
                String id = "uncognized term label";
                if (riskFactorMap.containsKey(newValue)){
                    id = riskFactorMap.get(newValue);
                }
                idField.setText(id);
            }
        });


        initRiskFactorTable();
        refresh();
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
        riskfactorTable.setItems(riskfactorObservableList);
        riskTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRiskType().toString()));
        riskIdColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRiskId().getLabel()));
        riskfactorEZrecordNumCol.setCellValueFactory(param -> {
            int size = 0;
            if (param.getValue().getEffectSizes() != null){
                size = param.getValue().getEffectSizes().size();
            }
            return new SimpleStringProperty(Integer.toString(size));
        });
        ezJsonStringCol.setCellValueFactory(param -> {
            String s = "";
            try {
                s = mapper.writeValueAsString(beingEditedRiskFactor.getEffectSizes());
            } catch (Exception e){
                //eat it
            }
            return new SimpleStringProperty(s);
        });
    }

    private void refresh() {
        try {
            riskFactorCombo.getSelectionModel().select(beingEditedRiskFactor.getRiskType());
        } catch (Exception e){
            //eat it
        }
        try {
            labelField.setText(beingEditedRiskFactor.getRiskId().getLabel());
        } catch (Exception e){
            labelField.clear();
        }
        try {
            idField.setText(beingEditedRiskFactor.getRiskId().getId());
        } catch (Exception e){
            idField.clear();
        }
    }


    @FXML
    void addClicked(ActionEvent event){
        event.consume();
        boolean qcpssed = qcPassed();
        if (qcpssed){
            Riskfactor.RiskFactorType riskFactorType = riskFactorCombo.getSelectionModel().getSelectedItem();
            String termLabel = labelField.getText().trim();
            String termId = riskFactorMap.get(termLabel);
            OntoTerm riskId = new OntoTerm(termId, termLabel);
            beingEditedRiskFactor.setRiskType(riskFactorType);
            beingEditedRiskFactor.setRiskId(riskId);
            riskfactorObservableList.add(beingEditedRiskFactor);
            beingEditedRiskFactor = new Riskfactor.Builder().build();
            refresh();
        }
    }


    @FXML
    void clearClicked(ActionEvent event) {
        event.consume();
        refresh();
    }


    private boolean qcPassed(){

        if (riskFactorCombo.getSelectionModel().isEmpty()){
            PopUps.showInfoMessage("RiskFactorType not specified", "ERROR");
            return false;
        }

        if (labelField.getText().trim().isEmpty()){
            PopUps.showInfoMessage("RiskFactor label is not specified", "ERROR");
            return false;
        }

        if (idField.getText().trim().isEmpty()){
            PopUps.showInfoMessage("RiskFactor id is not specified", "ERROR");
            return false;
        }

        return true;
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
    private void confirmClicked(ActionEvent e) {
        e.consume();

        isUpdated = true;
//        riskfactorObservableList.add(beingEditedRiskFactor);
//        beingEditedRiskFactor = new Riskfactor.Builder().build();
        signals.accept(Signal.DONE);
    }

    @FXML
    private void cancelClicked(ActionEvent e) {
        e.consume();
        signals.accept(Signal.CANCEL);
    }

    @FXML
    private void deleteClicked(ActionEvent event){
        event.consume();
        Riskfactor toRemove = riskfactorTable.getSelectionModel().getSelectedItem();
        if (toRemove.getEffectSizes() != null && !toRemove.getEffectSizes().isEmpty()){
            boolean confirmed = PopUps.getBooleanFromUser("Are you sure you want to delete this", "Risk factor has effect size annotation(s)", "Warn");
            if (confirmed){
                riskfactorObservableList.remove(toRemove);
            }
        } else {
            riskfactorObservableList.remove(toRemove);
        }
    }

    @FXML
    private void editClicked(ActionEvent event){
        event.consume();

        beingEditedRiskFactor = riskfactorTable.getSelectionModel().getSelectedItem();

        TimeAwareEffectSizeFactory factory = new TimeAwareEffectSizeFactory(beingEditedRiskFactor.getEffectSizes(),
                curatorId,
                beingEditedRiskFactor.getRiskId().getLabel());
        boolean isUpdated = factory.openDiag();
        if (isUpdated){
            beingEditedRiskFactor.setEffectSizes(factory.updated());
            riskfactorTable.refresh();
            //The following remove-add just notify the list view to update
            //TODO: refactor
//            riskfactorObservableList.remove(beingEditedRiskFactor);
//            riskfactorObservableList.add(beingEditedRiskFactor);
        }
    }

    public boolean isUpdated() {
        return this.isUpdated;
    }

    public List<Riskfactor> updated(){
        return new ArrayList<>(riskfactorObservableList);
    }

}
