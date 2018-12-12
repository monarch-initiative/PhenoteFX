package org.monarchinitiative.phenotefx.gui.riskfactorpopup;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class RiskFactorFactory {

    public RiskFactorFactory() {

    }

    public boolean showDialog() {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Risk Factors";
        window.setTitle(windowTitle);

        RiskFactorView view = new RiskFactorView();
        RiskFactorPresenter presenter = (RiskFactorPresenter) view.getPresenter();
        presenter.setDialogStage(window);

//        presenter.setSignal(signal -> {
//            switch (signal) {
//                case DONE:
//                    window.close();
//                    break;
//                case CANCEL:
//                case FAILED:
//                    throw new IllegalArgumentException(String.format("Illegal signal %s received.", signal));
//            }
//        });

        window.setScene(new Scene(view.getView()));
        window.showAndWait();
        return true;
//        if (presenter.isOkClicked() ) {
//            prow  = presenter.getPhenoRow();
//            return true;
//        }  else {
//            return false;
//        }
    }
}
