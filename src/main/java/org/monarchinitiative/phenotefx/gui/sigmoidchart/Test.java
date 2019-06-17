package org.monarchinitiative.phenotefx.gui.sigmoidchart;

import javafx.application.Application;
import javafx.stage.Stage;

public class Test extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        SigmoidChartFactory factory = new SigmoidChartFactory();
        factory.openDiag();
    }

    public static void main(String[] args){
        launch(args);
    }
}
