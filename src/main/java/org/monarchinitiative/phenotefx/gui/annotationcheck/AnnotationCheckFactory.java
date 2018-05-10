package org.monarchinitiative.phenotefx.gui.annotationcheck;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.gui.editrow.EditRowPresenter;
import org.monarchinitiative.phenotefx.gui.editrow.EditRowView;
import org.monarchinitiative.phenotefx.model.PhenoRow;

public class AnnotationCheckFactory {

    private static AnnotationCheckPresenter presenter;



    /**
     * @param oldrow The initial text that will appear in text field (current value of corresponding field in annotation; may be null).
     * @param primaryStage Reference to main window
     * @param newrow Title of the dialog
     * @return value entered by user
     */
    private static String showDialog(PhenoRow oldrow, PhenoRow newrow, Stage primaryStage) {
        Stage window;

        window = new Stage();
        window.setOnCloseRequest( event -> {window.close();} );
        window.setTitle("Proposed annotation update");
        presenter = new AnnotationCheckPresenter(oldrow,newrow);

        AnnotationCheckView view = new AnnotationCheckView();
        presenter = (AnnotationCheckPresenter) view.getPresenter();
//        presenter.setInitialText(initialText);
//        presenter.setLabel(label);
//        presenter.setDialogStage(window);
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
//        if (presenter.isOkClicked() )
//            return presenter.getText();
//        else
            return null;
    }




}
