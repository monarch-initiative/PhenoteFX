package org.monarchinitiative.phenotefx.gui.webviewerutil;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * A helper class that displays the Read-the-docs documentation for VPV in a JavaFX webview browser
 * @author Peter Robinson
 * @version 0.2.1 (2017-11-02)
 */
public class HelpViewFactory {
    private static final Logger logger = LoggerFactory.getLogger(HelpViewFactory.class.getName());
    private static final String PHENOTEFX_SITE = "https://monarch-initiative.github.io/PhenoteFX/";


    public static void display() {
        openHelpDialog();
    }


    private static String getHTML() {
        return "<html><body><h3>PhenoteFX Help</h3>" +
                "<p><i>PhenoteFX</i> is designed to facilitate the creation of HPO annotations for rare diseases." +
                ".</p>" +
                "<p>Documentation can be found at the <a href=\"https://monarch-initiative.github.io/PhenoteFX/\">PhenoteFX Documentation Website</a>.</p>" +
                "</body></html>";

    }





    private static void openHelpDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("PhenoteFX Help");
        alert.setHeaderText("Get help for PhenoteFX");
        alert.setContentText(String.format("A tutorial and detailed documentation for PhenoteFX can be found online: %s", PHENOTEFX_SITE));

        ButtonType buttonTypeOne = new ButtonType("Open documentation");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeOne){
          openBrowser();
          alert.close();
        } else {
            alert.close();
        }
    }


    /**
     * Open a JavaFW Webview window and confirmDialog our read the docs help documentation in it.
     */
    private static void openBrowser() {
        try{
            Stage window;
            window = new Stage();
            WebView web = new WebView();
            WebEngine webEngine = web.getEngine();
            //webEngine.setUserDataDirectory();
            webEngine.load(PHENOTEFX_SITE);
            Scene scene = new Scene(web);
            window.setScene(scene);
            window.show();
        } catch (Exception e){
            logger.error("Could not open browser to show RTD: {}",e.toString());
            e.printStackTrace();
        }
    }

}
