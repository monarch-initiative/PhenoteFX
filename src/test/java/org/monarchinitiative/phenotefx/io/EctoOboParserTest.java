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

import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import static org.junit.Assert.*;

@Ignore
public class EctoOboParserTest {


    @Test
    public void parse() throws Exception {
        assertNotNull(EctoOboParserTest.class.getClassLoader().getResourceAsStream("ecto.obo"));
        EctoParser parser = new EctoParser(EctoOboParserTest.class.getClassLoader().getResourceAsStream("ecto.obo"));
        Ontology ecto = parser.parse();
        assertNotNull(ecto);
        System.out.println(ecto.getTermMap().size());
        System.out.println(ecto.getRootTermId());
        System.out.println(ecto.getTermMap().get(ecto.getRootTermId()));
        System.out.println("contains term for empty string: " + parser.getName2IdMap().containsKey(""));
        //ecto.getTermMap().values().forEach(System.out::println);
    }

}