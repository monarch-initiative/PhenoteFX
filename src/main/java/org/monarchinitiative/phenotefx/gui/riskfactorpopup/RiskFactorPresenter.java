package org.monarchinitiative.phenotefx.gui.riskfactorpopup;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.gui.WidthAwareTextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

//@TODO: inject some resources
public class RiskFactorPresenter implements Initializable {

    private static Logger logger = LoggerFactory.getLogger(RiskFactorPresenter.class);

    private Stage stage;

    @FXML
    private ComboBox<RiskFactorModifier> modifierCombo;

    @FXML
    private ComboBox<RiskFactor> riskFactorCombo;

    @FXML
    private TextField riskFactorTextField;

    @FXML
    private TextField timeMeanField;

    @FXML
    private TextField timeSDfield;

    @FXML
    private ComboBox<SimpleTimeUnit> timeUnitCombo;

    @FXML
    private TableView<RiskFactorRow> riskFactorsTable;

    public void setDialogStage(Stage stage) {
        this.stage = stage;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        modifierCombo.getItems().addAll(RiskFactorModifier.values());
        riskFactorCombo.getItems().addAll(RiskFactor.values());
        timeUnitCombo.getItems().addAll(SimpleTimeUnit.values());

        riskFactorCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if( observable != null) {
                setupAutoComplete(newValue);
            }
        });
        initRiskFactorTable();
    }

    //@TODO: complete
    private void setupAutoComplete(RiskFactor riskFactor) {
        switch (riskFactor) {
            case HPO_Phenotype: //bind to hpo terms
                WidthAwareTextFields.bindWidthAwareAutoCompletion(riskFactorTextField,
                        Arrays.asList("HP:001", "HP:002"));
                break;
            case SECONDARY_DISEASE: //bind to mondo
                WidthAwareTextFields.bindWidthAwareAutoCompletion(riskFactorTextField,
                        Arrays.asList("Mondo:001", "Mondo:002"));
                break;
            case ENVIRONMENT: //bind to environment
                WidthAwareTextFields.bindWidthAwareAutoCompletion(riskFactorTextField,
                        Arrays.asList("Ecto:001", "Ecto:002"));
                break;
        }


    }

    private void initRiskFactorTable() {

    }

    @FXML
    void modifierComboClicked(ActionEvent event) {

    }

    @FXML
    void riskFactorClicked(ActionEvent event) {

    }

    @FXML
    void timeUnitComboClicked(ActionEvent event) {

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

    public enum RiskFactorModifier{
        INCREASED_BY_RISK,
        DECREASED_BY_RISK
    }

    public enum RiskFactor {
        HPO_Phenotype,
        SECONDARY_DISEASE,
        ENVIRONMENT
    }

    public enum SimpleTimeUnit{
        Year,
        Month,
        Day
    }

    public class RiskFactorRow {
        private SimpleStringProperty diseaseName;
        private SimpleStringProperty riskfactor;
        private RiskFactorModifier modifier;
        private SimpleFloatProperty mean;
        private SimpleFloatProperty sd;
        private SimpleTimeUnit timeUnit;
        private SimpleFloatProperty odds;

        public RiskFactorRow() {
            this.diseaseName = new SimpleStringProperty();
            this.riskfactor = new SimpleStringProperty();
            this.mean = new SimpleFloatProperty();
            this.sd = new SimpleFloatProperty();
            this.odds = new SimpleFloatProperty();
        }

        public String getDiseaseName() {
            return diseaseName.get();
        }

        public SimpleStringProperty diseaseNameProperty() {
            return diseaseName;
        }

        public void setDiseaseName(String diseaseName) {
            this.diseaseName.set(diseaseName);
        }

        public String getRiskfactor() {
            return riskfactor.get();
        }

        public SimpleStringProperty riskfactorProperty() {
            return riskfactor;
        }

        public void setRiskfactor(String riskfactor) {
            this.riskfactor.set(riskfactor);
        }

        public RiskFactorModifier getModifier() {
            return modifier;
        }

        public void setModifier(RiskFactorModifier modifier) {
            this.modifier = modifier;
        }

        public float getMean() {
            return mean.get();
        }

        public SimpleFloatProperty meanProperty() {
            return mean;
        }

        public void setMean(float mean) {
            this.mean.set(mean);
        }

        public float getSd() {
            return sd.get();
        }

        public SimpleFloatProperty sdProperty() {
            return sd;
        }

        public void setSd(float sd) {
            this.sd.set(sd);
        }

        public SimpleTimeUnit getTimeUnit() {
            return timeUnit;
        }

        public void setTimeUnit(SimpleTimeUnit timeUnit) {
            this.timeUnit = timeUnit;
        }

        public float getOdds() {
            return odds.get();
        }

        public SimpleFloatProperty oddsProperty() {
            return odds;
        }

        public void setOdds(float odds) {
            this.odds.set(odds);
        }
    }

}
