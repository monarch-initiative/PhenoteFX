package org.monarchinitiative.phenotefx.gui.evidencepopup;

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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import model.Evidence;
import org.monarchinitiative.phenotefx.gui.PopUps;
import org.monarchinitiative.phenotefx.gui.Signal;

import java.util.function.Consumer;

public class EvidencePresenter {

    private Consumer<Signal> signalConsumer;

    private Evidence evidence;

    private boolean evidenceupdated;

    public void setSignal(Consumer<Signal> consumer){
        this.signalConsumer = consumer;
    }

    @FXML
    private ComboBox<Evidence.EvidenceType> evidenceTypeCombo;

    @FXML
    private TextField evidenceIdField;

    @FXML
    private void initialize() {
        evidenceTypeCombo.getItems().addAll(Evidence.EvidenceType.values());
        evidenceTypeCombo.getSelectionModel().select(Evidence.EvidenceType.PCS);
    }

    public void setCurrent(Evidence evidence){
        this.evidence = evidence;
        if (this.evidence != null){
            evidenceTypeCombo.getSelectionModel().select(this.evidence.getEvidenceType());
            evidenceIdField.setText(this.evidence.getEvidenceId());
        }
    }

    @FXML
    void addClicked(ActionEvent event) {
        event.consume();
        boolean qcpass = qcPassed();
        if (qcpass){
            evidence = new Evidence.Builder()
                    .evidenceType(evidenceTypeCombo.getSelectionModel().getSelectedItem())
                    .evidenceId(evidenceIdField.getText().trim())
                    .build();
            evidenceupdated = true;
            signalConsumer.accept(Signal.DONE);
        } else {
            //do nothing
        }

    }

    @FXML
    void clearClicked(ActionEvent event) {
        event.consume();
        evidenceTypeCombo.getSelectionModel().select(Evidence.EvidenceType.PCS);
        evidenceIdField.clear();
        signalConsumer.accept(Signal.CANCEL);
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public boolean updated(){
        return this.evidenceupdated;
    }

    private boolean qcPassed(){
        if (evidenceTypeCombo.getSelectionModel().isEmpty() || evidenceIdField.getText().trim().isEmpty()){
            PopUps.showInfoMessage("Evidence type or id not specified", "ERROR");
            return false;
        }
        return true;
    }
}
