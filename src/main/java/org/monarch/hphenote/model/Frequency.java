package org.monarch.hphenote.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

/**
 * Convenience class for the Frequency terms (See HPOOnset)
 * Created by peter on 24.05.17.
 */
public class Frequency {

    private static Frequency instance;

    private Map<String,String> frequencyName2ID;

    private ObservableList<String> frequencyTermList;

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
        frequencyName2ID.put("Occasional:","HP:0040283");
        frequencyName2ID.put("Very rare","HP:0040284");
        frequencyName2ID.put("Excluded","HP:0040285");
    }


    public String getID(String frequencyName) {
        return this.frequencyName2ID.get(frequencyName);
    }
}
