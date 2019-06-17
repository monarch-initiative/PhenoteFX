package org.monarchinitiative.phenotefx.gui.frequency;

import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Frequency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.monarchinitiative.phenotefx.gui.sigmoidchart.SigmoidChartFactory;

import java.util.Map;

public class FrequencyFactory {

    private boolean isUpdated;
    private model.Frequency clone;
    private model.Frequency current;
    private Map<String, String> candidateOntoTermName2Id;

    public FrequencyFactory(@Nullable model.Frequency current, @NotNull Map<String, String> candidateName2IdMap) {
        this.current = current;
        if (this.current != null) {
            this.clone = new Frequency(current);
        }
        this.candidateOntoTermName2Id = candidateName2IdMap;
    }

    public boolean showDiag() {

        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Specify frequency";
        window.setTitle(windowTitle);
        FrequencyView view = new FrequencyView();
        FrequencyPresenter presenter = (FrequencyPresenter) view.getPresenter();
        presenter.setCurrent(current);
        presenter.setOntoTermMap(candidateOntoTermName2Id);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    isUpdated = presenter.isUpdated();
                    clone = presenter.updatedFrequency();
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

        return this.isUpdated;
    }

    public model.Frequency getUpdated() {
        return this.clone;
    }


}
