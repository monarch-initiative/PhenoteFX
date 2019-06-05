package org.monarchinitiative.phenotefx.gui.prevalencepopup;

import base.Fraction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Callback;
import model.*;
import org.monarchinitiative.phenotefx.gui.PopUps;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.gui.evidencepopup.EvidenceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

//deal with both incidence and prevalence
public class PrevalencePresenter {

    private static final Logger logger = LoggerFactory.getLogger(PrevalencePresenter.class);

    private String curator;

    private ObservableList<Prevalence> prevalenceObservableList = FXCollections.observableArrayList();

    private Consumer<Signal> signalConsumer;

    private ObjectMapper mapper = new ObjectMapper();

    private Evidence evidence;

    private boolean updated;

    @FXML
    private RadioButton sexSpecific;

    @FXML
    private ComboBox<String> type;

    @FXML
    private TextField unisex_field;

    @FXML
    private TextField male_field;

    @FXML
    private TextField female_field;

    @FXML
    private ListView<Prevalence> listView;


    public void setCuratorId(String curator) {
        this.curator = curator;
    }

    public void setCurrentPrevalences(List<Prevalence> prevalenceList){
        if (prevalenceList != null){
            this.prevalenceObservableList.addAll(prevalenceList);
        }
    }

    public void setSignal(Consumer<Signal> signals) {
        this.signalConsumer = signals;
    }

    @FXML
    private void initialize(){
        //listView = new ListView<>();
        listView.setItems(prevalenceObservableList);
        listView.setCellFactory(new Callback<ListView<Prevalence>, ListCell<Prevalence>>() {
            @Override
            public ListCell<Prevalence> call(ListView<Prevalence> param) {
                return new ListCell<Prevalence>(){
                    @Override
                    protected void updateItem(Prevalence prevalence, boolean bl){
                        super.updateItem(prevalence, bl);
                        if (prevalence != null) {
                            String text;
                            try {
                                text = mapper.writeValueAsString(prevalence);
                            } catch (JsonProcessingException e) {
                                text = "JsonProcessingException";
                            }
                            setText(text);
                        }
                    }

                };
            }
        });

        //TODO: implement sex specific function
        type.getItems().addAll("number", "ontology term");
        sexSpecific.setSelected(false);
        type.getSelectionModel().select("number");
    }

    @FXML
    void evidenceClicked(ActionEvent event){
        event.consume();
        EvidenceFactory factory = new EvidenceFactory(null);
        boolean hasNewEvidence = factory.openDiag();
        if (hasNewEvidence){
            //assign evidence to something and use it somehow
            evidence = factory.getEvidence();
            ObjectMapper mapper = new ObjectMapper();
            try {
                logger.info(mapper.writeValueAsString(evidence));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void addClicked(ActionEvent event) {
        event.consume();

        boolean qcpassed = qcPassed();
        if (qcpassed){
            String unisex_string = unisex_field.getText();
            double numerator = Double.parseDouble(unisex_string.split("/")[0]);
            double denominator = Double.parseDouble(unisex_string.split("/")[1]);
            Fraction fraction = new Fraction(numerator, denominator);
            Prevalence newPrevalence = new Prevalence.Builder()
                    .isSexSpecific(sexSpecific.isSelected())
                    .value(new Frequency.Builder()
                            .fraction(fraction).build())
                    .evidence(this.evidence)
                    .curationMeta(new CurationMeta(this.curator, LocalDate.now()))
                    .build();
            prevalenceObservableList.add(newPrevalence);
            logger.info("prevalenceobservableList size: "+ prevalenceObservableList.size());
        } else {
            logger.info("qc failed");
        }
    }

    private boolean qcPassed() {
        if (evidence == null){
            PopUps.showInfoMessage("Evidence not specified", "ERROR");
            return false;
        }
        if (type.getSelectionModel().getSelectedItem().equals("ontology term")){
            PopUps.showInfoMessage("Ontology term not supported yet", "ERROR");
            return false;
        }
        if (!unisex_field.getText().trim().matches("[0-9]+/[0-9]+")){
            PopUps.showInfoMessage("Fraction formatting error", "ERROR");
            return false;
        }
        return true;
    }

    @FXML
    void clearClicked(ActionEvent event) {
        event.consume();
        unisex_field.clear();
        male_field.clear();
        female_field.clear();
        sexSpecific.setSelected(false);
        type.getSelectionModel().select("number");
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

    }

    public boolean prevalenceDirty() {
        return updated;
    }


    public List<Prevalence> updatedPrevalences() {
        return new ArrayList<>(prevalenceObservableList);
    }



}
