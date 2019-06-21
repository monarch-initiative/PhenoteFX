package org.monarchinitiative.phenotefx.gui.pointvalueestimate;

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

import base.PointValueEstimate;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PointValueEstimateFactory {

    private PointValueEstimate clone;
    private boolean isUpdated;

    public PointValueEstimateFactory(PointValueEstimate current) {
        if (current != null){
            clone = new PointValueEstimate(current);
        }
    }

    public boolean openDiag() {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Prevalences and Incidences";
        window.setTitle(windowTitle);
        PointValueEstimateView view = new PointValueEstimateView();
        PointValueEstimatePresenter presenter = (PointValueEstimatePresenter) view.getPresenter();

        presenter.setCurrentValue(clone);

        presenter.setSignals(signal -> {
            switch (signal) {
                case DONE:
                    //check
                    isUpdated = presenter.isUpdated();
                    clone = presenter.updated();
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

        return isUpdated;
    }

    public PointValueEstimate updated() {
        return clone;
    }
}
