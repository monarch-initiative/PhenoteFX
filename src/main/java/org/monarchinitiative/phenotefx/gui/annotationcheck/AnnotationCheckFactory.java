package org.monarchinitiative.phenotefx.gui.annotationcheck;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.phenotefx.gui.editrow.EditRowPresenter;
import org.monarchinitiative.phenotefx.gui.editrow.EditRowView;
import org.monarchinitiative.phenotefx.model.PhenoRow;

/**
 * Convenience class to make a dialog appear that shows the new and old annotations for matching HPO ids.
 */
public class AnnotationCheckFactory {
    private static final Logger logger = LogManager.getLogger();
    private PhenoRow currentRow = null;

    private boolean updatedAnnotation = false;

    private Stage window;

    /**
     * @param oldrow       The initial text that will appear in text field (current value of corresponding field in annotation; may be null).
     * @param primaryStage Reference to main window
     * @param newrow       Title of the dialog
     * @return value entered by user
     */
    public PhenoRow showDialog(PhenoRow oldrow, PhenoRow newrow, Stage primaryStage) {
        updatedAnnotation = false;
        window = new Stage();
        window.setOnCloseRequest(event -> window.close());
        window.setTitle("Proposed annotation update");
        window.setScene(new Scene(createVbox(oldrow, newrow)));
        window.showAndWait();
        return currentRow;
    }

    public boolean updateAnnotation() {
        return updatedAnnotation;
    }


    private VBox createVbox(PhenoRow oldrow, PhenoRow newrow) {
        VBox vbox = new VBox();
        vbox.setMinWidth(1000);
        vbox.setMinHeight(700);
        Label label = new Label(String.format("Suggested new annotation for %s", oldrow.getPhenotypeName()));
        label.getStyleClass().add("header");
        label.setStyle("-fx-padding: 10px;\n" +
                "    -fx-font-size: 20px;\n" +
                "    -fx-background-color: ORANGERED;");
        Separator separator = new Separator();
        GridPane gpane = new GridPane();
        gpane.getColumnConstraints().add(new ColumnConstraints(100)); // column 0 is 100 wide
        gpane.getColumnConstraints().add(new ColumnConstraints(400)); // column 1 is 400 wide
        gpane.getColumnConstraints().add(new ColumnConstraints(400)); // column 2 is 400 wide
        gpane.setStyle("-fx-padding: 5px;\n" +
                "    -fx-font-size: 14px; \n " +
                "    -fx-border-insets: 5px;\n" +
                "    -fx-background-insets: 5px;\n");

        Label itemLabel = new Label("Item");
        itemLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        gpane.add(itemLabel, 0, 0);
        Label currentLabel = new Label("Current");
        currentLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        gpane.add(currentLabel, 1, 0);
        Label newLabel = new Label("New");
        newLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        gpane.add(newLabel, 2, 0);
        gpane.add(new Label("Onset"), 0, 1);
        gpane.add(new Label(oldrow.getOnsetName()), 1, 1);
        gpane.add(new Label(newrow.getOnsetName()), 2, 1);
        gpane.add(new Label("Frequency"), 0, 2);
        gpane.add(new Label(oldrow.getFrequency()), 1, 2);
        gpane.add(new Label(newrow.getFrequency()), 2, 2);
        gpane.add(new Label("Sex"), 0, 3);
        gpane.add(new Label(oldrow.getSex()), 1, 3);
        gpane.add(new Label(newrow.getSex()), 2, 3);
        gpane.add(new Label("Negation"), 0, 4);
        gpane.add(new Label(oldrow.getNegation()), 1, 4);
        gpane.add(new Label(newrow.getNegation()), 2, 4);
        gpane.add(new Label("Modifier"), 0, 5);
        gpane.add(new Label(oldrow.getModifier()), 1, 5);
        gpane.add(new Label(newrow.getModifier()), 2, 5);
        gpane.add(new Label("Description"), 0, 6);
        gpane.add(new Label(oldrow.getDescription()), 1, 6);
        gpane.add(new Label(newrow.getDescription()), 2, 6);
        gpane.add(new Label("Citation"), 0, 7);
        gpane.add(new Label(oldrow.getPublication()), 1, 7);
        gpane.add(new Label(newrow.getPublication()), 2, 7);
        gpane.add(new Label("Evidence"), 0, 8);
        gpane.add(new Label(oldrow.getEvidence()), 1, 8);
        gpane.add(new Label(newrow.getEvidence()), 2, 8);
        gpane.add(new Label("Assigned by"), 0, 9);
        gpane.add(new Label(oldrow.getAssignedBy()), 1, 9);
        gpane.add(new Label(newrow.getAssignedBy()), 2, 9);
        gpane.add(new Label("Date created"), 0, 10);
        gpane.add(new Label(oldrow.getDateCreated()), 1, 10);
        gpane.add(new Label(newrow.getDateCreated()), 2, 10);

        HBox buttonBox = new HBox();
        buttonBox.setStyle(" -fx-padding: 5px; \n" +
                "-fx-border-insets: 5px;\n" +
                "-fx-background-insets: 5px; ");

        Button keepOldButton = new Button("Keep old annotation");
        keepOldButton.setOnAction((event -> {
            currentRow = oldrow;
            updatedAnnotation = false;
            window.close();
        }));
        Button takeNewButton = new Button("Take new annotation");
        takeNewButton.setOnAction((event -> {
            currentRow = newrow;
            currentRow.setAssignedBy(oldrow.getAssignedBy());
            currentRow.setDateCreated(oldrow.getDateCreated());
            logger.trace("Setting new annotation to " + currentRow.toString());
            updatedAnnotation = true;
            window.close();
        }));

        Button takeNewButton2 = new Button("Take new annotation and update assigned By");
        takeNewButton2.setOnAction((event -> {
            currentRow = newrow;
            // just keep the date created, but used the assigned by fromthe new row
            currentRow.setDateCreated(oldrow.getDateCreated());
            updatedAnnotation = true;
            window.close();
        }));

        buttonBox.getChildren().addAll(keepOldButton, takeNewButton, takeNewButton2);
        buttonBox.setSpacing(10);
        vbox.setSpacing(10);
        vbox.getChildren().addAll(label, separator, gpane, buttonBox);

        return vbox;
    }


}
