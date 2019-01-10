package org.monarchinitiative.phenotefx.io;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.io.utils.CurieUtilBuilder;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenotefx.exception.PhenoteFxException;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.prefixcommons.CurieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;


public class EctoParser {

    private static Logger logger = LoggerFactory.getLogger(EctoParser.class);

    private String path;

    private InputStream stream;

    private Ontology ecto;

    private Map<String, String> name2IdMap;

    public EctoParser() throws PhenoteFxException {
        File dir = Platform.getPhenoteFXDir();
        String basename="ecto.obo";
        this.path = dir + File.separator + basename;
        try {
            this.stream = new FileInputStream(path);
            //Just load ECTO terms
            this.ecto = parse();
        } catch (FileNotFoundException | PhenolException e) {
            logger.error("ecto.obo not found at " + dir);
            throw new PhenoteFxException(String.format("Unable to parse Ecto OBO file at %s [%s]", this.path, e.toString()));
        }

        try {
            this.stream.close();
        } catch (IOException e) {
            logger.warn("IO exception for closing inputstream of ecto");
        }

    }

    public EctoParser(InputStream stream) {
        this.stream = stream;
    }

    public EctoParser(String path) throws FileNotFoundException {
        this.path = path;
        this.stream = new FileInputStream(path);
    }

    public Ontology parse() throws FileNotFoundException, PhenolException {
        this.ecto = OntologyLoader.loadOntology(this.stream, "ECTO");
        return this.ecto;
    }

    public Ontology getEcto() {
        return this.ecto;
    }

    /**
     * Return a map from ecto term names to term id.
     * @return
     */
    public Map<String, String> getName2IdMap(){

        if (this.name2IdMap != null) {
            return this.name2IdMap;
        }

        this.name2IdMap = this.ecto.getTermMap().values()
                .stream()
                .collect(Collectors.toMap(
                        term -> term.getName(),
                        term -> term.getId().getValue(),
                        (a, b) -> a));
        return this.name2IdMap;
    }

}
