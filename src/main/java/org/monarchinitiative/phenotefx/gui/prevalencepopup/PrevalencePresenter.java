package org.monarchinitiative.phenotefx.gui.prevalencepopup;

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
import model.*;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.gui.evidencepopup.EvidenceFactory;
import org.monarchinitiative.phenotefx.gui.frequency.FrequencyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

//deal with both incidence and beingEditted
public class PrevalencePresenter {

    private static final Logger logger = LoggerFactory.getLogger(PrevalencePresenter.class);

    private String curator;

    //TODO: call this one
    private Map<String, String> candidateTermName2IdMap = new HashMap<>();

    private ObservableList<Prevalence> prevalenceObservableList = FXCollections.observableArrayList();

    private Consumer<Signal> signalConsumer;

    private ObjectMapper mapper = new ObjectMapper();



    //This is the prevalence that we are editing now
    private Prevalence beingEditted = new Prevalence.Builder().build();

    private boolean updated;
//    private model.Frequency frequency_unisex;
//    private model.Frequency frequency_male;
//    private model.Frequency frequency_female;
//    private Evidence evidence;
//    private CurationMeta curationMeta;

    @FXML
    private RadioButton sexSpecific;

    @FXML
    private Button maleButton;

    @FXML
    private Button femaleButton;

    @FXML
    private TextField unisex_field;

    @FXML
    private TextField male_field;

    @FXML
    private TextField female_field;

    @FXML
    private TextField evidenceField;

    @FXML
    private TextField curationMetaField;

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

    public void setCandidateTerms(Map<String, String> termMaps){
        candidateTermName2IdMap = termMaps;
    }

    @FXML
    private void initialize(){
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
        maleButton.setDisable(true);
        femaleButton.setDisable(true);
        sexSpecific.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (observable != null){
                    if (newValue){
                        maleButton.setDisable(false);
                        femaleButton.setDisable(false);
                    } else{
                        maleButton.setDisable(true);
                        femaleButton.setDisable(true);
                    }
                    beingEditted.setIsSexSpecific(newValue);
                }
            }
        });
    }

    private void refresh() {
        try {
            unisex_field.setText(mapper.writeValueAsString(beingEditted.getValue()));
            male_field.setText(mapper.writeValueAsString(beingEditted.getMale()));
            female_field.setText(mapper.writeValueAsString(beingEditted.getFemale()));
            evidenceField.setText(mapper.writeValueAsString(beingEditted.getEvidence()));
            curationMetaField.setText(mapper.writeValueAsString(beingEditted.getCurationMeta()));
        } catch (Exception e){
            //eat NPE and Jackson exceptions
        }
    }

    @FXML
    void unisexClicked(ActionEvent event){
        event.consume();
        //TODO: pass candidate terms
        FrequencyFactory factory = new FrequencyFactory(beingEditted.getValue(), candidateTermName2IdMap);
        boolean updated = factory.showDiag();
        if (updated){
            beingEditted.setValue(factory.getUpdated());
            refresh();
        }
    }

    @FXML
    void maleClicked(ActionEvent event){
        event.consume();
        FrequencyFactory factory = new FrequencyFactory(beingEditted.getMale(), candidateTermName2IdMap);
        boolean updated = factory.showDiag();
        if (updated){
            beingEditted.setMale(factory.getUpdated());
            refresh();
        }
    }

    @FXML
    void femaleClicked(ActionEvent event){
        event.consume();
        FrequencyFactory factory = new FrequencyFactory(beingEditted.getFemale(), candidateTermName2IdMap);
        boolean updated = factory.showDiag();
        if (updated){
            beingEditted.setFemale(factory.getUpdated());
            refresh();
        }

    }

    @FXML
    void evidenceClicked(ActionEvent event){
        event.consume();
        EvidenceFactory factory = new EvidenceFactory(beingEditted.getEvidence());
        boolean hasNewEvidence = factory.openDiag();
        if (hasNewEvidence){
            beingEditted.setEvidence(factory.getEvidence());
            refresh();
        }
    }

    @FXML
    void curationMetaClicked(ActionEvent event){
        event.consume();
        //no nothing now
    }

    @FXML
    void addClicked(ActionEvent event) {
        event.consume();
        beingEditted.setCurationMeta(new CurationMeta.Builder().curator(this.curator).timestamp(LocalDate.now()).build());
        prevalenceObservableList.add(beingEditted);
        beingEditted = new Prevalence.Builder().build();
        clear();
    }

    @FXML
    void clearClicked(ActionEvent event) {
        event.consume();
        clear();
    }

    private void clear(){
        sexSpecific.setSelected(false);
        unisex_field.clear();
        male_field.clear();
        female_field.clear();
        evidenceField.clear();
        curationMetaField.clear();
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
        prevalenceObservableList.remove(listView.getSelectionModel().getSelectedIndex());
        logger.info("one beingEditted record is removed");
    }

    @FXML
    void editClicked(ActionEvent event){
        event.consume();
        Prevalence tobeEditted = listView.getSelectionModel().getSelectedItem();
        prevalenceObservableList.remove(tobeEditted);
        refresh();
    }

    public boolean prevalenceDirty() {
        return updated;
    }


    public List<Prevalence> updatedPrevalences() {
        List<Prevalence> result = new ArrayList<>();
        result.addAll(prevalenceObservableList);
        return result;
    }


}
