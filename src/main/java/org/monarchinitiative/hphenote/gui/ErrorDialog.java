package org.monarchinitiative.hphenote.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This is a convenience class that creates windows to report errors of various kinds.
 * @author Peter Robinson
 * @version 0.1.2 (2017-11-09)
 */
public class ErrorDialog {

    public static void display(String title, String message) {

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        window.setWidth(Math.max(400,Region.USE_PREF_SIZE));
        Label label = new Label();
        label.setText(message);
        label.setStyle(
                "-fx-border-color: lightblue; "
                        + "-fx-font-size: 14;"
                        + "-fx-border-insets: -5; "
                        + "-fx-border-radius: 5;"
                        + "-fx-border-style: dotted;"
                        + "-fx-border-width: 2;"
                        + "-fx-alignment: top-left;"
                        + "-fx-text-fill: red;"
        );

        Button button = new Button("OK");

        button.setOnAction(e -> {
            e.consume();
            window.close();
        });


        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 50, 50, 50));

        layout.getChildren().addAll(label, button);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }


    public static void displayException(String title, String message, Exception e) {
        TextArea textArea = new TextArea(e.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        Label label = new Label("The exception stacktrace was:");


        textArea.setMinSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(title);
        alert.setContentText(message);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }



}
