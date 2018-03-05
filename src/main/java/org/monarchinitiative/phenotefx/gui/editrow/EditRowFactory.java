package org.monarchinitiative.phenotefx.gui.editrow;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2018 Peter Robinson
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.model.PhenoRow;


public class EditRowFactory {

    private static EditRowPresenter presenter;

    public static String showFrequencyEditDialog(PhenoRow phenorow, Stage primaryStage) {
        String windowTitle = "Edit current frequency";
        String currentFrequency=phenorow.getFrequency();
        return showDialog(currentFrequency,primaryStage,windowTitle);
    }

    public static String showPublicationEditDialog(PhenoRow phenorow, Stage primaryStage) {
        String windowTitle = "Edit current publication";
        String currentPub = phenorow.getPub();
        return showDialog(currentPub,primaryStage,windowTitle);
    }


    public static String showDescriptionEditDialog(PhenoRow phenorow, Stage primaryStage) {
        String windowTitle = "Edit current description";
        String currentDescription=phenorow.getDescription();
        return showDialog(currentDescription,primaryStage,windowTitle);
    }



    private static String showDialog(String initialText, Stage primaryStage, String windowTitle) {
        Stage window;

        window = new Stage();
        window.setOnCloseRequest( event -> {window.close();} );
        window.setTitle(windowTitle);

        EditRowView view = new EditRowView();
        presenter = (EditRowPresenter) view.getPresenter();
        presenter.setInitialText(initialText);
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
            return presenter.getText();
        else
            return null;
    }




}
