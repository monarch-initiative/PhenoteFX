package org.monarchinitiative.phenotefx.gui.prevalencepopup;

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

import base.OntoTerm;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Prevalence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class PrevalenceFactory {

    private List<Prevalence> prevalences;
    private List<Prevalence> clone;
    private Map<String, String> candidateTermName2Id;
    private String curatorId;
    private boolean isUpdated;

    public PrevalenceFactory(@Nullable List<Prevalence> prevalences, @Nullable Collection<OntoTerm> prevalenceTerms, @NotNull String curatorId) {
        clone = new ArrayList<>();
        if (prevalences != null) {
            for (Prevalence prevalence : prevalences){
                clone.add(new Prevalence(prevalence));
            }
        }
        this.candidateTermName2Id = new HashMap<>();
        if (prevalenceTerms != null){
            this.candidateTermName2Id = prevalenceTerms.stream().collect(Collectors.toMap(p -> p.getLabel(), p -> p.getId()));
        }
        this.curatorId = curatorId;
    }

    //true if use clicked confirm and there are changes to prevalences or incidences
    public boolean openDiag() {

        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Common Disease Prevalences";
        window.setTitle(windowTitle);
        PrevalenceView view = new PrevalenceView();
        PrevalencePresenter presenter = (PrevalencePresenter) view.getPresenter();
        //presenter.setDialogStage(window);
        presenter.setCuratorId(curatorId);
        presenter.setCurrentPrevalences(clone);
        presenter.setCandidateTerms(this.candidateTermName2Id);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    //check
                    isUpdated = presenter.prevalenceDirty();
                    clone = presenter.updatedPrevalences();
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

    public List<Prevalence> getPrevalences() {
        return this.clone;
    }


}
