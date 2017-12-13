package org.monarchinitiative.hphenote.gui.help;

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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
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
import org.w3c.dom.Document;


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
        logger.error("Initializing presenter hash="+hashCode());
        Platform.runLater(() -> {
            this.closeButton.requestFocus();
        });
    }

    public void setData(String html) {
        logger.error(String.format("set data html=%s",html));
        this.contentWebEngine = wview.getEngine();
        contentWebEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                Document doc = contentWebEngine.getDocument();
            }
        });
        this.contentWebEngine.loadContent(html,"text/html");
        logger.error("content web eng "+ contentWebEngine.getDocument());
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
