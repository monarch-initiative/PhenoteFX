package org.monarchinitiative.phenotefx.gui.onsets;

import base.OntoTerm;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Onset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OnsetsFactory {

    private List<Onset> clone;
    private Map<String, String> onsetTermName2IdMap;
    private boolean isUpdated;
    private String curator;

    public OnsetsFactory(@Nullable Collection<Onset> currentOnsets,
                         @NotNull Collection<OntoTerm> onsetTerms,
                         @NotNull String curator){
        onsetTermName2IdMap = onsetTerms.stream().collect(Collectors.toMap(item -> item.getLabel(), item -> item.getId()));
        if (currentOnsets != null){
            clone = new ArrayList<>();
            for (Onset onset : currentOnsets){
                clone.add(new Onset(onset));
            }
        }
        this.curator = curator;
    }

    public boolean openDiag() {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Onsets";
        window.setTitle(windowTitle);
        OnsetsView view = new OnsetsView();
        OnsetsPresenter presenter = (OnsetsPresenter) view.getPresenter();

        presenter.setCurrent(clone);
        presenter.setCurator(curator);
        presenter.setTermMap(this.onsetTermName2IdMap);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    //check
                    isUpdated = presenter.isUpdated();
                    clone = presenter.updated();
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

    public List<Onset> updated(){
        return this.clone;
    }

}
