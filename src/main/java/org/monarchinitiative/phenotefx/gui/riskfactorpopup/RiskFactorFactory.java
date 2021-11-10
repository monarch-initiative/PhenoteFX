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

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.service.Resources;

import java.util.ArrayList;
import java.util.List;

public class RiskFactorFactory {

    private Resources resources;

    public RiskFactorFactory(Resources resources) {
        this.resources = resources;
    }

    public List<RiskFactorPresenter.RiskFactorRow> showDialog() {
        //the list will hold riskfactor rows that are added by the user. It will not be null, but might be empty
        List<RiskFactorPresenter.RiskFactorRow> result = new ArrayList<>();

        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Risk Factors";
        window.setTitle(windowTitle);

        window.showAndWait();

        return result;
    }
}
