package org.monarchinitiative.phenotefx.gui.annotationcheck;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.model.PhenoRow;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class AnnotationCheckPresenter implements Initializable {

    private @FXML Label currentOnset;
    private @FXML Label newOnset;
    private @FXML Label currentFrequency;
    private @FXML Label newFrequency;
    private @FXML Label currentSex;
    private @FXML Label newSex;
    private @FXML Label currentNot;
    private @FXML Label newNot;
    private @FXML Label currentModifier;
    private @FXML Label newModifier;
    private @FXML Label currentDescription;
    private @FXML Label newDescription;
    private @FXML Label currentPub;
    private @FXML Label newPub;
    private @FXML Label currentEvidence;
    private @FXML Label newEvidence;


    private Consumer<Signal> signal;

    private PhenoRow oldrow;

    private PhenoRow newrow;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // no-op, we need to receive data via setData
    }

    void setSignal(Consumer<Signal> signal) {
        this.signal = signal;
    }

    public AnnotationCheckPresenter(PhenoRow oldrow, PhenoRow newrow) {
        this.oldrow=oldrow;
        this.newrow=newrow;
        this.currentOnset.setText(oldrow.getOnsetName());
    }


}
