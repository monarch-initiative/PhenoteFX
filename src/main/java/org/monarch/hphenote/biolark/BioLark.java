package org.monarch.hphenote.biolark;

import java.util.Set;

/**
 * This class is responsible for orgnazing the results of Bio-Lark based parsing of HPO texts.
 * See: Groza T,et al. (2015) Automatic concept recognition using the human
 * phenotype ontology reference and test suite corpora. Database (Oxford). pii: bav005.
 * This class will store results of parsing and present an interface for the JavaFX wdiget that
 * will present the parsed text and suggested HPO terms for vetting by the user.
 * Created by robinp on 5/26/17.
 */
public class BioLark {

    public BioLark(String original, String biolarkJSON) {

    }


    public Set<String> getHPOTermNames() {
        return null;
    }

    public Set<String> getHPOTermIDs() {
        return null;
    }

    public Set<String> getNegatedHPOTermNames() {
        return null;
    }

    public Set<String> getNegatedHPOTermIDs() {
        return null;
    }

    /* public List<Pair<Integer,Integer>> getLocationsInOriginalText() {

    }
     */


}
