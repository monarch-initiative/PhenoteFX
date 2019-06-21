package org.monarchinitiative.phenotefx.gui.sigmoidchart;

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
import model.TimeAwareEffectSize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SigmoidChartFactory {

    private boolean isUpdated;
    private Double center;
    private Double steep;
    private TimeAwareEffectSize.TrendType type;

    public SigmoidChartFactory(@Nullable Double center, @Nullable Double steep, @NotNull TimeAwareEffectSize.TrendType type){
        this.center = center;
        this.steep = steep;
        this.type = type;
    }

    public boolean openDiag() {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Adjust time course";
        window.setTitle(windowTitle);
        SigmoidChartView view = new SigmoidChartView();
        SigmoidChartPresenter presenter = (SigmoidChartPresenter) view.getPresenter();
        presenter.setCenterValue(this.center);
        presenter.setSteepValue(this.steep);
        presenter.setCurveType(this.type);


        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    isUpdated = presenter.isUpdated();
                    this.center = presenter.getCenter();
                    this.steep = presenter.getSteep();
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

    public Double getCenter(){
        return this.center;
    }

    public Double getSteep(){
        return this.steep;
    }

    public TimeAwareEffectSize.TrendType getType(){
        return this.type;
    }
}
