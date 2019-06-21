package org.monarchinitiative.phenotefx.gui.newCommonDisease;

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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.jetbrains.annotations.NotNull;
import org.monarchinitiative.phenotefx.gui.PopUps;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.gui.WidthAwareTextFields;

import java.util.Map;
import java.util.function.Consumer;

public class NewCommonDiseasePresenter {

    private Window window;
    private Consumer<Signal> consumer;
    private Map<String, String> diseaseName2IdMap;
    private AutoCompletionBinding autoCompletionBinding;
    private OntoTerm disease;
    private boolean isUpdated;

    @FXML
    private TextField diseaseId;

    @FXML
    private TextField diseaseName;

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;


    public void setDialogStage(Window window) {
        this.window = window;
    }

    public void setSignal(Consumer<Signal> consumer) {
        this.consumer = consumer;
    }

    public void setDiseaseMap(@NotNull Map<String, String> diseaseMap){
        this.diseaseName2IdMap = diseaseMap;
         autoCompletionBinding = WidthAwareTextFields.bindWidthAwareAutoCompletion(diseaseName, diseaseName2IdMap.keySet());
    }

    @FXML
    void initialize(){
        diseaseName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (observable != null && newValue != null){
                String recommended = diseaseName2IdMap.getOrDefault(newValue, "");
                diseaseId.setText(recommended);
            }
        });
    }

    @FXML
    void doCancel(ActionEvent event) {
        event.consume();
        this.consumer.accept(Signal.CANCEL);
    }

    @FXML
    void doOK(ActionEvent event) {
        event.consume();
        boolean qcpassed = qcPassed();
        if (qcpassed){
            this.disease = new OntoTerm(new String(diseaseId.getText().trim()), new String(diseaseName.getText().trim()));
            this.isUpdated = true;
            this.consumer.accept(Signal.DONE);
        }
    }

    private boolean qcPassed() {
        if (diseaseId.getText().trim().isEmpty() || diseaseName.getText().trim().isEmpty()) {
            PopUps.showInfoMessage("Both disease mondo id and name should be present", "ERROR");
            return false;
        }
        return true;
    }

    public OntoTerm updated(){
        return this.disease;
    }

    public boolean isUpdated() {
        return this.isUpdated;
    }


}
