package org.monarchinitiative.phenotefx.gui.evidencepopup;

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
import model.Evidence;
import org.jetbrains.annotations.Nullable;

public class EvidenceFactory {

    private Evidence evidence;
    private boolean updated;

    public EvidenceFactory(@Nullable Evidence evidence) {}

    public boolean openDiag() {

        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Set Evidence";
        window.setTitle(windowTitle);
        EvidenceView view = new EvidenceView();
        EvidencePresenter presenter = (EvidencePresenter) view.getPresenter();
        presenter.setCurrent(evidence);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    updated = true;
                    evidence = presenter.getEvidence();
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

        return this.updated;
    }

    public Evidence getEvidence(){
        return evidence;
    }

}
