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
import javafx.beans.property.StringProperty;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * This class represents one row of the Phenotype model.
 * It mirrors the structure of the HPOA files in the HPO
 * repository (one per disease). The fields are
 * <ul>
 *     <li>Disease ID (e.g., OMIM:134600)</li>
 *     <li>Disease Name (e.g., FANCONI RENOTUBULAR SYNDROME)</li>
 *     <li>Phenotype ID (e.g., HP:0000093)</li>
 *     <li>Phenotype Name (e.g., Proteinuria)</li>
 *     <li>Age of Onset ID</li>
 *     <li>Age of Onset Name</li>
 *     <li>Frequency</li>
 *     <li>Sex</li>
 *     <li>Negation (if present, NOT)</li>
 *     <li>Modifier</li>
 *     <li>Description (free text, not obligatory)</li>
 *     <li>publication (e.g., OMIM:134600 or PMID:123456)</li>
 *     <li>Evidence (e.g., IEA)</li>
 *     <li>biocuration (e.g., HPO:skoehler[2017-02-17])</li>
 * </ul>
 * Created by robinp on 5/22/17.
 * * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class PhenoRow {
    private final StringProperty diseaseID;
    private final StringProperty diseaseName;
    private final StringProperty phenotypeID;
    private final StringProperty phenotypeName;
    private final StringProperty onsetID;
    private final StringProperty onsetName;
    private final StringProperty frequency;
    private final StringProperty sex;
    private final StringProperty negation;
    private final StringProperty modifier;
    private final StringProperty description;
    private final StringProperty publication;
    private final StringProperty evidence;
    private final StringProperty biocuration;
    /** This variable gets set to true if the user updates this row -- in this case, we will add a new
     * biocuration entry (biocuration history).
     */
    private boolean updated=false;

    private final static String EMPTY_STRING="";
    /** This will be set to the biocurator id and current date if the user modifies the current entry. */
    private String newBiocurationEntry=EMPTY_STRING;



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
                    String biocuration){
        this.diseaseID = new SimpleStringProperty(diseaseID);
        this.diseaseName = new SimpleStringProperty(diseaseName);
        this.phenotypeID=new SimpleStringProperty(phenotypeId.getValue());
        this.phenotypeName=new SimpleStringProperty(phenotypeName);
        this.onsetID=new SimpleStringProperty(ageOfOnsetId!=null?ageOfOnsetId.getValue():EMPTY_STRING);
        this.onsetName=new SimpleStringProperty(ageOfOnsetName);
        this.frequency=new SimpleStringProperty(frequencyString);
        this.sex=new SimpleStringProperty(sex);
        this.negation=new SimpleStringProperty(negation);
        this.modifier=new SimpleStringProperty(modifier);
        this.description=new SimpleStringProperty(description);
        this.publication=new SimpleStringProperty(publication);
        this.evidence=new SimpleStringProperty(evidenceCode);
        this.biocuration=new SimpleStringProperty(biocuration);

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
        this.biocuration = new SimpleStringProperty("");
    }


    public void setNewBiocurationEntry(String entry) { this.newBiocurationEntry=entry;  updated=true; }

    public String getDiseaseID() {
        return diseaseID.get();
    }

    public StringProperty diseaseIDProperty() {
        return diseaseID;
    }

    public void setDiseaseID(String diseaseID) {
        this.diseaseID.set(diseaseID);
    }

    public String getDiseaseName() {
        return diseaseName.get();
    }

    public StringProperty diseaseNameProperty() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName.set(diseaseName);
    }

    public String getPhenotypeID() {
        return phenotypeID.get();
    }

    public StringProperty phenotypeIDProperty() {
        return phenotypeID;
    }

    public void setPhenotypeID(String phenotypeID) {
        this.phenotypeID.set(phenotypeID);
    }

    public String getPhenotypeLabel() {
        return phenotypeName.get();
    }

    public StringProperty phenotypeNameProperty() {
        return phenotypeName;
    }

    public void setPhenotypeName(String phenotypeName) {
        this.phenotypeName.set(phenotypeName);
    }

    public String getOnsetID() {
        return onsetID.get();
    }

    public StringProperty onsetIDProperty() {
        return onsetID;
    }

    public void setOnsetID(String onsetID) {
        this.onsetID.set(onsetID);
    }

    public String getOnsetName() {
        return onsetName.get();
    }

    public StringProperty onsetNameProperty() {
        return onsetName;
    }

    public void setOnsetName(String onsetName) {
        this.onsetName.set(onsetName);
    }

    public String getFrequency() {
        return frequency.get();
    }

    public StringProperty frequencyProperty() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency.set(frequency);
    }

    public String getSex() {
        return sex.get();
    }

    public StringProperty sexProperty() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex.set(sex);
    }

    public String getNegation() {
        return negation.get();
    }

    public StringProperty negationProperty() {
        return negation;
    }

    public void setNegation(String negation) {
        this.negation.set(negation);
    }

    public String getModifier() {
        return modifier.get();
    }

    public StringProperty modifierProperty() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier.set(modifier);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getPublication() {
        return publication.get();
    }

    public StringProperty publicationProperty() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication.set(publication);
    }

    public String getEvidence() {
        return evidence.get();
    }

    public StringProperty evidenceProperty() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence.set(evidence);
    }

    public String getBiocuration() {
        return biocuration.get();
    }

    public StringProperty biocurationProperty() {
        return biocuration;
    }

    public void setBiocuration(String biocuration) {
        this.biocuration.set(biocuration);
    }


    /** @return string with all 14 fields, separated by a tab. Note that the biocuration entry is updated here
     * if this line was changed in the current session. */
    @Override
    public String toString() {
        String biocurationentry = this.biocuration.get();
        if (updated) {
            biocurationentry = String.format("%s;%s",this.biocuration.get(),newBiocurationEntry);
        }

        String[] s ={
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
                biocurationentry};
        return String.join("\t", s);
    }
}
