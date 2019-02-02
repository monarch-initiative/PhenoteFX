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
import java.io.InputStream;

import static org.junit.Assert.*;

@Ignore
public class MondoParserTest {
    @Test
    public void parse() throws Exception {
        InputStream mondoStream = MondoParserTest.class.getClassLoader().getResourceAsStream("mondo.obo");
        MondoParser parser = new MondoParser(mondoStream);
        Ontology mondo = parser.parse();
        assertNotNull(mondo);

        mondo.getTermMap().values().stream().filter(t -> t.getName().toLowerCase().contains("exposure"))
                .forEach(System.out::print);

    }

}