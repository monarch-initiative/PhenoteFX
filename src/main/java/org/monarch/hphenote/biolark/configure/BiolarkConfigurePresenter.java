package org.monarch.hphenote.biolark.configure;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.monarch.hphenote.biolark.BioLark;
import org.monarch.hphenote.biolark.Signal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * This class asks user/biocurator to provide text that should be analyzed and PMID of the publication from which the
 * analyzed text is coming from. Then, new thread is spawned and server is asked to perform text-minig analysis.
 * The server returns result in JSON format and the result is subsequently wrapped into instance of {@link BioLark}
 * object.
 * <p>
 * Created by Daniel Danis on 5/26/17.
 */
public class BiolarkConfigurePresenter implements Initializable {

    private static String server = "http://phenotyper.monarchinitiative.org:5678/cr/annotate";

    private BioLark result;

    private static AskServerTask task;

    /**
     * User will paste analyzed text here.
     */
    @FXML
    private TextArea contentTextArea;

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
     * This Consumer will be executed after analysis has been complete or if anything important happens.
     */
    private Consumer<Signal> signal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // This binding allows to start the analysis only after that all required info has been entered.
        BooleanBinding allSet = Bindings.createBooleanBinding(() -> // analyzeButton will stay disabled if:
                        !pmidTextField.getText().matches("\\d+") || // PMID Text doesn't match this regex OR
                                contentTextArea.getText().equalsIgnoreCase(""), // contentTextArea is empty
                pmidTextField.textProperty(), contentTextArea.textProperty());
        analyzeButton.disableProperty().bind(allSet);
    }

    /**
     * Run after user clicks Analyze button. Spawns a thread that tries to connect to the server and to retrieve response in JSON format.
     * The response is wrapped into {@link BioLark} object.
     */
    @FXML
    void analyzeButtonClicked() {
        /* Ask server to parse the text entered by user */
        statusLabel.setText("Asking server...");
        cancelButton.setDisable(false);
        task = new AskServerTask(getText());
        task.setOnSucceeded(e -> {
            try {
                this.result = new BioLark(task.get());
                signal.accept(Signal.DONE);
            } catch (InterruptedException | ExecutionException ie) {
                signal.accept(Signal.FAILED);
            }
        });
        task.setOnCancelled(e -> signal.accept(Signal.CANCEL));
        task.setOnFailed(e -> signal.accept(Signal.FAILED));
        Thread worker = new Thread(task);
        worker.setDaemon(true);
        worker.start();
    }

    @FXML
    void cancelButtonClicked() {
        task.cancel(true);
        signal.accept(Signal.CANCEL);
    }

    public String getPmid() {
        return pmidTextField.getText();
    }

    public BioLark getResult() {
        return this.result;
    }

    public String getText() {
        return contentTextArea.getText().replace('\n', ' ');
    }

    public void setSignal(Consumer<Signal> signal) {
        this.signal = signal;
    }

    /**
     * Subclass of {@link Task} to allow asynchronous communication with server in order to retrieve results of
     * text-mining analysis in JSON format.
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
