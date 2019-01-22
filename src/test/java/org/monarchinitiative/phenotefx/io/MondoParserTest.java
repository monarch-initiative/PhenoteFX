package org.monarchinitiative.phenotefx.io;

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