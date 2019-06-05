package org.monarchinitiative.phenotefx.model;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 Peter Robinson
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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

/**
 * The purpose of this class is to create a list of the HPO Onset terms and to provide
 * a mapping between the terms and the HPO IDs. It is not good style because I am hardcoding
 * the IDs. A better solution would be to use the ontologizer API to traverse the Onset subontology
 * TODO Refactor once ontologizer API is stable.
 * Created by peter on 24.05.17.
 */
public class HPOOnset {

    private static HPOOnset instance = null;

    private Map<String,String> onsetName2ID;

    private ObservableList<String> onsetTermList;

    public ObservableList<String> getOnsetTermList() { return  onsetTermList; }

    public Map<String,String> getOnset2NameMap() { return onsetName2ID; }


    private HPOOnset() {
        onsetName2ID = new HashMap<>();
        onsetTermList= FXCollections.observableArrayList();
        initializeMap();
        onsetTermList.addAll(onsetName2ID.keySet());
    }
    /** Return singleton*/
    public static HPOOnset factory() {
        if (instance== null)
            instance = new HPOOnset();
        return instance;
    }


    private void initializeMap() {
        onsetName2ID.put("Onset","HP:0003674");
        onsetName2ID.put("Congenital onsets","HP:0003577");
        onsetName2ID.put("Adult onsets","HP:0003581");
        onsetName2ID.put("Late onsets","HP:0003584");
        onsetName2ID.put("Middle age onsets","HP:0003596");
        onsetName2ID.put("Young adult onsets","HP:0011462");
        onsetName2ID.put("Infantile onsets","HP:0003593");
        onsetName2ID.put("Juvenile onsets","HP:0003621");
        onsetName2ID.put("Neonatal onsets","HP:0003623");
        onsetName2ID.put("Embryonal onsets","HP:0011460");
        onsetName2ID.put("Fetal onsets","HP:0011461");
        onsetName2ID.put("Childhood onsets","HP:0011463");
        onsetName2ID.put("Antenatal onsets","HP:0030674");

    }

    public String getID(String onsetName) {
        return this.onsetName2ID.get(onsetName);
    }
}
