package org.monarchinitiative.phenotefx.io;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.exception.PhenoteFxException;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MondoParser {

    private static Logger logger = LoggerFactory.getLogger(MondoParser.class);

    private String path;

    private InputStream stream;

    private Ontology mondo;
    private Ontology mondoDiseaseSubOntology;

    private final TermId DISEASEROOT = TermId.of("MONDO:0000001");

    Map<String, String> name2IdMap;

    public MondoParser() throws PhenoteFxException {
        File dir = Platform.getPhenoteFXDir();
        String basename="mondo.obo";
        this.path = dir + File.separator + basename;
        try {
            this.stream = new FileInputStream(new File(this.path));
            mondo = parse();
            this.mondoDiseaseSubOntology = getDiseaseSubOntology();
        } catch (FileNotFoundException | PhenolException e) {
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

    public Ontology parse() throws FileNotFoundException, PhenolException {
        this.mondo = OntologyLoader.loadOntology(this.stream);
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
