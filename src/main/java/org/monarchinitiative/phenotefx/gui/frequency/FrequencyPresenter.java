package org.monarchinitiative.phenotefx.gui.frequency;

import base.OntoTerm;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import model.Frequency;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.gui.WidthAwareTextFields;

import java.util.Map;
import java.util.function.Consumer;

public class FrequencyPresenter {

    @FXML
    private RadioButton isFraction;

    @FXML
    private RadioButton isOntologyTerm;

    @FXML
    private TextField ontoTermField;

    @FXML
    private TextField fractionNomField;

    @FXML
    private TextField fractionDenomField;

    private Map<String, String> ontoTermMap;

    private model.Frequency frequency = new Frequency.Builder().build();

    private boolean dirty;

    private Consumer<Signal> signalConsumer;

    private AutoCompletionBinding<String> autoCompletionBinding;


    public void setCurrent(model.Frequency current){
        if (current != null){
            this.frequency = current;
            if(current.isApproximate()){
                ontoTermField.setText(current.getApproximate().getLabel());
            } else if(current.isFraction()){
                fractionNomField.setText(Double.toString(current.getFraction().getNumerator()));
                fractionDenomField.setText(Double.toString(current.getFraction().getDenominator()));
            } else {
                //do nothing
            }
        }
    }

    public void setOntoTermMap(Map<String, String> termName2IdMap) {
        this.ontoTermMap = termName2IdMap;
        autoCompletionBinding = WidthAwareTextFields.bindWidthAwareAutoCompletion(ontoTermField, ontoTermMap.keySet());
        autoCompletionBinding.setVisibleRowCount(5);

    }

    public void setSignal(Consumer<Signal> signals){
        this.signalConsumer = signals;
    }


    @FXML
    void cancelClicked(ActionEvent event) {
        event.consume();
        autoCompletionBinding.dispose();
        signalConsumer.accept(Signal.CANCEL);
    }

    @FXML
    void confirmClicked(ActionEvent event) {
        event.consume();
        if (isOntologyTerm.isSelected()){
            String label = ontoTermField.getText().trim();
            String id = ontoTermMap.get(label);
            frequency = new Frequency.Builder()
                    .approximate(new OntoTerm(id, label))
                    .build();
        } else if(isFraction.isSelected()){
            double nominator = Double.parseDouble(fractionNomField.getText().trim());
            double denominator = Double.parseDouble(fractionDenomField.getText().trim());
            frequency = new Frequency.Builder()
                    .fraction(new base.Fraction(nominator, denominator))
                    .build();
        }

        dirty = true;

        autoCompletionBinding.dispose();
        signalConsumer.accept(Signal.DONE);

    }

    public model.Frequency updatedFrequency(){
        return this.frequency;
    }

    public boolean isUpdated(){
        return this.dirty;
    }

}
