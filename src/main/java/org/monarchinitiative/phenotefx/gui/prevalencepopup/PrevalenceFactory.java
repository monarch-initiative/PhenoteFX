package org.monarchinitiative.phenotefx.gui.prevalencepopup;

import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Prevalence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PrevalenceFactory {

    private List<Prevalence> prevalences;
    private String curatorId;
    private boolean isUpdated;

    public PrevalenceFactory(@Nullable List<Prevalence> prevalences, @NotNull String curatorId) {
        this.prevalences = prevalences;
        this.curatorId = curatorId;
    }

    //true if use clicked confirm and there are changes to prevalences or incidences
    public boolean openDiag() {

        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Prevalences and Incidences";
        window.setTitle(windowTitle);
        PrevalenceView view = new PrevalenceView();
        PrevalencePresenter presenter = (PrevalencePresenter) view.getPresenter();
        //presenter.setDialogStage(window);
        presenter.setCuratorId(curatorId);
        presenter.setCurrentPrevalences(prevalences);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    //check
                    isUpdated = presenter.prevalenceDirty();
                    prevalences = presenter.updatedPrevalences();
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

    public List<Prevalence> getPrevalences() {
        return this.prevalences;
    }


}
