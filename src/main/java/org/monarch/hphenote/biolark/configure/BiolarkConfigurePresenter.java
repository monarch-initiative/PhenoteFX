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
 * Presenter class that asks user to input
 * Created by Daniel Danis on 5/26/17.
 */
public class BiolarkConfigurePresenter implements Initializable {

    private static String server = "http://phenotyper.monarchinitiative.org:5678/cr/annotate";

    private String payload = "Our case is a 24-year-old male born to consanguineous Yemeni parents. He was healthy at birth and "
            + "as a baby he achieved normal developmental milestones.\nBy three years of age, he started to have "
            + "difficulties during walking and developed progressive knee deformities.\nRapidly over several years, he "
            + "started to\ndevelop progressive symmetric joint pain, stiffness and swelling. The first joint involved were "
            + "the knees followed by hips, elbows and hand joints. The pain\ninvolved almost all joints, but more "
            + "severe in hips and lower back. The patient was used to take non-steroidal anti-inflammatory drugs "
            + "(NSAIDs) irregularly\nin the case of severe pain, but he has never been on steroid therapy. There were "
            + "no symptoms of numbness or tingling in the extremities and there was no\nhepatosplenomegaly. He "
            + "exhibited a flexed posture in the trunk and extremities and abnormal gait (Figure 1A and supplemental "
            + "video S1). We found enlargement\nof joints, which were more prominent in the interphalangeal, elbow "
            + "and knees joints (Figure 1 A, B), but there were no signs of inflammation such as tenderness or\n"
            + "redness. Movements of all joints were extremely restricted, including neck, spine, shoulder, elbow, "
            + "wrist, knee, and ankle and interphalangeal joints of hands\nand feet. The mental status, vision, hearing "
            + " and speech were normal.";

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
                        !pmidTextField.getText().matches("\\d{7,9}") || // PMID Text doesn't match this regex OR
                                contentTextArea.getText().equalsIgnoreCase(""), // contentTextArea is empty
                pmidTextField.textProperty(), contentTextArea.textProperty());
        analyzeButton.disableProperty().bind(allSet);
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
