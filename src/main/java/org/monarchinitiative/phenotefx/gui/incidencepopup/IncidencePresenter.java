package org.monarchinitiative.phenotefx.gui.incidencepopup;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import model.Incidence;
import model.Prevalence;
import org.monarchinitiative.phenotefx.gui.Signal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

//deal with both incidence and prevalence
public class IncidencePresenter {

     String curator;

     ObservableList<Prevalence> prevalenceObservableList;
     ObservableList<Incidence> incidenceObservableList;

     Window stage;
     Consumer<Signal> signal;

    @FXML
    private TextField unisex_prevalence;

    @FXML
    private TextField years_to_onset_textfield;

    @FXML
    private TextField years_to_plateau_textfield;

    @FXML
    private TextField evidenceIdTextField;

    @FXML
    private TableView<?> riskFactorsTable;


    public void setCuratorId(String curator) {
        this.curator = curator;
    }

    public void setCurrentPrevalences(List<Prevalence> prevalenceList){
        this.prevalenceObservableList.addAll(prevalenceList);
    }

    public void setCurrentIncidences(List<Incidence> incidenceList){
        this.incidenceObservableList.addAll(incidenceList);
    }

    public void setDialogStage(Window stage) {
        this.stage = stage;
    }

    public void setSignal(Consumer<Signal> signals) {
        this.signal = signals;
    }

    @FXML
    void addClicked(ActionEvent event) {

    }

    @FXML
    void cancelClicked(ActionEvent event) {

    }

    @FXML
    void clearClicked(ActionEvent event) {

    }

    @FXML
    void confirmClicked(ActionEvent event) {

    }

    @FXML
    void deleteClicked(ActionEvent event) {

    }

    public boolean prevalenceDirty() {
        return false;
    }

    public boolean incidenceDirty() {
        return false;
    }

    public List<Incidence> updatedIncidences(){
        return new ArrayList<Incidence>(incidenceObservableList);
    }

    public List<Prevalence> updatedPrevalences() {
        return new ArrayList<>(prevalenceObservableList);
    }

}
