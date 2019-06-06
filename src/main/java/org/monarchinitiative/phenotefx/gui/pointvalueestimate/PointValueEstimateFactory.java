package org.monarchinitiative.phenotefx.gui.pointvalueestimate;

import base.PointValueEstimate;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PointValueEstimateFactory {

    private PointValueEstimate clone;
    private boolean isUpdated;

    public PointValueEstimateFactory(PointValueEstimate current) {
        if (current != null){
            clone = new PointValueEstimate(current);
        }
    }

    public boolean openDiag() {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Prevalences and Incidences";
        window.setTitle(windowTitle);
        PointValueEstimateView view = new PointValueEstimateView();
        PointValueEstimatePresenter presenter = (PointValueEstimatePresenter) view.getPresenter();

        presenter.setCurrentValue(clone);

        presenter.setSignals(signal -> {
            switch (signal) {
                case DONE:
                    //check
                    isUpdated = presenter.isUpdated();
                    clone = presenter.updated();
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

        return isUpdated;
    }

    public PointValueEstimate updated() {
        return clone;
    }
}
