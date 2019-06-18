package org.monarchinitiative.phenotefx.gui.sigmoidchart;


import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.util.converter.NumberStringConverter;
import model.TimeAwareEffectSize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;


public class SigmoidChartPresenter {

    private static Logger logger = LoggerFactory.getLogger(SigmoidChartFactory.class);

    @FXML
    private HBox lineChartBox;

    @FXML
    private Slider centerSlider;

    @FXML
    private Slider steepSlider;

    @FXML
    private TextField centerField;

    @FXML
    private TextField steepField;

    private Consumer<Signal> signals;

    private boolean isUpdated;

    private SimpleDoubleProperty observableCenterValue = new SimpleDoubleProperty();
    private SimpleDoubleProperty observableSteepValue = new SimpleDoubleProperty();
    private TimeAwareEffectSize.TrendType curveType = TimeAwareEffectSize.TrendType.BELL;


    private ObservableList<XYChart.Data<Number, Number>> tuplelist = FXCollections.observableArrayList();

    @FXML
    void initialize(){
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        centerSlider.valueProperty().bindBidirectional(observableCenterValue);
        steepSlider.valueProperty().bindBidirectional(observableSteepValue);
        centerField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        steepField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        observableCenterValue.addListener((observable, oldValue, newValue) -> {

            if (observable != null){
                centerField.setText(String.format("%.1f", newValue.floatValue()));
                refresh();
            }

        });
        observableSteepValue.addListener((obj, oldvalue, newvalue) -> {
            if (obj !=null) {
                steepField.setText(String.format("%.1f", newvalue.floatValue()));
                refresh();
            }
        });
        centerField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (observable != null && newValue != null){
                double value = 0.0;
                try {
                    value = Double.parseDouble(newValue);
                    observableCenterValue.setValue(value);
                } catch (Exception e){
                    //do nothing
                }
            }
        });
        steepField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (observable != null && newValue != null){
                double value = 0.0;
                try {
                    value = Double.parseDouble(newValue);
                    observableSteepValue.setValue(value);
                } catch (Exception e){
                    //do nothing
                }
            }
        });

        centerSlider.setMin(0);
        centerSlider.setMax(10);
        steepSlider.setMin(0.01);
        steepSlider.setMax(10);
        observableCenterValue.setValue(5);
        observableSteepValue.setValue(5);

        series.setData(tuplelist);
        NumberAxis x_axis = new NumberAxis();
        NumberAxis y_axis = new NumberAxis();
        x_axis.setLabel("year");
        y_axis.setLabel("relative to max");
        y_axis.setAutoRanging(false);
        y_axis.setLowerBound(0.0);
        y_axis.setUpperBound(1.0);
        y_axis.setTickUnit(0.2);

        LineChart<Number, Number> lineChart = new LineChart<Number, Number>(x_axis, y_axis);
        lineChartBox.getChildren().add(lineChart);
        lineChart.getData().add(series);
        lineChart.setMinHeight(400);

        refresh();
    }

    private void refresh() {

        double shift = centerSlider.getValue();
        double steep = steepSlider.getValue();
        logger.info("shift: " + shift);
        logger.info("steep: " + steep);

        tuplelist.clear();
        double [] x = x_series(0, 10, 0.1);
        for (int i = 0; i < x.length; i++){
            double y = sigmoid(x[i], shift, steep, this.curveType);
            tuplelist.add(new XYChart.Data<>(x[i], y));
        }

    }

    public void setSignal(Consumer<Signal> signals){
        this.signals = signals;
    }

    public void setCenterValue(@Nullable Double centerValue){
        if (centerValue != null){
            this.observableCenterValue.setValue(centerValue);
        }
    }

    public void setSteepValue(@Nullable Double steepValue){
        if (steepValue != null){
            this.observableSteepValue.setValue(steepValue);
        }
    }

    public void setCurveType(@NotNull TimeAwareEffectSize.TrendType type){
        this.curveType = type;
        refresh();
    }

    @FXML
    private void cancelClicked(ActionEvent event) {
        event.consume();
        signals.accept(Signal.CANCEL);
    }

    @FXML
    private void confirmClicked(ActionEvent event) {
        event.consume();
        this.isUpdated = true;
        signals.accept(Signal.DONE);
    }

    public boolean isUpdated() {
        return this.isUpdated;
    }

    private double sigmoid(double x, double shift, double steep, TimeAwareEffectSize.TrendType type){
        switch (type){
            case ASCEND:
                return sigmoid_ascend(x, shift, steep);
            case DESCEND:
                return sigmoid_descend(x, shift, steep);
            case BELL:
                return sigmoid_bell(x, shift, steep);
            default:
                return 0.0;
        }
    }

    double sigmoid_ascend(double x, double shift, double steep){
        return 1.0 / (1.0 + Math.exp(steep * (- x + shift)));
    }

    double sigmoid_descend(double x, double shift, double steep){
        return 1.0 / (1.0 + Math.exp(steep * (x - shift)));
    }

    double sigmoid_bell(double x, double shift, double steep){
        return 1.0 / (1.0 + Math.exp(Math.abs((shift - x) * steep) - 4));
    }


    public double getCenter() {

        return observableCenterValue.get();
    }

    public double getSteep() {
        return observableSteepValue.get();
    }

    private double[] x_series(double left, double right, double step) {
        int SIZE = (int) Math.floor((right - left) / step) + 1;
        double [] series = new double[SIZE];
        for (int i = 0; i < SIZE; i++){
            series[i] = left + i * step;
        }
        return series;
    }
}
