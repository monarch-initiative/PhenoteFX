package org.monarch.hphenote.biolark;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.monarch.hphenote.biolark.analyze.BiolarkAnalyzePresenter;
import org.monarch.hphenote.biolark.analyze.BiolarkAnalyzeView;
import org.monarch.hphenote.biolark.configure.BiolarkConfigurePresenter;
import org.monarch.hphenote.biolark.configure.BiolarkConfigureView;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is responsible for performing text-mining analysis that identifies putative HPO terms in given text.<p/>
 * At first user is asked to provide the text data to be mined for the HPO terms along with PMID of publication,
 * then connection to REST API is made and text is mined for the terms. Information is returned as JSON string which
 * is used to visualize origin of identified terms in original text. User is further asked to select
 * relevant/appropriate terms. Selected terms returned in a set.
 * Created by Daniel Danis on 5/30/17.
 */
public class BiolarkAnalysis implements TextMiningAnalyzer {

    /* Pop-Up dialog window. */
    private Stage window;

    /* Title of dialog window. */
    private static final String windowTitle = "Text-mining analysis";

    private String pmid;

    private BioLark result;

    private String text;

    /* Flag used to signalize that the analysis was performed correctly or should continue. */
    private boolean status = true;

    /* Sets containing the terms selected/approved by user. */
    private Set<String> yesTerms, notTerms;

    public BiolarkAnalysis() {
        window = new Stage();
        window.setOnCloseRequest(e -> status = false);
        window.setTitle(windowTitle);
        yesTerms = new HashSet<>();
        notTerms = new HashSet<>();
        runAnalysis();
    }

    /**
     * Run steps of the analysis.
     */
    private void runAnalysis() {
        getResult();
        if (status) {
            getSelection();
        }
    }

    private void getResult() {
        BiolarkConfigureView configureView = new BiolarkConfigureView();
        BiolarkConfigurePresenter configurePresenter = (BiolarkConfigurePresenter) configureView.getPresenter();

        configurePresenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    result = configurePresenter.getResult();
                    text = configurePresenter.getText();
                    pmid = configurePresenter.getPmid();
                    break;
                case CANCEL:
                    this.status = false;
                    break;
                case FAILED:
                    this.status = false;
                    Alert a = new Alert(Alert.AlertType.WARNING);
                    a.setTitle(windowTitle);
                    a.setHeaderText("Sorry, text-mining analysis failed");
                    a.setContentText("One from many possible reasons is that you're offline.");
                    a.showAndWait();
                    break;
            }
            window.close(); // this must be done in order to continue analysis. We're asynchronous here.. ;)
        });
        window.setScene(new Scene(configureView.getView()));
        window.showAndWait();
    }

    private void getSelection() {
        BiolarkAnalyzeView analyzeView = new BiolarkAnalyzeView();
        BiolarkAnalyzePresenter analyzePresenter = (BiolarkAnalyzePresenter) analyzeView.getPresenter();
        analyzePresenter.setData(result.getIntervals(),
                result.getHpoTermLabels(),
                result.getNegatedHPOTermLabels(),
                text);
        analyzePresenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    window.close();
                    break;
                case CANCEL:
                case FAILED:
                    throw new IllegalArgumentException(String.format("Illegal signal %s received.", signal));
            }

        });

        window.setScene(new Scene(analyzeView.getView()));
        window.showAndWait();

        // this will be executed after user selects yes & not HPO terms and after the window is closed
        yesTerms.addAll(analyzePresenter.getYesTerms());
        notTerms.addAll(analyzePresenter.getNotTerms());
    }

    // *********************** GETTERS **************************************************
    @Override
    public String getPmid() {
        return pmid;
    }

    @Override
    public Set<String> getYesTerms() {
        return yesTerms;
    }

    @Override
    public Set<String> getNotTerms() {
        return notTerms;
    }

    @Override
    public boolean getStatus() {
        return status;
    }

}
