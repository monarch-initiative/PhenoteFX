package org.monarchinitiative.phenotefx.model;

import java.util.ArrayList;
import java.util.List;

public class NewAnnotationChecker {

    private final List<PhenoRow> oldrows;

    public NewAnnotationChecker(List<PhenoRow> rows){
        this.oldrows=rows;
    }

    /**
     * We use this function to check whether a new row being added by text mining has the same
     * HPO:id as an existing annotation. If so, then we will need to decide what to do about it. If
     * not, we can just add the new annotation.
     * @param row A new phenotype annotation that is being added by text mining.
     * @return true if there is an existing row that has the same HPO:id
     */
    public boolean duplicateAnnotationExists(PhenoRow row) {
        for (PhenoRow pr: oldrows) {
            if (pr.getPhenotypeID().equals(row.getPhenotypeID())) {
                return true;
            }
        }
        return false;
    }


    public List<PhenoRow> getDuplicateRows(PhenoRow row) {
        List<PhenoRow> drows = new ArrayList<>();
        for (PhenoRow pr: oldrows) {
            if (pr.getPhenotypeID().equals(row.getPhenotypeID())) {
                drows.add(pr);
            }
        }
        return drows;
    }





}
