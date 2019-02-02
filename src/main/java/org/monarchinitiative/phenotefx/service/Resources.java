package org.monarchinitiative.phenotefx.service;

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
