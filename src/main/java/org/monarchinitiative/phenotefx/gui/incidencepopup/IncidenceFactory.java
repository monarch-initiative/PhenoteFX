package org.monarchinitiative.phenotefx.gui.incidencepopup;

import base.OntoTerm;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Incidence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IncidenceFactory {

    private List<Incidence> clone;

    private boolean isUpdated;

    private Set<OntoTerm> incidenceTerms;

    private String curatorId;

    //require incidence terms because the value could be a term
    public IncidenceFactory(@Nullable List<model.Incidence> current,
                            @NotNull Collection<OntoTerm> incidenceTerms,
                            @NotNull String curatorId){
        if (current != null) {
            clone = new ArrayList<>();
            for (model.Incidence incidence : current){
                clone.add(new model.Incidence(incidence));
            }
        }
        this.incidenceTerms = new HashSet<>(incidenceTerms);
        this.curatorId = curatorId;
    }

    public boolean openDiag() {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Incidences";
        window.setTitle(windowTitle);
        IncidenceView view = new IncidenceView();
        IncidencePresenter presenter = (IncidencePresenter) view.getPresenter();
        presenter.setCurrent(this.clone);
        presenter.setIncidenceTerms(this.incidenceTerms);
        presenter.setCuratorId(this.curatorId);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    isUpdated = presenter.isUpdated();
                    this.clone = presenter.updated();
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

    public boolean isUpdated(){
        return this.isUpdated;
    }

    public List<model.Incidence> updated(){
        return this.clone;
    }

}
