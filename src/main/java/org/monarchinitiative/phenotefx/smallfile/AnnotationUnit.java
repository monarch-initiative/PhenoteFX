package org.monarchinitiative.phenotefx.smallfile;

import org.monarchinitiative.phenol.ontology.data.TermId;


/**
 * The annotation files are not allowed to have more than one annotation (HPO term) per PMID.
 * This record is a simple way to look for duplicates
 * @param hpoId
 * @param pmid
 */
public record AnnotationUnit(String hpoId, String pmid) {
}
