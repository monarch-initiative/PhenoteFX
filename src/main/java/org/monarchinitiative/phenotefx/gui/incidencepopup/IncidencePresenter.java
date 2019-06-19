package org.monarchinitiative.phenotefx.gui.incidencepopup;

import base.OntoTerm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.CurationMeta;
import model.Incidence;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.gui.evidencepopup.EvidenceFactory;
import org.monarchinitiative.phenotefx.gui.frequency.FrequencyFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class IncidencePresenter {

    @FXML
    private TextField valueField;

    @FXML
    private TextField evidenceField;

    @FXML
    private TextField curationMetaField;

    @FXML
    private ListView<model.Incidence> listview;

    private model.Incidence beingEditted = new Incidence.Builder().build();

    private ObjectMapper mapper = new ObjectMapper();

    private Consumer<Signal> signalConsumer;

    private boolean isupdated;

    private Map<String, String> incidenceTermsName2Id;

    private String curatorId;

    //private List<Incidence> incidences;

    private ObservableList<model.Incidence> observableList = FXCollections.observableArrayList();


    public void setCurrent(List<model.Incidence> current){
        if (current != null){
            this.observableList.addAll(current);
        }
    }

    public void setIncidenceTerms(Collection<OntoTerm> incidenceTerms){
        if (incidenceTerms != null) {
            incidenceTermsName2Id = incidenceTerms.stream().collect(Collectors.toMap(t -> t.getLabel(), t-> t.getId()));
        }

    }

    public void setCuratorId(String curator){
        this.curatorId = curator;
    }

    private void refresh(){
        try {
            valueField.setText(mapper.writeValueAsString(beingEditted.getValue()));
            evidenceField.setText(mapper.writeValueAsString(beingEditted.getEvidence()));
            curationMetaField.setText(mapper.writeValueAsString(beingEditted.getCurationMeta()));
        } catch (Exception e) {
            //ignore any exception
        }

    }

    public void setSignal(Consumer<Signal> signals){
        this.signalConsumer = signals;
    }


    @FXML
    void initialize() {
        listview.setItems(observableList);
        listview.setCellFactory(new Callback<ListView<Incidence>, ListCell<Incidence>>() {
            @Override
            public ListCell<Incidence> call(ListView<Incidence> param) {
                return new ListCell<Incidence>(){
                    @Override
                    protected void updateItem(Incidence incidence, boolean bl){
                        super.updateItem(incidence, bl);
                        if (incidence != null) {
                            String text;
                            try {
                                //just show value
                                //TODO: replace with a incidence toString method
                                text = mapper.writeValueAsString(incidence.getValue());
                            } catch (JsonProcessingException e){
                                text = "JsonProcessingException";
                            }
                            setText(text);
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
    }

    private void clear() {
        valueField.clear();
        evidenceField.clear();
        curationMetaField.clear();
    }

    @FXML
    void addClicked(ActionEvent event) {
        event.consume();
        //create curation meta
        beingEditted.setCurationMeta(new CurationMeta.Builder()
                .curator(this.curatorId)
                .timestamp(LocalDate.now())
                .build());
        observableList.add(new Incidence(beingEditted));
        beingEditted = new Incidence.Builder().build();
        clear();
    }

    @FXML
    void clearClicked(ActionEvent event) {
        event.consume();
        clear();
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
    void valueClicked(ActionEvent event) {
        event.consume();
        FrequencyFactory factory = new FrequencyFactory(beingEditted.getValue(), incidenceTermsName2Id);
        boolean updated = factory.showDiag();
        if (updated){
            beingEditted.setValue(factory.getUpdated());
            refresh();
        }
    }

    @FXML
    void deleteClicked(ActionEvent event) {
        event.consume();
        observableList.remove(listview.getSelectionModel().getSelectedIndex());
    }

    @FXML
    void editClicked(ActionEvent event) {
        event.consume();
        beingEditted = listview.getSelectionModel().getSelectedItem();
        refresh();
    }

    @FXML
    void cancelClicked(ActionEvent event) {
        event.consume();
        this.signalConsumer.accept(Signal.CANCEL);
    }

    @FXML
    void confirmClicked(ActionEvent event) {
        event.consume();

        isupdated = true;

        this.signalConsumer.accept(Signal.DONE);
    }

    public boolean isUpdated(){
        return this.isupdated;
    }

    public List<Incidence> updated() {
        return new ArrayList<>(observableList);
    }


}
