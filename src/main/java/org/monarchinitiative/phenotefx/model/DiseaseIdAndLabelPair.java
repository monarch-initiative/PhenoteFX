package org.monarchinitiative.phenotefx.model;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

public record DiseaseIdAndLabelPair(String diseaseId, String diseaseLabel) {


    public String diseaseId() {
        if (diseaseId.startsWith("OMIM:")) {
            return diseaseId;
        } else if (diseaseId.length() == 6) {
            return "OMIM:" + diseaseId;
        } else {
            throw new PhenolRuntimeException("Malformed disease ID: " + diseaseId);
        }
    }
}
