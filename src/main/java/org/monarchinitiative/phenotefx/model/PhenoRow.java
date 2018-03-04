package org.monarchinitiative.phenotefx.model;

import javafx.beans.property.SimpleStringProperty;
import org.monarchinitiative.phenotefx.validation.EvidenceValidator;

/*
 * #%L
 * PhenoteFX
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

/**
 * This class represents one row of the Phenotype model.
 * It mirrors the structure of the 'small files' in the HPO
 * repository. The fields are
 * <ul>
 *     <li>Disease ID (e.g., OMIM:134600)</li>
 *     <li>Disease Name (e.g., FANCONI RENOTUBULAR SYNDROME)</li>
 *     <li>Gene ID (not to be used; some legacy files have information but do not add new info here)</li>
 *     <li>Gene Name (not to be used; some legacy files have information but do not add new info here)</li>
 *     <li>Genotype (not to be used; some legacy files have information but do not add new info here)</li>
 *     <li>Gene Symbol(s) (not to be used; some legacy files have information but do not add new info here)</li>
 *     <li>Phenotype ID (e.g., HP:0000093)</li>
 *     <li>Phenotype Name (e.g., Proteinuria)</li>
 *     <li>Age of Onset ID</li>
 *     <li>Age of Onset Name</li>
 *     <li>Evidence ID (e.g., IEA)</li>
 *     <li>Evidence Name (should be identical with Evidence ID)</li>
 *     <li>Frequency</li>
 *     <li>Sex ID</li>
 *     <li>Sex Name (should be identical with Sex ID)</li>
 *     <li>Negation ID (if present, NOT)</li>
 *     <li>Negation Name (should be identical with NOT id)</li>
 *     <li>Description (free text, not obligatory)</li>
 *     <li>Pub (e.g., OMIM:134600 or PMID:123456)</li>
 *     <li>Assigned by (e.g., HPO or HPO:skoehler)</li>
 *     <li>Date Created (e.g., Feb 17, 2009, not standardized at this time)</li>
 * </ul>
 * Created by robinp on 5/22/17.
 * * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.2.4 (2017-12-12)
 */
public class PhenoRow {

    private final SimpleStringProperty diseaseID;
    private final SimpleStringProperty diseaseName;
    private final SimpleStringProperty geneID;
    private final SimpleStringProperty geneName;
    private final SimpleStringProperty genotype;
    private final SimpleStringProperty geneSymbol;
    private final SimpleStringProperty phenotypeID;
    private final SimpleStringProperty phenotypeName;
    private final SimpleStringProperty ageOfOnsetID;
    private final SimpleStringProperty ageOfOnsetName;
    private final SimpleStringProperty evidenceID;
    private final SimpleStringProperty evidenceName;
    private final SimpleStringProperty frequency;
    private final SimpleStringProperty sexID;
    private final SimpleStringProperty sexName;
    private final SimpleStringProperty negationID;
    private final SimpleStringProperty negationName;
    private final SimpleStringProperty description;
    private final SimpleStringProperty pub;
    private final SimpleStringProperty assignedBy;
    private final SimpleStringProperty dateCreated;


    public PhenoRow(){
        this.diseaseID=new SimpleStringProperty("");
        this.diseaseName=new SimpleStringProperty("");
        this.geneID=new SimpleStringProperty("");
        this.geneName=new SimpleStringProperty("");
        this.genotype=new SimpleStringProperty("");
        this.geneSymbol=new SimpleStringProperty("");
        this.phenotypeID=new SimpleStringProperty("");
        this.phenotypeName=new SimpleStringProperty("");
        this.ageOfOnsetID=new SimpleStringProperty("");
        this.ageOfOnsetName=new SimpleStringProperty("");
        this.evidenceID=new SimpleStringProperty("");
        this.evidenceName=new SimpleStringProperty("");
        this.frequency=new SimpleStringProperty("");
        this.sexID=new SimpleStringProperty("");
        this.sexName=new SimpleStringProperty("");
        this.negationID=new SimpleStringProperty("");
        this.negationName=new SimpleStringProperty("");
        this.description=new SimpleStringProperty("");
        this.pub=new SimpleStringProperty("");
        this.assignedBy=new SimpleStringProperty("");
        this.dateCreated=new SimpleStringProperty("");

    }




    public String getDiseaseID() {
        return diseaseID.get();
    }

    public void setDiseaseID(String diseaseID) {
        this.diseaseID.set(diseaseID);
    }

    public String getDiseaseName() {
        return diseaseName.get();
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName.set(diseaseName);
    }

    public String getGeneID() {
        return geneID.get();
    }

    public void setGeneID(String geneID) {
        this.geneID.set(geneID);
    }

    public String getGeneName() {
        return geneName.get();
    }

    public void setGeneName(String geneName) {
        this.geneName.set(geneName);
    }

    public String getGenotype() {
        return genotype.get();
    }

    public void setGenotype(String genotype) {
        this.genotype.set(genotype);
    }

    public String getGeneSymbol() {
        return geneSymbol.get();
    }

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol.set(geneSymbol);
    }

    public String getPhenotypeID() {
        return phenotypeID.get();
    }

    public void setPhenotypeID(String phenotypeID) {
        this.phenotypeID.set(phenotypeID);
    }

    public String getPhenotypeName() {
        return phenotypeName.get();
    }

    public void setPhenotypeName(String phenotypeName) {
        this.phenotypeName.set(phenotypeName);
    }

    public String getAgeOfOnsetID() { return ageOfOnsetID.get(); }

    public void setAgeOfOnsetID(String ageOfOnsetID) {
        this.ageOfOnsetID.set(ageOfOnsetID);
    }

    public String getAgeOfOnsetName() {
        return ageOfOnsetName.get();
    }

    public void setAgeOfOnsetName(String ageOfOnsetName) {
        this.ageOfOnsetName.set(ageOfOnsetName);
    }

    public String getEvidenceID() {
        return evidenceID.get();
    }
    /** Set evidence ID and Name (the are 100% coupled) if the edit is valid. */
    public void setEvidenceID(String evidenceID) {
        if (EvidenceValidator.isValid(evidenceID)) {
            this.evidenceID.set(evidenceID);
            this.evidenceName.set(evidenceID);
        }
    }

    public String getEvidenceName() {
        return evidenceName.get();
    }

    public void setEvidenceName(String evidenceName) {
        if (EvidenceValidator.isValid(evidenceName)) {
            this.evidenceName.set(evidenceName);
            this.evidenceID.set(evidenceName);
        }
    }

    public String getFrequency() {
        return frequency.get();
    }

    public void setFrequency(String frequency) {
        this.frequency.set(frequency);
    }

    public String getSexID() {
        return sexID.get();
    }

    public void setSexID(String sexID) {
        this.sexID.set(sexID);
    }

    public String getSexName() {
        return sexName.get();
    }

    public void setSexName(String sexName) {
        this.sexName.set(sexName);
    }

    public String getNegationID() {
        return negationID.get();
    }

    public void setNegationID(String negationID) {
        this.negationID.set(negationID);
    }

    public String getNegationName() {
        return negationName.get();
    }

    public void setNegationName(String negationName) {
        this.negationName.set(negationName);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getPub() {
        return pub.get();
    }

    public void setPub(String pub) {
        this.pub.set(pub);
    }

    public String getAssignedBy() {
        return assignedBy.get();
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy.set(assignedBy);
    }

    public String getDateCreated() {
        return dateCreated.get();
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated.set(dateCreated);
    }

    public static PhenoRow constructFromLine(String line) throws Exception {
        String fields[] = line.split("\t");
        if (fields.length < 20) {
            throw new Exception(String.format("Malformed line (%s). I was expecting 21 fields but got %d.",line,fields.length));
        }
        PhenoRow prow = new PhenoRow();
        prow.setDiseaseID(fields[0]);
        prow.setDiseaseName(fields[1]);
        prow.setGeneID(fields[2]);
        prow.setGeneName(fields[3]);
        prow.setGenotype(fields[4]);
        prow.setGeneSymbol(fields[5]);
        prow.setPhenotypeID(fields[6]);
        prow.setPhenotypeName(fields[7]);
        prow.setAgeOfOnsetID(fields[8]);
        prow.setAgeOfOnsetName(fields[9]);
        prow.setEvidenceID(fields[10]);
        prow.setEvidenceName(fields[11]);
        prow.setFrequency(fields[12]);
        prow.setSexID(fields[13]);
        prow.setSexName(fields[14]);
        prow.setNegationID(fields[15]);
        prow.setNegationName(fields[16]);
        prow.setDescription(fields[17]);
        prow.setPub(fields[18]);
        prow.setAssignedBy(fields[19]);
        if (fields.length==21) { // The asusmption here is that some rows are missing their date.
            prow.setDateCreated(fields[20]);
        }
        return prow;
    }

    public String toString() {
        String s = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
        this.diseaseID.get(),
        this.diseaseName.get(),
        this.geneID.get(),
        this.geneName.get(),
        this.genotype.get(),
        this.geneSymbol.get(),
        this.phenotypeID.get(),
        this.phenotypeName.get(),
        this.ageOfOnsetID.get(),
        this.ageOfOnsetName.get(),
        this.evidenceID.get(),
        this.evidenceName.get(),
        this.frequency.get(),
        this.sexID.get(),
        this.sexName.get(),
        this.negationID.get(),
        this.negationName.get(),
        this.description.get(),
        this.pub.get(),
        this.assignedBy.get(),
        this.dateCreated.get());
        return s;
    }
}
