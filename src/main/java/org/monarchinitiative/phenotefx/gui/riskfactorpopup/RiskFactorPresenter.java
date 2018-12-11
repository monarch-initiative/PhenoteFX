package org.monarchinitiative.phenotefx.gui.riskfactorpopup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URL;
import java.util.ResourceBundle;

public class RiskFactorPresenter implements Initializable {

    private static Logger logger = LoggerFactory.getLogger(RiskFactorPresenter.class);

    private Stage stage;

    public void setDialogStage(Stage stage) {
        this.stage = stage;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void confirmClicked(ActionEvent e) {
        e.consume();
        logger.info("confirm button clicked");
    }

    @FXML
    private void cancelClicked(ActionEvent e) {
        e.consume();
        logger.info("cancel button clicked");
    }

}
