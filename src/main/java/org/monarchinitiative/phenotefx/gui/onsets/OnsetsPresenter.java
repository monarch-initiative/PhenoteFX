package org.monarchinitiative.phenotefx.gui.onsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.Onset;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.gui.evidencepopup.EvidenceFactory;
import org.monarchinitiative.phenotefx.gui.pointvalueestimate.PointValueEstimateFactory;

import java.util.Collection;
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

    private Onset beingEditted;

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
        try {
            hasStage.setSelected(beingEditted.isStage());
            stageLabelField.setText(beingEditted.getStage().getLabel());
            stageIdField.setText(beingEditted.getStage().getId());
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
    }


    @FXML
    void ageClicked(ActionEvent event) {
        event.consume();

        PointValueEstimateFactory factory = new PointValueEstimateFactory(beingEditted.getAge());
        boolean updated = factory.openDiag();
        if (updated){
            beingEditted.setAge(factory.updated());
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
        }
    }

    @FXML
    void addClicked(ActionEvent event) {
        event.consume();




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

}
