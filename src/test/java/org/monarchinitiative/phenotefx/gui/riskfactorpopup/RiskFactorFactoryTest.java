package org.monarchinitiative.phenotefx.gui.riskfactorpopup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.stage.Stage;
import model.TimeAwareEffectSize;
import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.monarchinitiative.phenotefx.gui.sigmoidchart.SigmoidChartFactory;
import org.monarchinitiative.phenotefx.service.Resources;

import static org.junit.Assert.*;

public class RiskFactorFactoryTest extends Application {

    private Resources resources = new Resources(null, null, null, null);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void list() throws Exception {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        RiskFactorFactory factory = new RiskFactorFactory(resources, "JAX:azhang", null);
        boolean isUpdated = factory.showDialog();
        if (isUpdated){
            factory.updated().forEach(e -> {
                try {
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(e);
                    System.out.println();
                } catch (JsonProcessingException e1) {
                    e1.printStackTrace();
                }
            });
        }
    }

}