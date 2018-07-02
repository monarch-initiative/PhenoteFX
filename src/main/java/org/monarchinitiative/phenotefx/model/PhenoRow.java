package org.monarchinitiative.phenotefx.model;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2018 Peter Robinson
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

import javafx.beans.property.SimpleStringProperty;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This class represents one row of the Phenotype model. We are using the new V2 small file format (which was
 * introduced in March 2018).
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
 */
public class PhenoRow {
    private final SimpleStringProperty diseaseID;
    private final SimpleStringProperty diseaseName;
    private final SimpleStringProperty phenotypeID;
    private final SimpleStringProperty phenotypeName;
    private final SimpleStringProperty onsetID;
    private final SimpleStringProperty onsetName;
    private final SimpleStringProperty frequency;
    private final SimpleStringProperty sex;
    private final SimpleStringProperty negation;
    private final SimpleStringProperty modifier;
    private final SimpleStringProperty description;
    private final SimpleStringProperty publication;
    private final SimpleStringProperty evidence;
    private final SimpleStringProperty assignedBy;
    private final SimpleStringProperty dateCreated;

    private final static String EMPTY_STRING="";

    public PhenoRow(String diseaseID,
                    String diseaseName,
                    TermId phenotypeId ,
                    String phenotypeName,
                    TermId ageOfOnsetId,
                    String ageOfOnsetName,
                    String frequencyString,
                    String sex,
                    String negation,
                    String modifier,
                    String description,
                    String publication,
                    String evidenceCode,
                    String assignedBy,
                    String dateCreated){
        this.diseaseID = new SimpleStringProperty(diseaseID);
        this.diseaseName = new SimpleStringProperty(diseaseName);
        this.phenotypeID=new SimpleStringProperty(phenotypeId.getIdWithPrefix());
        this.phenotypeName=new SimpleStringProperty(phenotypeName);
        this.onsetID=new SimpleStringProperty(ageOfOnsetId!=null?ageOfOnsetId.getIdWithPrefix():EMPTY_STRING);
        this.onsetName=new SimpleStringProperty(ageOfOnsetName);
        this.frequency=new SimpleStringProperty(frequencyString);
        this.sex=new SimpleStringProperty(sex);
        this.negation=new SimpleStringProperty(negation);
        this.modifier=new SimpleStringProperty(modifier);
        this.description=new SimpleStringProperty(description);
        this.publication=new SimpleStringProperty(publication);
        this.evidence=new SimpleStringProperty(evidenceCode);
        this.assignedBy=new SimpleStringProperty(assignedBy);
        this.dateCreated=new SimpleStringProperty(dateCreated);

    }
    public PhenoRow() {
        this.diseaseID = new SimpleStringProperty("");
        this.diseaseName = new SimpleStringProperty("");
        this.phenotypeID = new SimpleStringProperty("");
        this.phenotypeName = new SimpleStringProperty("");
        this.onsetID = new SimpleStringProperty("");
        this.onsetName = new SimpleStringProperty("");
        this.frequency = new SimpleStringProperty("");
        this.sex = new SimpleStringProperty("");
        this.negation = new SimpleStringProperty("");
        this.modifier = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.publication = new SimpleStringProperty("");
        this.evidence = new SimpleStringProperty("");
        this.assignedBy = new SimpleStringProperty("");
        this.dateCreated = new SimpleStringProperty("");
    }




    public String getDiseaseID() {
        return diseaseID.get();
    }

    public SimpleStringProperty diseaseIDProperty() {
        return diseaseID;
    }

    public void setDiseaseID(String diseaseID) {
        this.diseaseID.set(diseaseID);
    }

    public String getDiseaseName() {
        return diseaseName.get();
    }

    public SimpleStringProperty diseaseNameProperty() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName.set(diseaseName);
    }

    public String getPhenotypeID() {
        return phenotypeID.get();
    }

    public SimpleStringProperty phenotypeIDProperty() {
        return phenotypeID;
    }

    public void setPhenotypeID(String phenotypeID) {
        this.phenotypeID.set(phenotypeID);
    }

    public String getPhenotypeName() {
        return phenotypeName.get();
    }

    public SimpleStringProperty phenotypeNameProperty() {
        return phenotypeName;
    }

    public void setPhenotypeName(String phenotypeName) {
        this.phenotypeName.set(phenotypeName);
    }

    public String getOnsetID() {
        return onsetID.get();
    }

    public SimpleStringProperty onsetIDProperty() {
        return onsetID;
    }

    public void setOnsetID(String onsetID) {
        this.onsetID.set(onsetID);
    }

    public String getOnsetName() {
        return onsetName.get();
    }

    public SimpleStringProperty onsetNameProperty() {
        return onsetName;
    }

    public void setOnsetName(String onsetName) {
        this.onsetName.set(onsetName);
    }

    public String getFrequency() {
        return frequency.get();
    }

    public SimpleStringProperty frequencyProperty() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency.set(frequency);
    }

    public String getSex() {
        return sex.get();
    }

    public SimpleStringProperty sexProperty() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex.set(sex);
    }

    public String getNegation() {
        return negation.get();
    }

    public SimpleStringProperty negationProperty() {
        return negation;
    }

    public void setNegation(String negation) {
        this.negation.set(negation);
    }

    public String getModifier() {
        return modifier.get();
    }

    public SimpleStringProperty modifierProperty() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier.set(modifier);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getPublication() {
        return publication.get();
    }

    public SimpleStringProperty publicationProperty() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication.set(publication);
    }

    public String getEvidence() {
        return evidence.get();
    }

    public SimpleStringProperty evidenceProperty() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence.set(evidence);
    }

    public String getAssignedBy() {
        return assignedBy.get();
    }

    public SimpleStringProperty assignedByProperty() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy.set(assignedBy);
    }

    public String getDateCreated() {
        return dateCreated.get();
    }

    public SimpleStringProperty dateCreatedProperty() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated.set(dateCreated);
    }




    /** @return string with all 15 fields, separated by a tab */
    @Override
    public String toString() {
        String s[]  ={
                this.diseaseID.get(),
                this.diseaseName.get(),
                this.phenotypeID.get(),
                this.phenotypeName.get(),
                this.onsetID.get(),
                this.onsetName.get(),
                this.frequency.get(),
                this.sex.get(),
                this.negation.get(),
                this.modifier.get(),
                this.description.get(),
                this.publication.get(),
                this.evidence.get(),
                this.assignedBy.get(),
                this.dateCreated.get()};
        return Arrays.stream(s).collect(Collectors.joining("\t"));
    }
}
