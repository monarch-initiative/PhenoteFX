package org.monarchinitiative.phenotefx.io;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2020 Peter Robinson
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

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenotefx.exception.PhenoteFxException;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;


public class EctoParser {

    private static final Logger logger = LoggerFactory.getLogger(EctoParser.class);

    private String path;

    private final InputStream stream;

    private Ontology ecto;

    private Map<String, String> name2IdMap;

    public EctoParser() throws PhenoteFxException {
        File dir = Platform.getPhenoteFXDir();
        String basename="ecto.obo";
        this.path = dir + File.separator + basename;
        try {
            this.stream = new FileInputStream(path);
            this.ecto = parse();
        } catch (FileNotFoundException  e) {
            logger.error("ecto.obo not found at " + dir);
            throw new PhenoteFxException(String.format("Unable to parse Ecto OBO file at %s [%s]", this.path, e));
        }

        try {
            this.stream.close();
        } catch (IOException e) {
            logger.warn("IO exception for closing inputstream of ecto");
        }

    }

    public EctoParser(InputStream stream) {
        this.stream = stream;
    }

    public EctoParser(String path) throws FileNotFoundException {
        this.path = path;
        this.stream = new FileInputStream(path);
    }

    public Ontology parse() {
        this.ecto = OntologyLoader.loadOntology(this.stream, "ECTO");
        return this.ecto;
    }

    public Ontology getEcto() {
        return this.ecto;
    }

    /**
     * Return a map from ecto term names to term id.
     * @return map from ECTO names to ids
     */
    public Map<String, String> getName2IdMap(){

        if (this.name2IdMap != null) {
            return this.name2IdMap;
        }

        this.name2IdMap = this.ecto.getTermMap().values()
                .stream()
                .collect(Collectors.toMap(
                        Term::getName,
                        term -> term.getId().getValue(),
                        (a, b) -> a));
        return this.name2IdMap;
    }

}
