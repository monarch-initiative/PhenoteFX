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
import java.util.Optional;

/**
 * Convenience class for the Frequency terms (See HPOOnset)
 * Created by peter on 24.05.17.
 */
public class Frequency {

    private static Frequency instance;

    private final Map<String,String> frequencyName2ID;

    private final ObservableList<String> frequencyTermList;

    public ObservableList<String> getFrequencyTermList() { return  frequencyTermList; }

    public Map<String,String> getFrequency2NameMap() { return frequencyName2ID; }

    private Frequency() {
        frequencyName2ID = new HashMap<>();
        frequencyTermList= FXCollections.observableArrayList();
        initializeMap();
        frequencyTermList.addAll(frequencyName2ID.keySet());
    }
    /** Return singleton*/
    public static Frequency factory() {
        if (instance== null)
            instance = new Frequency();
        return instance;
    }

    private void initializeMap() {
        frequencyName2ID.put("Frequency","HP:0040279");
        frequencyName2ID.put("Obligate","HP:0040280");
        frequencyName2ID.put("Very frequent","HP:0040281");
        frequencyName2ID.put("Frequent","HP:0040282");
        frequencyName2ID.put("Occasional","HP:0040283");
        frequencyName2ID.put("Very rare","HP:0040284");
        frequencyName2ID.put("Excluded","HP:0040285");
    }


    public String getID(String frequencyName) {
        return this.frequencyName2ID.get(frequencyName);
    }

    /**
     * The method tries to find the name of an id for a frequency term
     * @param id an HPO id (for a frequency term), expressed as a string
     * @return the corresponding label, if it can be found
     */
    public Optional<String> getName(String id) {
        Optional<Map.Entry<String, String>> entry = this.frequencyName2ID.entrySet().stream()
                .filter(e -> e.getValue().equals(id)).findFirst();
        return entry.map(Map.Entry::getKey);
    }
}
