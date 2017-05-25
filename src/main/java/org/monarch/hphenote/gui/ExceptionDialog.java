package org.monarch.hphenote.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Created by robinp on 5/25/17.
 */
public class ExceptionDialog {


    public static void display(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("File Input Error");
        alert.setHeaderText("File Input Error");
        alert.setContentText("An error occured while reading a Phenote input file");

        Label label = new Label("The following lines caused errors:");

        TextArea textArea = new TextArea(msg);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

// Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.setWidth(1200);
        alert.setHeight(800);
        alert.showAndWait();

    }



}
