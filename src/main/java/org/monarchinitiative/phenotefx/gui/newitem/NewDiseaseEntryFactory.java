package org.monarchinitiative.phenotefx.gui.newitem;

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

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.monarchinitiative.phenotefx.model.DiseaseIdAndLabelPair;


import java.util.Optional;

public class NewDiseaseEntryFactory {


    public static Optional<DiseaseIdAndLabelPair> getDiseaseIdAndLabel() {
        Dialog<DiseaseIdAndLabelPair> dialog = new Dialog<>();
        dialog.setTitle("New disease entry");
        dialog.setHeaderText("Enter name and ID from OMIM");
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField diseaseId = new TextField();
        diseaseId.setPromptText("OMIM id (six digits)");
        TextField diseaseLabel = new TextField();
        diseaseLabel.setPromptText("OMIM disease name");
        grid.add(new Label("ID:"), 0, 0);
        grid.add(diseaseId, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(diseaseLabel, 1, 1);
        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);

        diseaseId.textProperty().addListener((observable, oldValue, newValue) -> okButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(diseaseId::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new DiseaseIdAndLabelPair(diseaseId.getText().trim(), diseaseLabel.getText().trim());
            }
            return null;
        });
        return dialog.showAndWait();
    }
}
