package org.monarchinitiative.phenotefx.io;

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

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.exception.PhenoteFxException;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

public class MondoParser {

    private static Logger logger = LoggerFactory.getLogger(MondoParser.class);

    private String path;

    private InputStream stream;

    private Ontology mondo;
    private Ontology mondoDiseaseSubOntology;

    private final TermId DISEASEROOT = TermId.of("MONDO:0000001");

    private Map<String, String> name2IdMap;

    public MondoParser() throws PhenoteFxException {
        File dir = Platform.getPhenoteFXDir();
        String basename="mondo.obo";
        this.path = dir + File.separator + basename;
        try {
            this.stream = new FileInputStream(new File(this.path));
            mondo = parse();
            this.mondoDiseaseSubOntology = getDiseaseSubOntology();
        } catch (FileNotFoundException e) {
            logger.error(String.format("Unable to parse Mondo OBO file at %s", this.path));
            throw new PhenoteFxException(String.format("Unable to parse Mondo OBO file at %s [%s]", this.path, e.toString()));
        }

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public MondoParser(String path) throws FileNotFoundException {
        this.path = path;
        this.stream = new FileInputStream(path);
    }

    public MondoParser(InputStream stream) {
        this.stream = stream;
    }

    public Ontology parse() {
        this.mondo = OntologyLoader.loadOntology(this.stream, "MONDO");
        return this.mondo;
    }

    public Ontology getDiseaseSubOntology() {
        return this.mondo.subOntology(DISEASEROOT);
    }

    public Map<String, String> getName2IdMap() {
        if (name2IdMap != null) {
            return this.name2IdMap;
        }

        if (this.mondo == null) {
            throw new RuntimeException("mondo is null. call parse() first.");
        }

        this.name2IdMap = this.mondoDiseaseSubOntology.getTermMap().values()
                .stream()
                .collect(Collectors.toMap(Term::getName,
                        term -> term.getId().getValue(),
                        (a, b) -> a));

        return this.name2IdMap;
    }
}
