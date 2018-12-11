package org.monarchinitiative.phenotefx.io;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.obo.OboOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MondoParser {

    private OboOntologyLoader loader;

    private String path;

    private InputStream stream;

    private Ontology mondo;
    private Ontology mondoDiseaseSubOntology;

    private final TermId DISEASEROOT = TermId.of("MONDO:0000001");

    Map<String, String> name2IdMap;

    public MondoParser(String path) throws FileNotFoundException {
        this.path = path;
        this.stream = new FileInputStream(path);
    }

    public MondoParser(InputStream stream) {
        this.stream = stream;
    }

    public Ontology parse() throws FileNotFoundException, PhenolException {
        loader = new OboOntologyLoader(stream);
        this.mondo = loader.load();
        return this.mondo;
    }

    public Ontology getDiseaseSubOntology() {
        return this.mondo.subOntology(DISEASEROOT);
    }

    public Map<String, String> getName2IdMap() {
        if (name2IdMap != null) {
            return this.name2IdMap;
        }

        this.name2IdMap = new HashMap<>();
        if (this.mondo == null) {
            throw new RuntimeException("mondo is null. call parse() first.");
        }

        this.name2IdMap = this.mondoDiseaseSubOntology.getTermMap().values()
                .stream()
                .collect(Collectors.toMap(disease -> disease.getName(),
                        disease -> disease.getId().getValue()));
        return this.name2IdMap;
    }
}
