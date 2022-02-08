package org.monarchinitiative.phenotefx.model;

import org.monarchinitiative.phenotefx.gui.PopUps;

/**
 * A class to store some data about the current disease model.
 */
public class PhenoteModel {

    private String biocuratorId = null;
    private String diseaseLabel = null;
    private String diseaseId = null;

    private String currentPercentage = null;


    public PhenoteModel() {

    }

    public String getBiocuratorId() {
        return biocuratorId;
    }

    public void setBiocuratorId(String biocuratorId) {
        if (biocuratorId == null) {
            this.biocuratorId = "";
            return;
        }
        if (biocuratorId.contains("\\")) {
            PopUps.showInfoMessage("Error","Attempt to set biocurator id with slash");
            return;
        }
        this.biocuratorId = biocuratorId;
    }

    public String getDiseaseLabel() {
        return diseaseLabel;
    }

    public void setDiseaseLabel(String diseaseLabel) {
        this.diseaseLabel = diseaseLabel;
    }

    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
    }

    public void setCurrentPercentage(String p) {
        this.currentPercentage = p;
    }
    public String getCurrentPercentage() {
        return currentPercentage;
    }
}
