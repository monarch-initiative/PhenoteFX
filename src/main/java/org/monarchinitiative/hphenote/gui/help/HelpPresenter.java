package org.monarchinitiative.hphenote.gui.help;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.hphenote.gui.Signal;



import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Created by peterrobinson on 7/3/17.
 */
public class HelpPresenter implements Initializable {
    private static final Logger logger = LogManager.getLogger();
    @FXML private WebView wview;
    @FXML private Button closeButton;
    @FXML private ScrollPane scrollPane;

    private WebEngine contentWebEngine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            this.closeButton.requestFocus();
        });
    }

    public void setData(String html) {
        this.contentWebEngine = wview.getEngine();
        this.contentWebEngine.loadContent(html);
    }

    private Consumer<Signal> signal;

    public void setSignal(Consumer<Signal> signal) {
        this.signal = signal;
    }


    @FXML
    public void closeWindow(ActionEvent e) {
        e.consume();
        signal.accept(Signal.DONE);
    }
}
