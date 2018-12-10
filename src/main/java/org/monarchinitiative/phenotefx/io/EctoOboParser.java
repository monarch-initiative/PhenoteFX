package org.monarchinitiative.phenotefx.io;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.base.OntologyOboParser;
import org.monarchinitiative.phenol.io.obo.OboOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EctoOboParser {

    private OboOntologyLoader loader;

    private String path;

    private InputStream stream;

    public EctoOboParser(String path) throws FileNotFoundException {
        this.path = path;
        this.stream = new FileInputStream(path);
    }

    public EctoOboParser(InputStream stream) {
        this.stream = stream;
    }

    public Ontology parse() throws FileNotFoundException, PhenolException {
        loader = new OboOntologyLoader(stream);
        Ontology ecto = loader.load();
        return ecto;
    }

}
