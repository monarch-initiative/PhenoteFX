package org.monarchinitiative.phenotefx.gui.prevalencepopup;

import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Prevalence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrevalenceFactory {

    private List<Prevalence> prevalences;
    private List<Prevalence> clone;
    private Map<String, String> candidateTermName2Id;
    private String curatorId;
    private boolean isUpdated;

    public PrevalenceFactory(@Nullable List<Prevalence> prevalences, @Nullable Map<String, String> prevalenceTermName2Id, @NotNull String curatorId) {
        clone = new ArrayList<>();
        if (prevalences != null) {
            for (Prevalence prevalence : prevalences){
                clone.add(new Prevalence(prevalence));
            }
        }
        this.candidateTermName2Id = new HashMap<>();
        if (prevalenceTermName2Id != null){
            this.candidateTermName2Id = new HashMap<>(prevalenceTermName2Id);
        }
        this.curatorId = curatorId;
    }

    //true if use clicked confirm and there are changes to prevalences or incidences
    public boolean openDiag() {

        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Prevalences";
        window.setTitle(windowTitle);
        PrevalenceView view = new PrevalenceView();
        PrevalencePresenter presenter = (PrevalencePresenter) view.getPresenter();
        //presenter.setDialogStage(window);
        presenter.setCuratorId(curatorId);
        presenter.setCurrentPrevalences(clone);
        presenter.setCandidateTerms(this.candidateTermName2Id);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    //check
                    isUpdated = presenter.prevalenceDirty();
                    clone = presenter.updatedPrevalences();
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
        return this.clone;
    }


}
