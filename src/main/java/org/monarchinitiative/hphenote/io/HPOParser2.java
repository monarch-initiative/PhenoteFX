package org.monarchinitiative.hphenote.io;

import com.github.phenomics.ontolib.formats.hpo.HpoDiseaseAnnotation;
import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.formats.hpo.HpoTermRelation;
import com.github.phenomics.ontolib.io.obo.hpo.HpoOboParser;
import com.github.phenomics.ontolib.ontology.data.*;
import org.apache.log4j.Logger;
import org.monarchinitiative.hphenote.gui.ExceptionDialog;
import org.monarchinitiative.hphenote.gui.Platform;
import org.monarchinitiative.hphenote.model.HPO;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class uses the ontolib library to parse the HPO file and to provide the data structures needed to populate the
 * GUI with HPO terms and names.
 * TODO replace old HPOParser class once this is stable and working.
 * @author Peter Robinson
 * @version 0.0.1
 */
public class HPOParser2 {
    static Logger logger = Logger.getLogger(HPOParser2.class.getName());
    /** The absolute path of the hp.obo file that will be parsed in. */
    private File hpoPath =null;
    /** Key: an HPO id, such as HP:0001234; value: corresponding {@link HPO} object. */
    private Map<String,HPO> hpoMap=null;
    /** key: an HPO label; value: corresponding HP id, e.g., HP:0001234 */
    private Map<String,String> hpoName2IDmap=null;
    /** Key: any label (can be a synonym). Value: corresponding main preferred label. */
    public Map<String,String> hpoSynonym2PreferredLabelMap;
    /** Ontology objert with just the inheritance terms. */
    private Ontology<HpoTerm, HpoTermRelation> inheritance=null;
    /** Ontology object with just the phenotypic abnormality terms. */
    private Ontology<HpoTerm, HpoTermRelation> abnormalPhenoSubOntology=null;


    public HPOParser2() {
        File dir = Platform.getHPhenoteDir();
        String basename="hp.obo";
        this.hpoPath = new File(dir + File.separator + basename);
        this.hpoMap=new HashMap<>();
        hpoName2IDmap=new HashMap<>();
        this.hpoSynonym2PreferredLabelMap=new HashMap<>();
        inputFile();
    }

    /** @return a Map of HPO terms. THe Map will be initialized but empty if the hp.obo file cannot be parsed. */
    public Map<String,HPO> getTerms() {
        return this.hpoMap;
    }
    public Map<String,String> getHpoName2IDmap() { return this.hpoName2IDmap; }
    public Map<String,String> getHpoSynonym2PreferredLabelMap() { return hpoSynonym2PreferredLabelMap; }

    public Ontology<HpoTerm, HpoTermRelation> getAbnormalPhenoSubOntology() { return abnormalPhenoSubOntology; }


    /**
     * Inputs the hp.obo file and fills {@link #hpoMap} with the contents.
     */
    private void inputFile() {
        HpoOntology hpo;
        TermPrefix pref = new ImmutableTermPrefix("HP");
        TermId inheritId = new ImmutableTermId(pref,"0000005");
        try {
            HpoOboParser hpoOboParser = new HpoOboParser(hpoPath);
            hpo = hpoOboParser.parse();
            this.abnormalPhenoSubOntology = hpo.getPhenotypicAbnormalitySubOntology();
            this.inheritance = hpo.subOntology(inheritId);
        } catch (IOException e) {
            logger.error(String.format("Unable to parse HPO OBO file at %s", hpoPath.getAbsolutePath() ));
            logger.error(e,e);
            ExceptionDialog.display(e.toString());
            return;
        }
        Map<TermId,HpoTerm> termmap=hpo.getTermMap();
        System.err.println("Term IDs in phenotypic abnormality sub ontology");
        for (TermId termId : abnormalPhenoSubOntology.getNonObsoleteTermIds()) {
            HpoTerm hterm = termmap.get(termId);
            String label = hterm.getName();
            String id = hterm.getId().toString();
            HPO hp = new HPO();
            hp.setHpoId(id);
            hp.setHpoName(label);

            hpoName2IDmap.put(label,id);
            this.hpoMap.put(id,hp);
            this.hpoSynonym2PreferredLabelMap.put(label,label);
            List<TermSynonym> syns = hterm.getSynonyms();
            for (TermSynonym syn : syns ) {
                String synlabel = syn.getValue();
                this.hpoSynonym2PreferredLabelMap.put(synlabel, label);
            }
        }

    }












    public Ontology<HpoTerm, HpoTermRelation> getInheritanceSubontology() {
        Map<TermId,HpoTerm> submap = inheritance.getTermMap();
        Set<TermId> actual = inheritance.getNonObsoleteTermIds();
        for (TermId t:actual) {
            System.out.println("INHERITANCE GOT TERM "+ submap.get(t).getName());
        }
        return this.inheritance;
    }










}
