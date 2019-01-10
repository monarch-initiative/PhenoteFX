package org.monarchinitiative.phenotefx.io;

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