package org.monarchinitiative.phenotefx.gui.effectsizepopup;

import javafx.scene.Scene;
import javafx.stage.Stage;
import model.TimeAwareEffectSize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TimeAwareEffectSizeFactory {


    private List<TimeAwareEffectSize> clone;
    private boolean isUpdated;
    private String curator;
    private String riskName;

    public TimeAwareEffectSizeFactory(@Nullable Collection<TimeAwareEffectSize> currentEZ,
                                        @NotNull String curator,
                                      @NotNull String riskName){
        if (currentEZ != null){
            clone = new ArrayList<>();
            for (TimeAwareEffectSize ez : currentEZ){
                clone.add(new TimeAwareEffectSize(ez));
            }
        }
        this.curator = curator;
        this.riskName = riskName;
    }

    public boolean openDiag() {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Effect Size";
        window.setTitle(windowTitle);
        TimeAwareEffectSizeView view = new TimeAwareEffectSizeView();
        TimeAwareEffectSizePresenter presenter = (TimeAwareEffectSizePresenter) view.getPresenter();

        presenter.setCurrent(clone);
        presenter.setCurator(curator);
        presenter.setWindowTitle(riskName);

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

    public List<TimeAwareEffectSize> updated(){
        return this.clone;
    }

}
