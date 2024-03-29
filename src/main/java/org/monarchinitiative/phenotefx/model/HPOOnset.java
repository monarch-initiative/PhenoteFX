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

import java.util.*;

/**
 * The purpose of this class is to create a list of the HPO Onset terms and to provide
 * a mapping between the terms and the HPO IDs. It is not good style because I am hardcoding
 * the IDs. A better solution would be to use the ontologizer API to traverse the Onset subontology
 * Created by peter on 24.05.17.
 */
public class HPOOnset {

    private static HPOOnset instance = null;

    private final Map<String,String> onsetName2ID;

    private final ObservableList<String> onsetTermList;

    private HPOOnset() {
        onsetName2ID = new LinkedHashMap<>();
        onsetTermList= FXCollections.observableArrayList();
        initializeMap();
        onsetTermList.addAll(onsetName2ID.keySet());
    }

    public ObservableList<String> getOnsetTermList() { return  onsetTermList; }

    /** Return singleton*/
    public static HPOOnset factory() {
        if (instance== null)
            instance = new HPOOnset();
        return instance;
    }


    private void initializeMap() {
        onsetName2ID.put("Antenatal onset","HP:0030674");
        onsetName2ID.put("Embryonal onset","HP:0011460");
        onsetName2ID.put("Fetal onset","HP:0011461");
        onsetName2ID.put("Late first trimester onset","HP:0034199");
        onsetName2ID.put("Second trimester onset","HP:0034198");
        onsetName2ID.put("Third trimester onset","HP:0034197");
        onsetName2ID.put("Congenital onset","HP:0003577");
        onsetName2ID.put("Neonatal onset","HP:0003623");
        onsetName2ID.put("Infantile onset","HP:0003593");
        onsetName2ID.put("Childhood onset","HP:0011463");
        onsetName2ID.put("Juvenile onset","HP:0003621");
        onsetName2ID.put("Adult onset","HP:0003581");
        onsetName2ID.put("Young adult onset","HP:0011462");
        onsetName2ID.put("Early young adult onset","HP:0025708");
        onsetName2ID.put("Intermediate young adult onset","HP:0025709");
        onsetName2ID.put("Late young adult onset","HP:0025710");
        onsetName2ID.put("Middle age onset","HP:0003596");
        onsetName2ID.put("Late onset","HP:0003584");
    }

    public String getID(String onsetName) {
        return this.onsetName2ID.get(onsetName);
    }
}
