package org.monarchinitiative.phenotefx.gui.incidencepopup;

import base.OntoTerm;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Incidence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class IncidenceFactory {

    private model.Incidence current;

    private boolean isUpdated;

    private model.Incidence clone;

    private Set<OntoTerm> incidenceTerms;

    //require incidence terms because the value could be a term
    public IncidenceFactory(@Nullable model.Incidence current, @NotNull Collection<OntoTerm> incidenceTerms){
        this.current = current;
        this.clone = new Incidence.Builder().build();
        if (this.current != null){
            this.clone.setValue(this.current.getValue());
            this.clone.setEvidence(this.current.getEvidence());
            this.clone.setCurationMeta(this.current.getCurationMeta());
        }
        this.incidenceTerms = new HashSet<>(incidenceTerms);
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

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    isUpdated = presenter.updated();
                    this.clone = presenter.updatedIncidence();
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

    public model.Incidence updated(){
        return this.clone;
    }

}
