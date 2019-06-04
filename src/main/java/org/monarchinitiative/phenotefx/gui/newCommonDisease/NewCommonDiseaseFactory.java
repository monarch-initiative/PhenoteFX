package org.monarchinitiative.phenotefx.gui.newCommonDisease;

import base.OntoTerm;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.service.Resources;


public class NewCommonDiseaseFactory {

    private Resources resources;
    private boolean newDiseaseSpecified;
    private OntoTerm newDisease;
//
//    public NewCommonDiseaseFactory(Resources resources){
//        this.resources = resources;
//    }

    /**
     * Return the new disease annotation
     * @return
     */
    public boolean openDiag() {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Create new disease annotation";
        window.setTitle(windowTitle);

        NewCommonDiseaseView view = new NewCommonDiseaseView();
        NewCommonDiseasePresenter presenter = (NewCommonDiseasePresenter) view.getPresenter();
        presenter.setDialogStage(window);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    newDiseaseSpecified = true;
                    newDisease = presenter.newDisease();
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
        return newDiseaseSpecified;
    }

    public OntoTerm getNewDisease() {
        return newDisease;
    }
}
