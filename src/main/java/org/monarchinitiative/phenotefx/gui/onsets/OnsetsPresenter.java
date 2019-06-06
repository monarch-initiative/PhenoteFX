package org.monarchinitiative.phenotefx.gui.onsets;

import base.OntoTerm;
import base.PointValueEstimate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.CurationMeta;
import model.Onset;
import org.monarchinitiative.phenotefx.gui.PopUps;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.gui.evidencepopup.EvidenceFactory;
import org.monarchinitiative.phenotefx.gui.pointvalueestimate.PointValueEstimateFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    private Map<String, String> onsetTermName2IdMap;

    private ObjectMapper mapper = new ObjectMapper();

    private Consumer<Signal> signalConsumer;

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
            }
        });
        hasStage.setSelected(true);
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
            //create curation meta data
            beingEditted.setCurationMeta(new CurationMeta.Builder()
                    .curator(this.curatorId)
                    .timestamp(LocalDate.now())
                    .build());
            onsetObservableList.add(beingEditted);
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
            } else {
                OntoTerm stage = new OntoTerm(id, label);
                beingEditted.setStage(true);
                beingEditted.setStage(stage);
                beingEditted.setAge(false);
                beingEditted.setAge(null);
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
