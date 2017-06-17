package org.monarchinitiative.hphenote.biolark.configure;

/*
 * #%L
 * HPhenote
 * %%
 * Copyright (C) 2017 Peter Robinson
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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.monarchinitiative.hphenote.biolark.BioLark;
import org.monarchinitiative.hphenote.biolark.Signal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Presenter class that asks user to input
 * Created by Daniel Danis on 5/26/17.
 */
public class BiolarkConfigurePresenter implements Initializable {
    /** Address of REST service for BioLark */
    private static String server = "http://phenotyper.monarchinitiative.org:5678/cr/annotate";

    private BioLark result;

    private static AskServerTask task;

    /**
     * User will paste analyzed text here.
     */
    @FXML
    private TextArea contentTextArea;

    private String contentText;

    /**
     * Clicking this Button will start analysis.
     */
    @FXML
    private Button analyzeButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label statusLabel;

    /**
     * User inserts PMID of the publication from which the analyzed text is coming from.
     */
    @FXML
    private TextField pmidTextField;

    /**
     * This Consumer will be executed after analysis has been complete.
     */
    private Consumer<Signal> signal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // This binding allows to start the analysis only after that all required info has been entered.
        BooleanBinding allSet = Bindings.createBooleanBinding(() -> // analyzeButton will stay disabled if:
                        !pmidTextField.getText().matches("\\d{1,9}") || // PMID Text doesn't match this regex OR
                                contentTextArea.getText().equalsIgnoreCase(""), // contentTextArea is empty
                pmidTextField.textProperty(), contentTextArea.textProperty());
        analyzeButton.disableProperty().bind(allSet);
        /* the following removes the annoying spaces that NCBI puts
        * in front of the PMID when you copy it from the webpage. */
        pmidTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                String txt = pmidTextField.getText();
                pmidTextField.setText(txt.trim());
            }
        });
    }

    /**
     * Run after user clicks Analyze button. Connects
     */
    @FXML
    void analyzeButtonClicked() {
        /* Ask server to parse the text entered by user */
        statusLabel.setText("Asking server...");
        cancelButton.setDisable(false);
        String userInput = processUserInputText();
        task = new AskServerTask(userInput);
        task.setOnSucceeded(e -> {
            try {
                this.result = new BioLark(task.get());
                signal.accept(Signal.DONE);
            } catch (InterruptedException | ExecutionException ie) {
                signal.accept(Signal.CANCEL);
            }
        });
        Thread worker = new Thread(task);
        worker.setDaemon(true);
        worker.start();
    }

    @FXML
    void cancelButtonClicked() {
        task.cancel(true);
        signal.accept(Signal.CANCEL);
    }

    private String processUserInputText() {
        contentText = contentTextArea.getText();
        return contentText;
    }

    public String getPmid() {
        return pmidTextField.getText();
    }

    public BioLark getResult() {
        return this.result;
    }

    public String getText() {
        return contentText;
    }

    public void setSignal(Consumer<Signal> signal) {
        this.signal = signal;
    }

    /**
     * Subclass of {@link Task} to allow asynchronous communication with
     */
    private class AskServerTask extends Task<String> {

        private final String userInput;

        AskServerTask(String userInput) {
            this.userInput = userInput;
        }

        @Override
        protected String call() throws Exception {
            return getBiolarkJson(userInput);
        }

        private String getBiolarkJson(String payload) {
            StringBuilder jsonStringBuilder = new StringBuilder();

            try {
                URL url = new URL(server);
                HttpURLConnection connection = (HttpURLConnection)
                        url.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type",
                        "application/json; charset=UTF-8");
                OutputStreamWriter writer = new
                        OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                writer.write(payload);
                writer.close();
                BufferedReader br = new BufferedReader(new
                        InputStreamReader(connection.getInputStream()));

                String line;
                while ((line = br.readLine()) != null) {
                    jsonStringBuilder.append(line);
                }
                br.close();
                connection.disconnect();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return jsonStringBuilder.toString();
        }

    }

}
