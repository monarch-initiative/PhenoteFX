package org.monarchinitiative.phenotefx.gui;

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

import com.google.common.collect.ImmutableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

public class PercentageFinder {

    private static final String EMPTY_STRING="";


    public PercentageFinder(){
        String result = getStringFromUser();
        result = result.replaceAll("%",EMPTY_STRING);
        Double perc;
        try {
            perc = Double.parseDouble(result);
        } catch (NumberFormatException e) {
            return;
        }
        showCandidates(perc);
    }


    /**
     * Request a String from user.
     *
     * @return String with user input
     */
    private String getStringFromUser() {
        String windowTitle="Enter percentage";
        String promptText="Percentage";
        String labelText="Enter percentage";
        TextInputDialog dialog = new TextInputDialog(promptText);
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(null);
        dialog.setContentText(labelText);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(EMPTY_STRING);

    }


    private void showCandidates(double perc) {
        ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
        try {
            //ClassLoader classLoader = getClass().getClassLoader();
            /* File file = new File(classLoader.getResource("data/percentages.txt").getFile()); */
            InputStream inputStream = PercentageFinder.class.getResourceAsStream("/data/percentages.txt");
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(streamReader);
           // BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line=br.readLine())!=null) {
                String A[] = line.split("\\s+");
                if (A.length<3) {
                    PopUps.showInfoMessage("Malformed line " + line,"Error reading percentages.txt");
                    return;
                }
                Double d = Double.parseDouble(A[0]);
                if (Math.abs(d-perc)<3) {
                    String s = String.format("%.2f = %s/%s",d,A[1],A[2]);
                    builder.add(s);
                }

            }
        } catch (IOException e) {
            PopUps.showException("Could not find percentages file","Error","Could not find percentages file",e);
        }

        ListView<String> listView = new ListView();

        listView.getItems().addAll(builder.build());


        HBox hbox = new HBox(listView);

        Scene scene = new Scene(hbox, 250, 300);


        Stage stage = new Stage();
        stage.setTitle("Candidate fractions");
        stage.setScene(scene);
        stage.show();

    }



}
