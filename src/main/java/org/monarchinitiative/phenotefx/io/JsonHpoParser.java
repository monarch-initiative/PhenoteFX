package org.monarchinitiative.phenotefx.io;

/*-
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2021 Peter Robinson
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geneontology.obographs.model.GraphDocument;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.io.utils.CurieUtilBuilder;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.prefixcommons.CurieUtil;

import java.io.File;
import java.io.IOException;

public class JsonHpoParser {

    private final Ontology hpo;

    public JsonHpoParser(String hpoJsonPath) {
        ObjectMapper mapper = new ObjectMapper();
        // skip fields not used in OBO such as domainRangeAxioms
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            File f = new File(hpoJsonPath);
            if (! f.isFile()) {
                throw new PhenolRuntimeException("Could not file hp.json file at " + f.getAbsolutePath());
            }
            GraphDocument gdoc = mapper.readValue(f, GraphDocument.class);
            //System.out.println(gdoc.toString());
            CurieUtil curieUtil =  CurieUtilBuilder.defaultCurieUtil();
            this.hpo = OntologyLoader.loadOntology(gdoc, curieUtil, "HP");

            } catch (IOException e) {
            throw new PhenolRuntimeException(e.getLocalizedMessage());
        }
    }

    public static Ontology loadOntology(String hpoJsonPath) {
        ObjectMapper mapper = new ObjectMapper();
        // skip fields not used in OBO such as domainRangeAxioms
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            File f = new File(hpoJsonPath);
            if (! f.isFile()) {
                throw new PhenolRuntimeException("Could not file hp.json file at " + f.getAbsolutePath());
            }
            GraphDocument gdoc = mapper.readValue(f, GraphDocument.class);
            //System.out.println(gdoc.toString());
            CurieUtil curieUtil =  CurieUtilBuilder.defaultCurieUtil();
            return OntologyLoader.loadOntology(gdoc, curieUtil, "HP");

        } catch (IOException e) {
            throw new PhenolRuntimeException(e.getLocalizedMessage());
        }
    }

    public Ontology getHpo() {
        return hpo;
    }
}
