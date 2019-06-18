package org.monarchinitiative.phenotefx.gui.sigmoidchart;

import javafx.application.Application;
import javafx.stage.Stage;
import model.TimeAwareEffectSize;

public class Test extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        SigmoidChartFactory factory = new SigmoidChartFactory(6.0, 1.0, TimeAwareEffectSize.TrendType.ASCEND);
        boolean isUpdated = factory.openDiag();
        if (isUpdated){
            System.out.println(String.format("type: %s; center: %.1f; steep: %.1f", factory.getType(), factory.getCenter(), factory.getSteep()));
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
