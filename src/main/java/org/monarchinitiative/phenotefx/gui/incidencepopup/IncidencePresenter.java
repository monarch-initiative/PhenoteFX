package org.monarchinitiative.phenotefx.gui.incidencepopup;

import base.OntoTerm;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.gui.evidencepopup.EvidenceFactory;
import org.monarchinitiative.phenotefx.gui.frequency.FrequencyFactory;

import java.util.Collection;
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

    private model.Incidence current;

    private ObjectMapper mapper = new ObjectMapper();

    private Consumer<Signal> signalConsumer;

    private boolean isupdated;

    private Map<String, String> incidenceTermsName2Id;

    public void setCurrent(model.Incidence current){
        this.current = current;
    }

    public void setIncidenceTerms(Collection<OntoTerm> incidenceTerms){
        if (incidenceTerms != null) {
            incidenceTermsName2Id = incidenceTerms.stream().collect(Collectors.toMap(t -> t.getLabel(), t-> t.getId()));
        }
    }

    private void refresh(){
        try {
            valueField.setText(mapper.writeValueAsString(current.getValue()));
            evidenceField.setText(mapper.writeValueAsString(current.getEvidence()));
            curationMetaField.setText(mapper.writeValueAsString(current.getCurationMeta()));
        } catch (Exception e) {
            //ignore any exception
        }

    }

    public void setSignal(Consumer<Signal> signals){
        this.signalConsumer = signals;
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

    @FXML
    void evidenceClicked(ActionEvent event) {
        event.consume();
        EvidenceFactory factory = new EvidenceFactory(current.getEvidence());
        boolean updated = factory.openDiag();
        if (updated){
            current.setEvidence(factory.getEvidence());
            refresh();
        }

    }

    @FXML
    void valueClicked(ActionEvent event) {
        event.consume();
        FrequencyFactory factory = new FrequencyFactory(current.getValue(), incidenceTermsName2Id);
        boolean updated = factory.showDiag();
        if (updated){
            current.setValue(factory.getUpdated());
            refresh();
        }
    }

    public boolean updated(){
        return this.isupdated;
    }

    public model.Incidence updatedIncidence() {
        return this.current;
    }
}
