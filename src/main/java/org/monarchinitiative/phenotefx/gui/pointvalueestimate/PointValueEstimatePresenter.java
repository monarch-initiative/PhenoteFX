package org.monarchinitiative.phenotefx.gui.pointvalueestimate;

import base.OntoTerm;
import base.PointValueEstimate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.monarchinitiative.phenotefx.gui.Signal;

import java.util.function.Consumer;

public class PointValueEstimatePresenter {

    @FXML
    private TextField meanField;

    @FXML
    private RadioButton hasSD;

    @FXML
    private RadioButton hasCI95;

    @FXML
    private TextField sdtevField;

    @FXML
    private TextField ci_leftField;

    @FXML
    private TextField ci_rightField;

    @FXML
    private TextField unit_left_field;

    @FXML
    private TextField unit_right_field;

    private Consumer<Signal> signalConsumer;

    private PointValueEstimate beingEditted;

    private boolean isUpdated;

    public void setCurrentValue(PointValueEstimate current){
        beingEditted = current;
        refresh();
    }

    public void setSignals(Consumer<Signal> signals){
        this.signalConsumer = signals;
    }

    @FXML
    void initialize() {

        sdtevField.setDisable(true);
        ci_leftField.setDisable(true);
        ci_rightField.setDisable(true);

        hasSD.selectedProperty().addListener((obj, oldValue, newValue) -> {
            if (obj != null){
                if (newValue){
                    hasCI95.setDisable(true);
                    sdtevField.setDisable(false);
                } else {
                    hasCI95.setDisable(false);
                    sdtevField.setDisable(true);
                }
            }
        });

        hasCI95.selectedProperty().addListener((obj, oldValue, newValue) -> {
            if (obj != null){
                if (newValue){
                    hasSD.setDisable(true);
                    ci_leftField.setDisable(false);
                    ci_rightField.setDisable(false);
                } else {
                    hasSD.setDisable(false);
                    ci_leftField.setDisable(true);
                    ci_rightField.setDisable(true);
                }
            }
        });
    }

    private void refresh(){
        if (beingEditted != null){
            meanField.setText(Double.toString(beingEditted.getMean()));
            Double std = beingEditted.getStdev();
            sdtevField.setText(std == null ? "" : Double.toString(std));
            PointValueEstimate.CI95 ci95 = beingEditted.getCi95();
            if (ci95 != null){
                ci_leftField.setText(Double.toString(ci95.getLeft()));
                ci_rightField.setText(Double.toString(ci95.getRight()));
            }
            OntoTerm unit = beingEditted.getUnit();
            if (unit != null){
                unit_left_field.setText(unit.getLabel());
                unit_right_field.setText(unit.getId());
            }
        }
    }

    @FXML
    void cancelClicked(ActionEvent event) {
        event.consume();
        signalConsumer.accept(Signal.CANCEL);

    }

    @FXML
    void confirmClicked(ActionEvent event) {
        event.consume();
        isUpdated = true;

        double mean = Double.parseDouble(meanField.getText().trim());
        Double sd = null;
        PointValueEstimate.CI95 ci95 = null;
        if (hasSD.isSelected()){
            sd = Double.parseDouble(sdtevField.getText().trim());
        } else if (hasCI95.isSelected()){
            Double ci95_l = Double.parseDouble(ci_leftField.getText().trim());
            Double ci95_r = Double.parseDouble(ci_rightField.getText().trim());
            ci95 = new PointValueEstimate.CI95(ci95_l, ci95_r);
        }
        OntoTerm unit = null;
        String label = unit_left_field.getText().trim();
        String id = unit_right_field.getText().trim();
        if (!label.isEmpty() && !id.isEmpty()){
            unit = new OntoTerm(id, label);
        }
        beingEditted = new PointValueEstimate.Builder()
                .mean(mean)
                .stdev(sd)
                .ci95(ci95)
                .unit(unit)
                .build();
        signalConsumer.accept(Signal.DONE);
    }

    private double doubleValueOf(String text){
        return 0.0;
    }

    public boolean isUpdated() {
        return this.isUpdated;
    }

    public PointValueEstimate updated() {
        return beingEditted;
    }
}
