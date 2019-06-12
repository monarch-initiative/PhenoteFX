package org.monarchinitiative.phenotefx.gui.newCommonDisease;

import base.OntoTerm;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.monarchinitiative.phenotefx.service.Resources;

import javax.validation.constraints.Null;
import java.util.HashMap;
import java.util.Map;


public class NewCommonDiseaseFactory {

    //map from disease name to id
    private Map<String, String> diseaseMap;
    private boolean newDiseaseSpecified;
    private OntoTerm newDisease;

    public NewCommonDiseaseFactory(@Nullable Map<String, String> diseaseMap){
        this.diseaseMap = new HashMap<>();
        if (diseaseMap != null){
            this.diseaseMap = new HashMap<>(diseaseMap);
        }
    }

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
        presenter.setDiseaseMap(this.diseaseMap);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    newDiseaseSpecified = presenter.isUpdated();
                    newDisease = presenter.updated();
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
