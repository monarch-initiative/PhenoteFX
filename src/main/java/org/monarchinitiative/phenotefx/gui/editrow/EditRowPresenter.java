package org.monarchinitiative.phenotefx.gui.editrow;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.gui.PopUps;
import org.monarchinitiative.phenotefx.gui.Signal;
import org.monarchinitiative.phenotefx.model.PhenoRow;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * This causes a dialog to popup where the user can set the publication.
 */
public class EditRowPresenter implements Initializable {

    @FXML
    private TextField textField;


    private Stage dialogStage;
    private boolean okClicked = false;
    private Consumer<Signal> signal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // no-op, we need to receive data via setData
    }

    void setSignal(Consumer<Signal> signal) {
        this.signal = signal;
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    String getText() {
        return this.textField.getText();
    }

    /**
     * @param text Text that will be used to initialize the dialog
     */
    void setInitialText(String text) {
        this.textField.setText(text);
    }


    /** Optionally remove all whitespace from initial text -- not using this for now. */
    void removeWhiteSpaceFromTextEntries() {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                String txt = textField.getText();
                txt = txt.replaceAll("\\s", "");
                textField.setText(txt);
            }
        });
    }



    /**
     * @return true if the user clicked OK, false otherwise.
     */
    boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";
        if (textField.getText() == null || textField.getText().length() == 0) {
            errorMessage += "No valid entry!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message
            PopUps.showInfoMessage("Please correct invalid fields", errorMessage);
            return false;
        }
    }
}
