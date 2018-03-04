package org.monarchinitiative.phenotefx.gui.editrow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.model.PhenoRow;

import java.io.IOException;

public class EditRowFactory {



    public static String showPersonEditDialog(PhenoRow phenorow, Stage primaryStage) {
        Stage window;
        String windowTitle = "Edit current publication";
        window = new Stage();
        window.setOnCloseRequest( event -> {window.close();} );
        window.setTitle(windowTitle);

        EditRowView view = new EditRowView();
        EditRowPresenter presenter = (EditRowPresenter) view.getPresenter();
        presenter.setPhenoRow(phenorow);
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
        if (presenter.isOkClicked() )
            return presenter.getNewPublication();
        else
            return null;
    }



}
