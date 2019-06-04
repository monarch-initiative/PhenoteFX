package org.monarchinitiative.phenotefx.gui.incidencepopup;

import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Incidence;
import model.Prevalence;
import org.monarchinitiative.phenotefx.gui.riskfactorpopup.RiskFactorView;

import java.util.List;

public class IncidenceFactory {

    List<Prevalence> existingPrevalences;
    List<Incidence> existingIncidences;
    List<Prevalence> updatedPrevalences;
    List<Incidence> updatedIncidences;
    String curatorId;

    public IncidenceFactory(List<Prevalence> prevalences, List<Incidence> incidences, String curatorId) {
        this.existingPrevalences = prevalences;
        this.existingIncidences = incidences;
        this.curatorId = curatorId;
    }

    //true if use clicked confirm and there are changes to prevalences or incidences
    public boolean openDiag() {

        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Prevalences and Incidences";
        window.setTitle(windowTitle);
        RiskFactorView view = new RiskFactorView();
        IncidencePresenter presenter = (IncidencePresenter) view.getPresenter();
        presenter.setDialogStage(window);
        presenter.setCuratorId(curatorId);
        presenter.setCurrentPrevalences(existingPrevalences);
        presenter.setCurrentIncidences(existingIncidences);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    //check
                    if (presenter.prevalenceDirty()) {
                        updatedIncidences = presenter.updatedIncidences();
                    }
                    if (presenter.incidenceDirty()) {
                        updatedPrevalences = presenter.updatedPrevalences();
                    }
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

        return updatedPrevalences != null || updatedIncidences != null;
    }

    public List<Prevalence> getPrevalences() {
        return updatedPrevalences;
    }

    public List<Incidence> getIncidences() {
        return updatedIncidences;
    }

}
