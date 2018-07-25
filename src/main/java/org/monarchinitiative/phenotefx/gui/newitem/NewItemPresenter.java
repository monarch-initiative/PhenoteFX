package org.monarchinitiative.phenotefx.gui.newitem;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2018 Peter Robinson
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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.model.PhenoRow;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * This is for a dialog with two text fields that allows the user to start a new file
 * for a new disease. One text field is for the disease id (e.g., OMIM:123456) and the
 * other is for the disease name. If the user clicks OK, it will initialize the first row of the
 * table. The user can also choose the mode of inheritance-- the first term. By
 * default the evidence is TAS and the source is the disease ID, but this can be changed in the
 * GUI if a PMID is available.
 * @author <a href:"mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class NewItemPresenter implements Initializable {

    private Consumer<Signal> signal;

    @FXML
    private TextArea diseaseName;

    @FXML
    private TextArea diseaseId;

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;

    @FXML
    private ChoiceBox<String> moiChoice;

    private String moi=null;

    private PhenoRow prow;

    @FXML private ObservableList<String> moiList =
            FXCollections.observableArrayList("dominant", "recessive", "X-recessive","X-dominant","mitochondrial");

    @FXML
    ChoiceBox<String> modeOfInheritanceChoice;
    private Stage dialogStage;
    private boolean okClicked = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moiChoice.setItems(moiList);
        moiChoice.getSelectionModel().selectFirst();
        moi=moiList.get(0);
        moiChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            setMoi(newValue);
        });
        // remove whitespace for disease ID field.
        this.diseaseId.textProperty().addListener( // ChangeListener
                (observable, oldValue, newValue) -> {
                    String txt = diseaseId.getText();
                    txt = txt.replaceAll("\\s", "");
                    diseaseId.setText(txt);
                });
    }

    private void setMoi(String newval) {
        moi=newval;
    }


    void setSignal(Consumer<Signal> signal) {
        this.signal = signal;
    }

    /**
     * @return true if the user clicked OK, false otherwise.
     */
    boolean isOkClicked() {
        return okClicked;
    }


    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage parent stage
     */
    void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    public PhenoRow getPhenoRow() {
        return prow;
    }

    @FXML
    private void doOK(ActionEvent e) {
        e.consume();
        String id = diseaseId.getText().trim();
        String name = diseaseName.getText().trim();
        prow = new PhenoRow();
        prow.setDiseaseID(id);
        prow.setEvidence("TAS");
        prow.setDiseaseName(name);
        String inhLabel="";
        String inhId="";
        switch (moi) {
            case "dominant":
                inhLabel="Autosomal dominant inheritance";
                inhId="HP:0000006";
                break;
            case "recessive":
                inhLabel="Autosomal recessive inheritance";
                inhId="HP:0000007";
                break;
            case "X-recessive":
                inhLabel="X-linked recessive inheritance";
                inhId="HP:0001419";
                break;
            case "X-dominant":
                inhLabel="X-linked dominant inheritance";
                inhId="HP:0001423";
                break;
            case "mitochondrial":
                inhLabel="Mitochondrial inheritance";
                inhId="HP:0001427";
                break;
             default:
                 System.err.println("COULD NOT GET INHERITANCE FROM CHOOSER. SHOULD NEVER HAPPEN");
        }
        prow.setPhenotypeID(inhId);
        prow.setPhenotypeName(inhLabel);
        okClicked=true;
        dialogStage.close();
    }

    @FXML
    private void doCancel(ActionEvent e) {
        e.consume();
        dialogStage.close();
    }

}
