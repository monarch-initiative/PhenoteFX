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
import model.CommonDiseaseAnnotation;
import model.Riskfactor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.monarchinitiative.phenotefx.service.Resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RiskFactorFactory {

    private Resources resources;
    private String curatorId;
    private List<Riskfactor> clone;
    private boolean isUpdated;

    //Expect to get available Resources, curator id and current riskfactors
    public RiskFactorFactory(@NotNull Resources resources,
                             @NotNull String curatorId,
                             @Nullable Collection<Riskfactor> currentRiskFactors) {
        this.resources = resources;
        this.curatorId = curatorId;
        if (currentRiskFactors != null) {
            this.clone = new ArrayList<>();
            for (Riskfactor riskfactor : currentRiskFactors){
                this.clone.add(new Riskfactor(riskfactor));
            }
        }
    }

    public boolean showDialog() {
        //the list will hold riskfactor rows that are added by the user. It will not be null, but might be empty
        //List<RiskFactorPresenter.RiskFactorRow> result = new ArrayList<>();

        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Risk Factors";
        window.setTitle(windowTitle);
        RiskFactorView view = new RiskFactorView();
        RiskFactorPresenter presenter = (RiskFactorPresenter) view.getPresenter();
        presenter.setResource(resources);
        presenter.setCuratorId(curatorId);
        presenter.setCurrentRiskFactors(clone);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    isUpdated = presenter.isUpdated();
                    clone = presenter.updated();
                    //result.addAll(presenter.getConfirmed());
                    window.close();
                    break;
                case CANCEL:
                    window.close();
                    break;
                case FAILED:
                    throw new IllegalArgumentException(String.format("Illegal signal %s received.", signal));
            }
        });

        window.setScene(new Scene(view.getView()));
        window.showAndWait();

        return this.isUpdated;
    }

    public List<Riskfactor> updated() {
        return this.clone;
    }
}
