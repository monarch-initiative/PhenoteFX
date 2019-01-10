package org.monarchinitiative.phenotefx.service;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenotefx.io.EctoParser;
import org.monarchinitiative.phenotefx.io.HPOParser;
import org.monarchinitiative.phenotefx.io.MedGenParser;
import org.monarchinitiative.phenotefx.io.MondoParser;

import java.util.Map;


/**
 * Manage all resources through this class, such as HPO, Mondo, ECTO
 */
public class Resources {

    private MedGenParser medGenParser;
    private HPOParser hpoParser;
    private MondoParser mondoParser;
    private EctoParser ectoParser;

    public Resources(MedGenParser medGenParser, HPOParser hpoParser, MondoParser mondoParser, EctoParser ectoParser) {
        this.medGenParser = medGenParser;
        this.hpoParser = hpoParser;
        this.mondoParser = mondoParser;
        this.ectoParser = ectoParser;
    }

    public Map<String, String> getOmimName2IdMap() {
        return this.medGenParser.getOmimName2IdMap();
    }

    public Ontology getHPO() {
        return hpoParser.getHpoOntology();
    }

    public Map<String,String> getHpoName2IDmap() { return hpoParser.getHpoName2IDmap(); }
    public Map<String,String> getHpoSynonym2PreferredLabelMap() { return hpoParser.getHpoSynonym2PreferredLabelMap(); }

    /**@return map with key: label and value HPO Id for just the Clinical Modifier subhierarchy */
    public Map<String,String> getModifierMap() {
        return hpoParser.getModifierMap();
    }

    public Ontology getDiseaseSubOntology() {
        return mondoParser.getDiseaseSubOntology();
    }

    public Map<String, String> getMondoDiseaseName2IdMap() {
        return mondoParser.getName2IdMap();
    }

    public Ontology getEcto() {
        return this.ectoParser.getEcto();
    }

    public Map<String, String> getEctoName2Id() {
        return this.ectoParser.getName2IdMap();
    }

    //@TODO: cache the name to id maps?
    public void cache() {

    }

}
