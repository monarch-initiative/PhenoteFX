package org.monarchinitiative.phenotefx.gui.riskfactorpopup;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2019 Peter Robinson
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.stage.Stage;
import model.TimeAwareEffectSize;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.monarchinitiative.phenotefx.gui.sigmoidchart.SigmoidChartFactory;
import org.monarchinitiative.phenotefx.service.Resources;

import static org.junit.Assert.*;

@Ignore
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