package org.monarchinitiative.phenotefx.gui.onsets;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.CurationMeta;
import model.Onset;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.monarchinitiative.phenotefx.gui.PopUps;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.gui.WidthAwareTextFields;
import org.monarchinitiative.phenotefx.gui.evidencepopup.EvidenceFactory;
import org.monarchinitiative.phenotefx.gui.pointvalueestimate.PointValueEstimateFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

public class OnsetsPresenter {

    @FXML
    private RadioButton hasStage;

    @FXML
    private TextField stageLabelField;

    @FXML
    private TextField stageIdField;

    @FXML
    private Button ageButton;

    @FXML
    private TextField ageField;

    @FXML
    private TextField evidenceField;

    @FXML
    private TextField curationMetaField;

    @FXML
    private ListView<Onset> listview;


    private ObservableList<Onset> onsetObservableList = FXCollections.observableArrayList();

    private boolean updated;

    private Onset beingEditted = new Onset.Builder().build();

    private String curatorId;

    private Map<String, String> onsetTermName2IdMap = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();

    private Consumer<Signal> signalConsumer;

    private AutoCompletionBinding autoCompletion;

    public void setCurator(String curator){
        this.curatorId = curator;
    }

    public void setCurrent(Collection<Onset> onsets){
        if (onsets != null){
            onsetObservableList.addAll(onsets);
        }
    }

    public void setTermMap(Map<String, String> onsetTermName2Idmap){
        this.onsetTermName2IdMap = onsetTermName2Idmap;
        autoCompletion = WidthAwareTextFields.bindWidthAwareAutoCompletion(stageLabelField, onsetTermName2IdMap.keySet());
    }

    public void setSignal(Consumer<Signal> signals){
        this.signalConsumer = signals;
    }

    @FXML
    void initialize(){
        listview.setItems(onsetObservableList);
        listview.setCellFactory(new Callback<ListView<Onset>, ListCell<Onset>>() {
            @Override
            public ListCell<Onset> call(ListView<Onset> param) {
                return new ListCell<Onset>(){
                    @Override
                    protected void updateItem(Onset onset, boolean bl){
                        super.updateItem(onset, bl);
                        if (onset != null) {
                            String text;
                            try {
                                text = mapper.writeValueAsString(onset);
                            } catch (JsonProcessingException e){
                                text = "JsonProcessingException";
                            }
                            setText(text);
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });

        hasStage.selectedProperty().addListener((obj, oldvalue, newvalue) -> {
            if (obj != null){
                if (newvalue){
                    ageButton.setDisable(true);
                    stageLabelField.setDisable(false);
                    stageIdField.setDisable(false);
                } else {
                    ageButton.setDisable(false);
                    stageLabelField.setDisable(true);
                    stageIdField.setDisable(true);
                }
                beingEditted.setStage(newvalue);
            }
        });
        hasStage.setSelected(true);
        stageLabelField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (observable != null && newValue != null){
                String id = "";
                if (onsetTermName2IdMap.containsKey(newValue)){
                    id = onsetTermName2IdMap.get(newValue);
                }
                stageIdField.setText(id);
            }
        });
    }

    private void refresh() {

        hasStage.setSelected(beingEditted.isStage());

        if (beingEditted.getStage() != null){
            stageLabelField.setText(beingEditted.getStage().getLabel());
            stageIdField.setText(beingEditted.getStage().getId());
        }
        try {
            ageField.setText(mapper.writeValueAsString(beingEditted.getAge()));
            evidenceField.setText(mapper.writeValueAsString(beingEditted.getEvidence()));
            curationMetaField.setText(mapper.writeValueAsString(beingEditted.getCurationMeta()));
        } catch (Exception e){
            //eat all exception for now
        }
    }

    private void clear() {
        hasStage.setSelected(false);
        stageLabelField.clear();
        stageIdField.clear();
        ageField.clear();
        evidenceField.clear();
        curationMetaField.clear();
        beingEditted = new Onset.Builder().build();
    }


    @FXML
    void ageClicked(ActionEvent event) {
        event.consume();

        PointValueEstimateFactory factory = new PointValueEstimateFactory(beingEditted.getAge());
        boolean updated = factory.openDiag();
        if (updated){
            beingEditted.setStage(false);
            beingEditted.setAge(factory.updated());
            beingEditted.setAge(true);
            refresh();
        }
    }

    @FXML
    void curationMetaClicked(ActionEvent event) {
        event.consume();
        //do nothing now
    }

    @FXML
    void evidenceClicked(ActionEvent event) {
        event.consume();

        EvidenceFactory factory = new EvidenceFactory(beingEditted.getEvidence());
        boolean updated = factory.openDiag();
        if (updated){
            beingEditted.setEvidence(factory.getEvidence());
            refresh();
        }
    }

    @FXML
    void addClicked(ActionEvent event) {
        event.consume();

        boolean qcpassed = qcPassed();

        if (qcpassed){
            if (hasStage.isSelected()){
                String id = stageIdField.getText().trim();
                String label = stageLabelField.getText().trim();
                OntoTerm stage = new OntoTerm(id, label);
                beingEditted.setStage(true);
                beingEditted.setStage(stage);
                beingEditted.setAge(false);
                beingEditted.setAge(null);
            } else {
                //age would have been set, so do nothing here
            }
            //create curation meta data
            beingEditted.setCurationMeta(new CurationMeta.Builder()
                    .curator(this.curatorId)
                    .timestamp(LocalDate.now())
                    .build());
            onsetObservableList.add(new Onset(beingEditted));
            clear();
            beingEditted = new Onset.Builder().build();
            refresh();
        }
    }

    private boolean qcPassed() {
        boolean passed = false;

        if (hasStage.isSelected()){
            String label = stageLabelField.getText().trim();
            String id = stageIdField.getText().trim();
            if (label.isEmpty() || id.isEmpty()){
                PopUps.showInfoMessage("Onset stage ontology term not specified", "ERROR");
                return false;
            }
        } else {
            if (beingEditted.getAge() == null) {
                PopUps.showInfoMessage("Onset age not specified", "ERROR");
                return false;
            }
        }

        return true;
    }

    @FXML
    void clearClicked(ActionEvent event) {
        event.consume();
        clear();
    }

    @FXML
    void cancelClicked(ActionEvent event) {
        event.consume();
        signalConsumer.accept(Signal.CANCEL);
    }

    @FXML
    void confirmClicked(ActionEvent event) {
        event.consume();
        updated = true;
        signalConsumer.accept(Signal.DONE);
    }

    @FXML
    void deleteClicked(ActionEvent event) {
        event.consume();
        onsetObservableList.remove(listview.getSelectionModel().getSelectedItem());
    }

    @FXML
    void editClicked(ActionEvent event) {
        event.consume();
        beingEditted = listview.getSelectionModel().getSelectedItem();
        onsetObservableList.remove(beingEditted);
        refresh();
    }

    public boolean isUpdated(){
        return updated;
    }

    public List<Onset> updated(){
        return new ArrayList<>(onsetObservableList);
    }

}
