package org.monarchinitiative.hphenote.io;

/*
 * #%L
 * HPhenote
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


import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermContainer;
import ontologizer.ontology.TermMap;
import ontologizer.types.ByteString;
import org.monarchinitiative.hphenote.gui.Platform;
import org.monarchinitiative.hphenote.model.HPO;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This is a very simple OBO parser that gets a list of HPO terms and IDs. It is intended
 * to enable autocompletion and is not a complete OBO Parser.
 * TODO Replace this with new ontologizer-lib parser when finished
 * @author  Peter Robinson
 * @version 0.0.1 (May 24, 2017)
 */
public class HPOParser extends Parser {

    private Map<String,HPO> hpoMap=null;

    private Map<String,String> hpoName2IDmap=null;

    /** The constructor sets {@link #absolutepath} to
     * the absolute path of the downloaded HPO file (whether or not the file has
     * been downloaded yet; The parser will simply return an empty Map if the file
     * is not available. Note that there is no option as to where the file gets stored
     * (in the .hphenote directory in the home directory of the user).
     */
    public HPOParser() {
        File dir = Platform.getHPhenoteDir();
        String basename="hp.obo";
        this.absolutepath = new File(dir + File.separator + basename);
        this.hpoMap=new HashMap<>();
        hpoName2IDmap=new HashMap<>();
        this.hpoSynonym2PreferredLabelMap=new HashMap<>();
        inputFile();
    }

    /** @return a Map of HPO terms. THe Map will be initialized but empty if the hp.obo
     * file cannot be parsed.
     */
    public Map<String,HPO> getTerms() {
        return this.hpoMap;
    }

    public Map<String,String> getHpoName2IDmap() { return this.hpoName2IDmap; }

    public Map<String,String> hpoSynonym2PreferredLabelMap;

    public Map<String,String> getHpoSynonym2PreferredLabelMap() { return hpoSynonym2PreferredLabelMap; }


    /**
     * Inputs the hp.obo file and fills {@link #hpoMap} with the contents.
     */
    private void inputFile() {

        Ontology ontology=null;
        if (! inputFileExists())
            return;
        try {
            OBOParser parser = new OBOParser(new OBOParserFileInput(this.absolutepath.getAbsolutePath()));

            String parseResult = parser.doParse();

            System.err.println("Information about parse result:");
            System.err.println(parseResult);
            TermContainer termContainer =
                    new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser.getDate());
            ontology = Ontology.create(termContainer);
        } catch (IOException e) {
            System.err.println(
                    "ERROR: Problem reading input file. See below for technical information\n\n");
            e.printStackTrace();
            System.exit(1);
        } catch (OBOParserException e) {
            System.err.println(
                    "ERROR: Problem parsing OBO file. See below for technical information\n\n");
            e.printStackTrace();
            System.exit(1);
        }
        TermMap tmap = ontology.getTermMap();
        Iterator<Term> it = tmap.iterator();
        while (it.hasNext()) {
            Term t = it.next();
            //System.out.println(t);
            String label = t.getName().toString();
            String id = t.getIDAsString();
            HPO hpo = new HPO();
            hpo.setHpoId(id);
            hpo.setHpoName(label);
            hpoName2IDmap.put(label,id);
            this.hpoMap.put(id,hpo);
            this.hpoSynonym2PreferredLabelMap.put(label,label);
            ByteString[] synarray = t.getSynonyms();
            if (synarray != null) {
                for (ByteString syn : synarray)
                    this.hpoSynonym2PreferredLabelMap.put(syn.toString(), label);
            }


        }
    }

}
/*eof*/