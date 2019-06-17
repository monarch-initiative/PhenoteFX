package org.monarchinitiative.phenotefx.gui.sigmoidchart;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SigmoidChartFactory {

    private boolean isUpdated;

    public boolean openDiag() {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Adjust time course";
        window.setTitle(windowTitle);
        SigmoidChartView view = new SigmoidChartView();
        SigmoidChartPresenter presenter = (SigmoidChartPresenter) view.getPresenter();

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    isUpdated = presenter.isUpdated();
                    //clone = presenter.updatedFrequency();
                    window.close();
                    break;
                case CANCEL:
                    window.close();
                    break;
                case FAILED:
                    throw new IllegalArgumentException(String.format("Illegal signal %s received.", signal));
            }
        });

        window.setScene(new Scene(view.getView()));
        window.showAndWait();

        return this.isUpdated;
    }
}
