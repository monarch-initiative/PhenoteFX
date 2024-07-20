package org.monarchinitiative.phenotefx.smallfile;


import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenotefx.model.PhenoRow;

import java.util.Optional;

/**
 * The annotation files are not allowed to have more than one annotation (HPO term) per PMID.
 * This  is a simple way to look for duplicates
 */
public class AnnotationUnit{


    private final HpoPmidPair hpoPmidPair;

    private final PhenoRow existingAnnot;

    private PhenoRow additionalRow = null;

    private boolean isDuplicate;

    public AnnotationUnit(HpoPmidPair pair, PhenoRow row) {
        this.hpoPmidPair = pair;
        this.existingAnnot = row;
    }


    public AnnotationUnit(String hpoId, String pmid, PhenoRow existing) {
        this.hpoPmidPair = new HpoPmidPair(hpoId, pmid);
        existingAnnot = existing;
    }

    public void addAnnotationRow(PhenoRow additional) {
        if (this.additionalRow != null) {
            // should never happen
            throw new PhenolRuntimeException("Annotation already has an existing row");
        }
        this.isDuplicate = isDuplicate(additional);
        this.additionalRow = additional;
    }

    /**
     * This is used to add a new row that does not have any correspondence in the original table
     * @param additionalRow
     */
    public void addUniqueAnnotationRow(PhenoRow additionalRow) {
        if (this.additionalRow != null) {
            // should never happen
            throw new PhenolRuntimeException("Annotation already has an existing row");
        }
        this.isDuplicate = false;
        this.additionalRow = additionalRow;
    }

    public HpoPmidPair getHpoPmidPair() {
        return hpoPmidPair;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public boolean additionalIsUnique() {
        return additionalRow != null && ! isDuplicate;
    }

    public Optional<PhenoRow> getAdditionalRow() {
        return Optional.ofNullable(additionalRow);
    }

    public boolean isDuplicate(PhenoRow additional) {
       return additional.getDiseaseIdAndPmidPair().equals(existingAnnot.getDiseaseIdAndPmidPair()) &&
                additional.getFrequency().equals(existingAnnot.getFrequency()) &&
                additional.getSex().equals(existingAnnot.getSex()) &&
            additional.getModifier().equals(existingAnnot.getModifier());
    }


    public String duplicateErrorMessage() {
        return String.format("%s (%s) frequency: %s - %s",
                this.existingAnnot.phenotypeNameProperty().get(),
                this.existingAnnot.phenotypeIDProperty().get(),
                this.existingAnnot.frequencyProperty().get(),
                this.existingAnnot.getPublication());
    }
}
