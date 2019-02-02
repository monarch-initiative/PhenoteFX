package org.monarchinitiative.phenotefx.gui.logviewer;

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


import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.monarchinitiative.phenotefx.gui.PopUps;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * Class to display a simple log viewer. The log is read from the log4j2 log file and displayed. The lines are colored
 * according to the level of the log item.
 */
public class LogViewerFactory {
    private String logpath;

    public LogViewerFactory() {
        this.logpath= Platform.getAbsoluteLogPath();
    }

    /**
     * Read lines from logfile.
     * Like this
     * [TRACE] 08-26-2017 07:25:47 [Thread-6] (ExtendedViewPointCreationTask.java:138) - Adding viewpoint ZMYM2 to list (size: 37)
     */
    public void display() {
        Log log = new Log();
        MyLogger mylogger = new MyLogger(log, "main");

        try{
            BufferedReader br = new BufferedReader(new FileReader(logpath));
            String line;
            while ((line=br.readLine())!=null) {
                int i = line.indexOf("]");
                if (i<0) continue; /* should never happen, each line starts with [INFO], [ERROR], etc. */
                String level = line.substring(1,i);
                i = line.indexOf("[",i);
                int j =line.indexOf("]",i);
                if (i<0 || j<0) continue; /* should never happen -- data is in square brackets */
                String date = line.substring(i+1,j);
                i=line.indexOf("(",j);
                j=line.indexOf(")",j);
                if (i<0 || j<0) continue; /* should never happen -- class/line is in square brackets */
                String context=line.substring(i+1,j);
                i=line.indexOf("-",j);
                String message=line.substring(i+2);
                Level lvl=Level.string2level(level);
                LogRecord record = new LogRecord(lvl,date,context,message);
                mylogger.log(record);
            }
            br.close();
        } catch (IOException e) {
            PopUps.showException  ("Error","Error opening logfile", "Could not open logfile",e);
            return;
        }

        LogView logView = new LogView(mylogger);
        logView.setPrefWidth(800);

        ChoiceBox<Level> filterLevelcb = new ChoiceBox<>(
                FXCollections.observableArrayList(
                        Level.values()
                )
        );
        filterLevelcb.getSelectionModel().select(Level.TRACE);
        logView.filterLevelProperty().bind(
                filterLevelcb.getSelectionModel().selectedItemProperty()
        );

        ToggleButton showTimestamp = new ToggleButton("Show Timestamp");
        logView.showTimeStampProperty().bind(showTimestamp.selectedProperty());

        ToggleButton showLocation = new ToggleButton("Show Location");
        logView.showLocationProperty().bind(showLocation.selectedProperty());

        ToggleButton tail = new ToggleButton("Tail");
        logView.tailProperty().bind(tail.selectedProperty());

        tail.setOnAction(e-> {if (tail.isSelected()) { tail.setStyle("-fx-background-color: #000000," +
                "linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);\n" +
                "    -fx-text-fill: white;"); } else {tail.setStyle("");} });

        HBox controls = new HBox(
                10,
                filterLevelcb,
                showTimestamp,
                showLocation,
                tail
              /* , rateLayout*/
        );
        controls.setMinHeight(HBox.USE_PREF_SIZE);

        VBox layout = new VBox(
                10,
                controls,
                logView
        );
        VBox.setVgrow(logView, Priority.ALWAYS);

        Scene scene = new Scene(layout);
        //scene.getStylesheets().add("css/logviewer.css" );
       /* scene.getStylesheets().add(
                LogViewerFactory.class.getResource("/css/logviewer.css").toExternalForm()
        );*/
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();


    }


}
