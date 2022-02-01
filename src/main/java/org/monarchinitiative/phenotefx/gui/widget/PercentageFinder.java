package org.monarchinitiative.phenotefx.gui.widget;

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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PercentageFinder {
    private static final Logger LOG = LoggerFactory.getLogger(PercentageFinder.class);

    public static void show() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Percentage calculcator");

        window.setWidth(Math.max(400, Region.USE_PREF_SIZE));
        Label label1 = new Label();
        label1.setText("Enter percentage and cohort total");
        label1.setStyle(
                "-fx-border-color: lightblue; "
                        + "-fx-font-size: 14;"
                        + "-fx-border-insets: -5; "
                        + "-fx-border-radius: 5;"
                        + "-fx-border-style: dotted;"
                        + "-fx-border-width: 2;"
                        + "-fx-alignment: top-left;"
                        + "-fx-text-fill: red;"
        );

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> {
            e.consume();
            window.close();
        });
        Button calculate = new Button("Calculate");


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField cohortSizeTextField = new TextField();
        cohortSizeTextField.setPromptText("Cohort size");
        TextField percentageAffectedTextField = new TextField();
        percentageAffectedTextField.setPromptText("percentage affected");
        Label label = new Label("n/m:");
        Label resultLabel = new Label("");
        grid.add(new Label("Size:"), 0, 0);
        grid.add(cohortSizeTextField, 1, 0);
        grid.add(new Label("Percentage:"), 0, 1);
        grid.add(percentageAffectedTextField, 1, 1);
        grid.add(label, 0,2);
        grid.add(resultLabel, 1, 2);


        calculate.setOnAction(e ->{
            String guess =  getGuess(cohortSizeTextField.getText(), percentageAffectedTextField.getText());
            LOG.info(guess);
            resultLabel.setText(guess);
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 50, 50, 50));

        layout.getChildren().addAll(label1, grid, calculate, closeButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);

        window.setScene(scene);
        window.showAndWait();
    }

    private static String getGuess(String n, String perc) {
        Integer total;
        double percentage;
        try {
            total = Integer.parseInt(n.trim());
            percentage = Double.parseDouble(perc.replaceAll("%","").trim());
            int m = (int) Math.round(0.01*percentage * total);
            return String.format("%d/%d", m,total);
        } catch (Exception e) {
            return e.getMessage();
        }
    }






}
