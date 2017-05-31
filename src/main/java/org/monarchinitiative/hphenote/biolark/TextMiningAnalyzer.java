package org.monarchinitiative.hphenote.biolark;

/*
 * #%L
 * HPhenote
 * %%
 * Copyright (C) 2017 Peter Robinson
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Set;

/**
 * Classes that implement this interface are able to performing text-mining analysis that identifies putative HPO
 * terms the text coming from scientific publication.
 * Created by Daniel Danis on 5/30/17.
 */
public interface TextMiningAnalyzer {

    /**
     * Get PMID of the scientific publication that is the source of provided text.
     * @return String PMID
     */
    String getPmid();

    /**
     * Get set of <i>YES</i> HPO terms that were identified in provided text.
     * @return
     */
    Set<String> getYesTerms();

    /**
     * Get set of <i>NOT</i> HPO terms that were identified in provided text.
     * @return
     */
    Set<String> getNotTerms();

}
