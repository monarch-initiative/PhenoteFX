package org.monarchinitiative.phenotefx.gui.hpotextminingwidget;

import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.lexical.LexicalResources;
import org.monarchinitiative.fenominal.core.textmapper.ClinicalTextMapper;
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
    private final ClinicalTextMapper mapper;

    public FenominalMinerApp(Ontology ontology) {
        LexicalResources lexicalResources = new LexicalResources();
        this.mapper = new ClinicalTextMapper(ontology, lexicalResources);
    }

    /**
     * @param query Query string for mining HPO terms (for instance, text that was pasted into the GUI window for mining).
     * @return collection of mined HPO terms to display in the GUI
     */
    @Override
    public Collection<MinedTerm> doMining(final String query) {
        List<MappedSentencePart> mappedSentenceParts = mapper.mapText(query);
        LOGGER.trace("Retrieved {} mapped sentence parts ", mappedSentenceParts.size());
        List<MinedTerm> mtcollection=new ArrayList<>();
        for (var x : mappedSentenceParts) {
            MinedTerm mterm = new org.monarchinitiative.hpotextmining.core.miners.SimpleMinedTerm(x.getStartpos(), x.getEndpos(), x.getTid().getValue(), true);
        mtcollection.add(mterm);
        }
        //return mappedSentenceParts.stream().map(SimpleMinedTerm::fromMappedSentencePart).collect(Collectors.toList());
        return mtcollection;
    }

    public Ontology getHpo() {
        return this.mapper.getHpo();
    }


}
