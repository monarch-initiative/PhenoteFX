package org.monarchinitiative.phenotefx.gui.newCommonDisease;

import base.OntoTerm;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.stage.Window;
import model.CommonDiseaseAnnotation;
import org.monarchinitiative.phenotefx.gui.PopUps;
import org.monarchinitiative.phenotefx.gui.Signal;

import java.util.function.Consumer;

public class NewCommonDiseasePresenter {

    private Window window;
    private Consumer<Signal> consumer;

    @FXML
    private TextArea diseaseId;

    @FXML
    private TextArea diseaseName;

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

    @FXML
    void doCancel(ActionEvent event) {
        event.consume();
        this.consumer.accept(Signal.CANCEL);
    }

    @FXML
    void doOK(ActionEvent event) {
        event.consume();
        boolean qcpassed = qcPassed();
        if (!qcpassed) {
            return;
        }
        this.consumer.accept(Signal.DONE);
    }

    public OntoTerm newDisease() {
        return new OntoTerm(diseaseId.getText().trim(), diseaseName.getText().trim());
    }

    private boolean qcPassed() {
        if (diseaseId.getText().trim().isEmpty() || diseaseName.getText().trim().isEmpty()) {
            PopUps.showInfoMessage("Both disease mondo id and name should be present", "ERROR");
            return false;
        }
        return true;
    }


}
