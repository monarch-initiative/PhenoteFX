package org.monarchinitiative.phenotefx.gui.newitem;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.model.PhenoRow;

public class NewItemFactory {

    private static NewItemPresenter presenter;

    private static PhenoRow prow;

    private static String biocurator;

    private static String createdOn;

    public NewItemFactory(){

    }

    /**
     *
     * @return true if the user clicks OK and has generated a PhenoRow
     */
    public boolean showDialog() {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Data for new disease entry";
        window.setTitle(windowTitle);

        NewItemView view = new NewItemView();
        presenter = (NewItemPresenter) view.getPresenter();
        presenter.setDialogStage(window);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    window.close();
                    break;
                case CANCEL:
                case FAILED:
                    throw new IllegalArgumentException(String.format("Illegal signal %s received.", signal));
            }
        });

        window.setScene(new Scene(view.getView()));
        window.showAndWait();
        if (presenter.isOkClicked() ) {
            prow  = presenter.getPhenoRow();
            return true;
        }  else {
            return false;
        }
    }


    public void setBiocurator(String curator, String date) {
        biocurator=curator;
        createdOn=date;
    }

    public PhenoRow getProw() {
        if (biocurator!=null && createdOn!=null) {
            prow.setAssignedBy(biocurator);
            prow.setDateCreated(createdOn);
        }
        return prow;
    }
}
