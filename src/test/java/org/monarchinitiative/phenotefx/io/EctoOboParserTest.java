package org.monarchinitiative.phenotefx.io;

import org.junit.Test;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import static org.junit.Assert.*;

public class EctoOboParserTest {


    @Test
    public void parse() throws Exception {
        assertNotNull(EctoOboParserTest.class.getClassLoader().getResourceAsStream("ecto.obo"));
        EctoOboParser parser = new EctoOboParser(EctoOboParserTest.class.getClassLoader().getResourceAsStream("ecto.obo"));
        Ontology ecto = parser.parse();
        assertNotNull(ecto);
    }

}