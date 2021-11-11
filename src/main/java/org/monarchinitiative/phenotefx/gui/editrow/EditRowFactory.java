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


import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.model.PhenoRow;

/**
 * This class sets up the dialogs that appear and allow the user to update specific fields of an annotation.
 */
public class EditRowFactory {

    public static String showFrequencyEditDialog(PhenoRow phenorow) {
        String windowTitle = "Edit current frequency";
        String label="frequency";
        String currentFrequency=phenorow.getFrequency();
        return showDialog(currentFrequency,windowTitle, label);
    }

    /**
     * Show a little dialog with a text area to enter items such as "PMID:123". When we
     * copy this information from PubMed, there typically are a few whitespace characters
     * between PMID: and the number, and so we automatically remove all whitespace.
     * @param phenorow
     * @param primaryStage
     * @return
     */
    public static String showPublicationEditDialog(PhenoRow phenorow, Stage primaryStage) {
        String windowTitle = "Edit current publication";
        String label = "publication";
        String currentPub = phenorow.getPublication();
        return showDialog(currentPub,windowTitle,label,true);
    }


    public static String showDescriptionEditDialog(PhenoRow phenorow, Stage primaryStage) {
        String windowTitle = "Edit current description";
        String label="description";
        String currentDescription=phenorow.getDescription();
        return showDialog(currentDescription,windowTitle,label);
    }


    /**
     * @param initialText The initial text that will appear in text field (current value of corresponding field in annotation; may be null).
     * @param windowTitle Title of the dialog
     * @param label label of the text field
     * @return value entered by user
     */
    private static String showDialog(String initialText, String windowTitle, String label, boolean removeWhitespace) {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        window.setTitle(windowTitle);


        window.showAndWait();
//        if (presenter.isOkClicked() )
//            return presenter.getText();
//        else
            return null;
    }

    private static String showDialog(String initialText, String windowTitle, String label) {
        return showDialog(initialText,windowTitle,label,false);
    }

}
