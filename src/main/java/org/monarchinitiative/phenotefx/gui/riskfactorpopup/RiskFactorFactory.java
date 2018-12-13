package org.monarchinitiative.phenotefx.gui.riskfactorpopup;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.service.Resources;

import java.net.URL;

public class RiskFactorFactory {

    private Resources resources;

    public RiskFactorFactory(Resources resources) {
        this.resources = resources;
    }

    public boolean showDialog() {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Risk Factors";
        window.setTitle(windowTitle);
        RiskFactorView view = new RiskFactorView();
        RiskFactorPresenter presenter = (RiskFactorPresenter) view.getPresenter();
        presenter.setResource(resources);
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
