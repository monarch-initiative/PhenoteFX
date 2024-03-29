package org.monarchinitiative.phenotefx.io;

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

import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.*;

import org.monarchinitiative.phenotefx.gui.Platform;
import org.monarchinitiative.phenotefx.model.HPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.getDescendents;

/**
 * This class uses the ontolib library to parse the HPO file and to provide the data structures needed to populate the
 * GUI with HPO terms and names.
 * @author Peter Robinson
 * @version 0.1.1
 */
public class HPOParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(HPOParser.class);
    /** Key: an HPO id, such as HP:0001234; value: corresponding {@link HPO} object. */
    private final Map<String,HPO> hpoMap;
    /** key: an HPO label; value: corresponding HP id, e.g., HP:0001234 */
    private final Map<String,String> hpoName2IDmap;
    /** Key: any label (can be a synonym). Value: corresponding main preferred label. */
    private final Map<String,String> hpoSynonym2PreferredLabelMap;
    /** Ontology */
    private final Ontology ontology;
    /** Modifiers */
    private final Map<String,String> modifierMap;

    /**
     * Construct a parser and use the default HPO location
     */
    public HPOParser() {
        this(Platform.getPhenoteFXDir() + File.separator + "hp.json");
    }

    /**
     * Construct a parser and use a specified location for the HPO
     */
    public HPOParser(String hpoJsonPath) {
        if (hpoJsonPath.endsWith(".obo")) {
            throw new PhenolRuntimeException("Cannot parse *.obo files, try hp.json");
        }
        LOGGER.info("hpoJsonPath = {}", hpoJsonPath);
        // The absolute path of the hp.obo file that will be parsed in.
        File hpoPath1 = new File(hpoJsonPath);
        LOGGER.info("About to load {}", hpoPath1.getAbsolutePath());
        this.ontology = OntologyLoader.loadOntology(hpoPath1);
        LOGGER.debug("Loaded ontology, got {} terms", ontology.countNonObsoleteTerms());
        this.hpoMap=new HashMap<>();
        hpoName2IDmap=new HashMap<>();
        this.hpoSynonym2PreferredLabelMap=new HashMap<>();
        for (TermId termId : ontology.getTermMap().keySet()) {
            Term hterm = ontology.getTermMap().get(termId);
            String label = hterm.getName();
            String id = hterm.id().getValue();
            HPO hp = new HPO();
            hp.setHpoId(id);
            hp.setHpoName(label);
            hpoName2IDmap.put(label,id);
            this.hpoMap.put(id,hp);
            this.hpoSynonym2PreferredLabelMap.put(label,label);
            List<TermSynonym> syns = hterm.getSynonyms();
            if (syns!=null) {
                for (TermSynonym syn : syns) {
                    String synlabel = syn.getValue();
                    this.hpoSynonym2PreferredLabelMap.put(synlabel, label);
                }
            }
        }
        LOGGER.debug("Got {} HPO synonyms", hpoSynonym2PreferredLabelMap.size());
        this.modifierMap = new HashMap<>();
        TermId clinicalModifier = TermId.of("HP:0012823");
        Set<TermId> modifierIds = getDescendents(ontology,clinicalModifier);
        for (TermId tid:modifierIds) {
            Term term = ontology.getTermMap().get(tid);
            modifierMap.put(term.getName(),tid.getValue());
        }
        LOGGER.info("Got {} modifier terms", this.modifierMap.size());
    }

    public Ontology getHpoOntology() {
        return ontology;
    }

    /** @return a Map of HPO terms. THe Map will be initialized but empty if the hp.obo file cannot be parsed. */
    public Map<String,HPO> getTerms() {
        return this.hpoMap;
    }
    public Map<String,String> getHpoName2IDmap() { return this.hpoName2IDmap; }
    public Map<String,String> getHpoSynonym2PreferredLabelMap() { return hpoSynonym2PreferredLabelMap; }


    public Map<String,String> getModifierMap() {
        return this.modifierMap;
    }


}
