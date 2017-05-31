package org.monarch.hphenote.biolark;

import java.util.Set;

/**
 * Classes that implement this interface are able to performing text-mining analysis that identifies putative HPO
 * terms the text coming from scientific publication.
 * Created by Daniel Danis on 5/30/17.
 */
public interface TextMiningAnalyzer {

    /**
     * Get PMID of the scientific publication that is the source of provided text.
     *
     * @return String PMID
     */
    String getPmid();

    /**
     * Get set of <i>YES</i> HPO terms that were identified in provided text.
     *
     * @return
     */
    Set<String> getYesTerms();

    /**
     * Get set of <i>NOT</i> HPO terms that were identified in provided text.
     *
     * @return
     */
    Set<String> getNotTerms();

    /**
     * True is returned if analysis run without any problems. Check this flag before retrieval of results.
     *
     * @return
     */
    boolean getStatus();

}
