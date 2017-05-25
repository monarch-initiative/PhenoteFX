package org.monarch.hphenote.model;

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
        onsetName2ID.put("Onset","HP_0003674");
        onsetName2ID.put("Congenital onset","HP_0003577");
        onsetName2ID.put("Adult onset","HP_0003581");
        onsetName2ID.put("Late onset","HP_0003584");
        onsetName2ID.put("Middle age onset","HP_0003596");
        onsetName2ID.put("Young adult onset","HP_0011462");
        onsetName2ID.put("Infantile onset","HP_0003593");
        onsetName2ID.put("Juvenile onset","HP_0003621");
        onsetName2ID.put("Neonatal onset","HP_0003623");
        onsetName2ID.put("Embryonal onset","HP_0011460");
        onsetName2ID.put("Fetal onset","HP_0011461");
        onsetName2ID.put("Young adult onset","HP_0011462");
        onsetName2ID.put("Childhood onset","HP_0011463");
        onsetName2ID.put("Antenatal onset","HP_0030674");

    }

    public String getID(String onsetName) {
        return this.onsetName2ID.get(onsetName);
    }
}
