package org.monarchinitiative.phenotefx.gui.riskfactorpopup;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.service.Resources;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RiskFactorFactory {

    private Resources resources;

    public RiskFactorFactory(Resources resources) {
        this.resources = resources;
    }

    public List<RiskFactorPresenter.RiskFactorRow> showDialog() {
        //the list will hold riskfactor rows that are added by the user. It will not be null, but might be empty
        List<RiskFactorPresenter.RiskFactorRow> result = new ArrayList<>();

        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Risk Factors";
        window.setTitle(windowTitle);
        RiskFactorView view = new RiskFactorView();
        RiskFactorPresenter presenter = (RiskFactorPresenter) view.getPresenter();
        presenter.setResource(resources);
        presenter.setDialogStage(window);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    result.addAll(presenter.getConfirmed());
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

        return result;
    }
}
