package org.monarchinitiative.phenotefx.gui.sigmoidchart;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;


public class SigmoidChartPresenter {

    private static Logger logger = LoggerFactory.getLogger(SigmoidChartFactory.class);

//    @FXML
//    private LineChart<Number, Double> chartView;
    @FXML
    private HBox lineChartBox;

    @FXML
    private Slider centerSlider;

    @FXML
    private Slider steepSlider;

    @FXML
    private TextField param1Field;

    @FXML
    private TextField param2Field;

    private Consumer<Signal> signals;

    private boolean isUpdated;


    private ObservableList<XYChart.Data<Number, Number>> tuplelist = FXCollections.observableArrayList();

    @FXML
    void initialize(){
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        centerSlider.setMin(-5);
        centerSlider.setMax(10);
        centerSlider.setValue(5);
        steepSlider.setMin(0.01);
        steepSlider.setMax(10);
        steepSlider.setValue(1);

        centerSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (observable != null && newValue != null){
                    refresh();
                    param1Field.setText(Double.toString(centerSlider.getValue()));
                }
            }
        });

        steepSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (observable != null && newValue != null){
                    refresh();
                    param2Field.setText(Double.toString(steepSlider.getValue()));
                }
            }
        });

        series.setData(tuplelist);
        NumberAxis x_axis = new NumberAxis();
        NumberAxis y_axis = new NumberAxis();

        LineChart<Number, Number> lineChart = new LineChart<Number, Number>(x_axis, y_axis);
        lineChartBox.getChildren().add(lineChart);
        lineChart.getData().add(series);
        lineChart.setPrefSize(400, 400);

        refresh();
    }

    private void refresh() {

        double shift = centerSlider.getValue();
        double steep = steepSlider.getValue();
        logger.info("shift: " + shift);
        logger.info("steep: " + steep);

        tuplelist.clear();
        double [] x = x_series(0, 10, 0.1);
        //double [] y = sigmoid(x, centerSlider.getValue(), center);
        for (int i = 0; i < x.length; i++){
            double y = sigmoid(x[i], shift, steep, "ascend");
            tuplelist.add(new XYChart.Data<>(x[i], y));
        }

    }

    public void setSignal(Consumer<Signal> signals){
        this.signals = signals;
    }

    @FXML
    private void cancelClicked(ActionEvent event) {
        event.consume();
    }

    @FXML
    private void confirmClicked(ActionEvent event) {
        event.consume();
        this.isUpdated = true;
    }

    public boolean isUpdated() {
        return this.isUpdated;
    }

    private double sigmoid(double x, double shift, double steep, String type){
        switch (type){
            case "ascend":
                return sigmoid_ascend(x, shift, steep);
            case "descend":
                return sigmoid_descend(x, shift, steep);
            case "bell":
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




//    private static double sigmoid(double x){
//        return Math.exp(x) / (Math.exp(x) + 1);
//    }
//
//    private static double sigmoid(double x, double shape, double shift){
//        return 1.0 / (1.0 + Math.exp(-(x - shift)));
//    }
//
//    private static double[] sigmoid(double[] x){
//        double[] y = new double[x.length];
//        for (int i = 0; i < x.length; i++){
//            y[i] = sigmoid(x[i]);
//        }
//        return y;
//    }
//
//    private static double[] sigmoid(double[] x, double shape, double shift){
//        double[] y = new double[x.length];
//        for (int i = 0; i < x.length; i++){
//            y[i] = sigmoid(x[i], shape, shift);
//        }
//        return y;
//    }
//
//    private static double sigmoid_inverse(double y) {
//        return Math.log(y) - Math.log(1 - y);
//    }
//
    private double[] x_series(double left, double right, double step) {
        int SIZE = (int) Math.floor((right - left) / step) + 1;
        double [] series = new double[SIZE];
        for (int i = 0; i < SIZE; i++){
            series[i] = left + i * step;
        }
        return series;
    }
}
