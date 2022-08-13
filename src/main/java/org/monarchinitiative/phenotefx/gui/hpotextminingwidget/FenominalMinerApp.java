package org.monarchinitiative.phenotefx.gui.hpotextminingwidget;


import org.monarchinitiative.fenominal.core.impl.lexical.LexicalResources;
import org.monarchinitiative.fenominal.core.impl.textmapper.ClinicalTextMapper;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
import org.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import org.monarchinitiative.hpotextmining.core.miners.TermMiner;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FenominalMinerApp implements TermMiner {

    private static final Logger LOGGER = LoggerFactory.getLogger(FenominalMinerApp.class);

    private final Ontology ontology;
    private final org.monarchinitiative.fenominal.core.TermMiner miner;

    public FenominalMinerApp(Ontology ontology) {
        this.ontology = ontology;
        miner = org.monarchinitiative.fenominal.core.TermMiner.defaultNonFuzzyMapper(ontology);
    }

    /**
     * @param query Query string for mining HPO terms (for instance, text that was pasted into the GUI window for mining).
     * @return collection of mined HPO terms to display in the GUI
     */
    @Override
    public Collection<MinedTerm> doMining(final String query) {
        Collection<MinedTermWithMetadata> hits = miner.mineTermsWithMetadata(query);
        LOGGER.trace("Retrieved {} mining hits", hits.size());
        List<MinedTerm> mtcollection=new ArrayList<>();
        for (var x : hits) {
            MinedTerm mterm = new org.monarchinitiative.hpotextmining.core.miners.SimpleMinedTerm(x.getBegin(), x.getEnd(), x.getTermId().getValue(), x.isPresent());
            mtcollection.add(mterm);
        }
        return mtcollection;
    }

    public Ontology getHpo() {
        return this.ontology;
    }


}
