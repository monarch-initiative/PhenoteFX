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

import ontologizer.io.obo.OBOParserException;
import ontologizer.ontology.TermContainer;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;
import org.monarchinitiative.phenol.io.obo.hpo.HpoOboParser;
import org.monarchinitiative.phenol.ontology.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.phenotefx.exception.PhenoteFxException;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.monarchinitiative.phenotefx.model.HPO;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class uses the ontolib library to parse the HPO file and to provide the data structures needed to populate the
 * GUI with HPO terms and names.
 * @author Peter Robinson
 * @version 0.1.1
 */
public class HPOParser {
    private static final Logger logger = LogManager.getLogger();
    /** The absolute path of the hp.obo file that will be parsed in. */
    private File hpoPath =null;
    /** Key: an HPO id, such as HP:0001234; value: corresponding {@link HPO} object. */
    private Map<String,HPO> hpoMap=null;
    /** key: an HPO label; value: corresponding HP id, e.g., HP:0001234 */
    private Map<String,String> hpoName2IDmap=null;
    /** Key: any label (can be a synonym). Value: corresponding main preferred label. */
    public Map<String,String> hpoSynonym2PreferredLabelMap;
    /** Ontology */
    private HpoOntology ontology=null;

    /**
     * Construct a parser and use the default HPO location
     */
    public HPOParser() throws PhenoteFxException {
        File dir = Platform.getPhenoteFXDir();
        String basename="hp.obo";
        this.hpoPath = new File(dir + File.separator + basename);
        this.hpoMap=new HashMap<>();
        hpoName2IDmap=new HashMap<>();
        this.hpoSynonym2PreferredLabelMap=new HashMap<>();
        inputFile();
    }


    public HpoOntology getHpoOntology() {
        return ontology;
    }


    /**
     * Construct a parser and use a custom location for the HPO
     */
    public HPOParser(String hpoPath) throws PhenoteFxException {
        File dir = Platform.getPhenoteFXDir();
        String basename="hp.obo";
        this.hpoPath = new File(hpoPath);
        this.hpoMap=new HashMap<>();
        hpoName2IDmap=new HashMap<>();
        this.hpoSynonym2PreferredLabelMap=new HashMap<>();
        inputFile();
    }

    /** @return a Map of HPO terms. THe Map will be initialized but empty if the hp.obo file cannot be parsed. */
    public Map<String,HPO> getTerms() {
        return this.hpoMap;
    }
    public Map<String,String> getHpoName2IDmap() { return this.hpoName2IDmap; }
    public Map<String,String> getHpoSynonym2PreferredLabelMap() { return hpoSynonym2PreferredLabelMap; }



    /**
     * Inputs the hp.obo file and fills {@link #hpoMap} with the contents.
     */
    private void inputFile() throws PhenoteFxException {
        try {
            HpoOboParser hpoOboParser = new HpoOboParser(hpoPath);
            this.ontology = hpoOboParser.parse();
        } catch (IOException e) {
            logger.error(String.format("Unable to parse HPO OBO file at %s", hpoPath.getAbsolutePath() ));
            logger.error(e,e);
            throw new PhenoteFxException(String.format("Unable to parse HPO OBO file at %s [%s]", hpoPath.getAbsolutePath(),e.toString()));
        }
        if (ontology==null) {
            logger.error("HpoOntology is null");
        }
        Map<TermId,HpoTerm> termmap=ontology.getTermMap();

        for (TermId termId : termmap.keySet()) {
            HpoTerm hterm = termmap.get(termId);
            String label = hterm.getName();
            String id = hterm.getId().getIdWithPrefix();//hterm.getId().toString();
            HPO hp = new HPO();
            hp.setHpoId(id);
            hp.setHpoName(label);

            hpoName2IDmap.put(label,id);
            this.hpoMap.put(id,hp);
            this.hpoSynonym2PreferredLabelMap.put(label,label);
            List<TermSynonym> syns = hterm.getSynonyms();
            for (TermSynonym syn : syns ) {
                String synlabel = syn.getValue();
                this.hpoSynonym2PreferredLabelMap.put(synlabel, label);
            }
        }
    }


    /**
     * This method is provided because the text mining widget is using the Ontologizer API to
     * input the HPO OBO file. TODO - refactor once the widget is updated to phenol.
     * @param pathToOBOFile
     * @return
     * @throws IOException
     * @throws OBOParserException
     */
    public ontologizer.ontology.Ontology getOntologizerOntology(String pathToOBOFile) throws IOException , OBOParserException{
        ontologizer.io.obo.OBOParser parser = new ontologizer.io.obo.OBOParser(new ontologizer.io.obo.OBOParserFileInput(pathToOBOFile), ontologizer.io.obo.OBOParser.PARSE_DEFINITIONS);
        String result = parser.doParse();
        TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser.getDate());
        return ontologizer.ontology.Ontology.create(termContainer);
    }




}
