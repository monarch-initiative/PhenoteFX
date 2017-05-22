package org.monarch.hphenote.model;

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
 */
public class PhenoRow {

    private String diseaseID;
    private String diseaseName;
    private String geneID;
    private String geneName;
    private String genotype;
    private String geneSymbol;
    private String phenotypeID;
    private String phenotypeName;
    private String ageOfOnsetID;
    private String ageOfOnsetName;
    private String evidenceID;
    private String evidenceName;
    private String frequency;
    private String sexID;
    private String sexName;
    private String negationID;
    private String negationName;
    private String description;
    private String pub;
    private String assignedBy;
    private String dateCreated;


    public PhenoRow(){
        initDefault();
    }


    private void initDefault() {
        this.diseaseID="";
        this.diseaseName="";
        this.geneID="";
        this.geneName="";
        this.genotype="";
        this.geneSymbol="";
        this.phenotypeID="";
        this.phenotypeName="";
        this.ageOfOnsetID="";
        this.ageOfOnsetName="";
        this.evidenceID="";
        this.evidenceName="";
        this.frequency="";
        this.sexID="";
        this.sexName="";
        this.negationID="";
        this.negationName="";
        this.description="";
        this.pub="";
        this.assignedBy="";
        this.dateCreated="";
    }


    public String getDiseaseID() {
        return diseaseID;
    }

    public void setDiseaseID(String diseaseID) {
        this.diseaseID = diseaseID;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getGeneID() {
        return geneID;
    }

    public void setGeneID(String geneID) {
        this.geneID = geneID;
    }

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public String getGenotype() {
        return genotype;
    }

    public void setGenotype(String genotype) {
        this.genotype = genotype;
    }

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    public String getPhenotypeID() {
        return phenotypeID;
    }

    public void setPhenotypeID(String phenotypeID) {
        this.phenotypeID = phenotypeID;
    }

    public String getPhenotypeName() {
        return phenotypeName;
    }

    public void setPhenotypeName(String phenotypeName) {
        this.phenotypeName = phenotypeName;
    }

    public String getAgeOfOnsetID() {
        return ageOfOnsetID;
    }

    public void setAgeOfOnsetID(String ageOfOnsetID) {
        this.ageOfOnsetID = ageOfOnsetID;
    }

    public String getAgeOfOnsetName() {
        return ageOfOnsetName;
    }

    public void setAgeOfOnsetName(String ageOfOnsetName) {
        this.ageOfOnsetName = ageOfOnsetName;
    }

    public String getEvidenceID() {
        return evidenceID;
    }

    public void setEvidenceID(String evidenceID) {
        this.evidenceID = evidenceID;
    }

    public String getEvidenceName() {
        return evidenceName;
    }

    public void setEvidenceName(String evidenceName) {
        this.evidenceName = evidenceName;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getSexID() {
        return sexID;
    }

    public void setSexID(String sexID) {
        this.sexID = sexID;
    }

    public String getSexName() {
        return sexName;
    }

    public void setSexName(String sexName) {
        this.sexName = sexName;
    }

    public String getNegationID() {
        return negationID;
    }

    public void setNegationID(String negationID) {
        this.negationID = negationID;
    }

    public String getNegationName() {
        return negationName;
    }

    public void setNegationName(String negationName) {
        this.negationName = negationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public static PhenoRow constructFromLine(String line) throws Exception {
        String fields[] = line.split("\t");
        if (fields.length != 21) {
            throw new Exception(String.format("Malformed line (%s). I was expected 21 fields but got %d.",line,fields.length));
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
        prow.setDateCreated(fields[20]);
        return prow;
    }
}
