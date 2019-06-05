package org.monarchinitiative.phenotefx.gui.evidencepopup;

import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Evidence;
import org.jetbrains.annotations.Nullable;

public class EvidenceFactory {

    private Evidence evidence;
    private boolean updated;

    public EvidenceFactory(@Nullable Evidence evidence) {}

    public boolean openDiag() {

        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Set Evidence";
        window.setTitle(windowTitle);
        EvidenceView view = new EvidenceView();
        EvidencePresenter presenter = (EvidencePresenter) view.getPresenter();
        presenter.setCurrent(evidence);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    updated = true;
                    evidence = presenter.getEvidence();
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

        return this.updated;
    }

    public Evidence getEvidence(){
        return evidence;
    }

}
