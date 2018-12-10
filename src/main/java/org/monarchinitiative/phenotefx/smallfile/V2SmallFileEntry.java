package org.monarchinitiative.phenotefx.smallfile;

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


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by peter on 1/20/2018.
 * This class represents the contents of a single annotation line.
 */
public class  V2SmallFileEntry {
    private static final Logger logger = LogManager.getLogger();

    /** Field #1 */
    private final String diseaseID;
    /** Field #2 */
    private final String diseaseName;
    /** Field #3 */
    private final TermId phenotypeId;
    /** Field #4 */
    private final String phenotypeName;
    /** Field #5 */
    private final String ageOfOnsetId;
    /** Field #6 */
    private final String ageOfOnsetName;
    /** Field #7 can be one of N/M, X% or a valid frequency term Id */
    private final String frequencyModifier;
    /** Field #9 */
    private final String sex;
    /** Field #10 */
    private final String negation;
    /** Field #11 */
    private final String modifier;
    /** Field #12 */
    private final String description;
    /** Field #13 */
    private final String publication;
    /** Field #7 */
    private final String evidenceCode;
    /** Field #14 */
    private final String biocuration;


    /*
     private static final String[] expectedFields = {
            "#diseaseID",
            "diseaseName",
            "phenotypeID",
            "phenotypeName",
            "onsetID",
            "onsetName",
            "frequency",
            "sex",
            "negation",
            "modifier",
            "description",
            "publication",
            "evidence",
            "biocuration"};


    private final int DISEASEID_IDX=0;
    private final int DISEASENAME_IDX=1;
    private final int PHENOTYPEID_IDX=2;
    private final int PHENOTYPENAME_IDX=3;
    private final int AGEOFONSETID_IDX=4;
    private final int AGEOFONSETNAME_IDX=5;
    private final int FREQUENCY_IDX=6;
    private final int SEX_ID=7;
    private final int NEGATIVE_IDX=8;
    private final int MODIFIER_IDX=9;
    private final int DESCRIPTION_IDX=10;
    private final int PUBLICATION_IDX=11;
    private final int EVIDENCE_IDX=12;
    private final int BIOCURATION_IDX=13;
     */

    private static final String EMPTY_STRING="";

    public String getDiseaseID() {
        return diseaseID;
    }

    /** The disease ID has two parts, the database (before the :) and the id (after the :).
     * @return the database part of the diseaseID
     */
    public String getDB() {
        String[]A=diseaseID.split(":");
        return A[0];
    }
    /** The disease ID has two parts, the database (before the :) and the id (after the :).
     * @return the object_ID part of the diseaseID
     */
    public String getDB_Object_ID() {
        String[]A=diseaseID.split(":");
        if (A.length>1) return A[1];
        else return diseaseID;
    }









    public String getDiseaseName() {
        return diseaseName;
    }

    public TermId getPhenotypeId() {
        return phenotypeId;
    }

    public String getPhenotypeName() {
        return phenotypeName;
    }

    public String getAgeOfOnsetId() {
        return ageOfOnsetId;
    }

    public String getAgeOfOnsetName() {
        return ageOfOnsetName;
    }

    public String getEvidenceCode() {
        return evidenceCode;
    }

    public String getFrequencyModifier() {
        return frequencyModifier;
    }

    public String getSex() {
        return sex;
    }

    public String getNegation() {
        return negation;
    }

    public String getModifier() {
        return modifier;
    }

    public String getDescription() {
        return description;
    }

    public String getPublication() {
        return publication;
    }

    public String getBiocuration() {
        return biocuration;
    }

    /** @return true iff this entry has an N/M style frequency entry */
    boolean isNofM() {
        int i = this.frequencyModifier.indexOf('/');
        int len=this.frequencyModifier.length();
        // 1. The string must contain the character '/'
        // 2. The / cannot be on the first (0th) position of the string
        // 3. The / cannot be the last character of the string
        return (i>0 && i<len-1);
    }

    boolean isPercentage() {
        int i=this.frequencyModifier.indexOf('%');
        // percentage symbol must be present and cannot be at the first (0th) position
        if (i<1) return false;
        String regex="\\d{1,3}\\.?\\d?%";
        return this.frequencyModifier.matches(regex);
    }

    boolean isFrequencyTerm() {
        String regex="HP:\\d{7}";
        return this.frequencyModifier.matches(regex);
    }



    public void merge(String freq, List<V2SmallFileEntry> annotlist ) {


    }


    /**
     * This is the header of the V2 small files.
     * @return V2 small file header.
     */
    public static String getHeaderV2() {
        String []fields={"#diseaseID",
                "diseaseName",
                "phenotypeID",
                "phenotypeName",
                "onsetID",
                "onsetName",
                "frequency",
                "sex",
                "negation",
                "modifier",
                "description",
                "publication",
                "evidence",
                "biocuration"};
        return Arrays.stream(fields).collect(Collectors.joining("\t"));
    }

    /** @return a new V2SmallFileEntry obejct with an updated id.*/
    public V2SmallFileEntry withUpdatedPrimaryId(TermId primaryId){
        return new V2SmallFileEntry(this.diseaseID,
                this.diseaseName,
                primaryId,
                this.phenotypeName,
                this.ageOfOnsetId,
                this.ageOfOnsetName,
                this.evidenceCode,
                this.frequencyModifier,
                this.sex,
                this.negation,
                this.modifier,
                this.description,
                this.publication,
                this.biocuration);
    }

    /** @return a new V2SmallFileEntry obejct with an updated id.*/
    public V2SmallFileEntry withUpdatedLabel(String currentLabel){
        return new V2SmallFileEntry(this.diseaseID,
                this.diseaseName,
                this.phenotypeId,
                currentLabel,
                this.ageOfOnsetId,
                this.ageOfOnsetName,
                this.evidenceCode,
                this.frequencyModifier,
                this.sex,
                this.negation,
                this.modifier,
                this.description,
                this.publication,
                this.biocuration);
    }



    public static class Builder {
        /** Field #1 */
        private  final String diseaseID;
        /** Field #2 */
        private  final String diseaseName;
        /** Field #3 */
        private  final TermId phenotypeId;
        /** Field #4 */
        private  final String phenotypeName;
        /** Field #5 */
        private  String ageOfOnsetId=EMPTY_STRING;
        /** Field #6 */
        private  String ageOfOnsetName=EMPTY_STRING;
        /** Field #7 */
        private  final String evidenceCode;
        /** Field #8 -- the HPO id for frequency (if available) */
        private TermId frequencyId=null;
        /** Field #9 -- string representing n/m or x% frequency data*/
        private  String frequencyString=EMPTY_STRING;
        /** Field #10 */
        private  String sex=EMPTY_STRING;
        /** Field #11 */
        private  String negation=EMPTY_STRING;
        /** Field #12 */
        private  String modifier=EMPTY_STRING;
        /** Field #13 */
        private  String description=EMPTY_STRING;
        /** Field #14 */
        private  final String publication;
        /** Field #15 */
        private  final String biocuration;

        public Builder(String diseaseId,
                       String diseasename,
                       TermId phenoId,
                       String phenoName,
                       String evidence,
                       String pub,
                       String biocur) {
            this.diseaseID=diseaseId;
            this.diseaseName=diseasename;
            this.phenotypeId=phenoId;
            this.phenotypeName=phenoName;
            this.evidenceCode=evidence;
            this.publication=pub;
            this.biocuration=biocur;
        }

        public Builder frequencyId(TermId f) {
            this.frequencyId=f;
            return this;
        }

        public Builder frequencyString(String f) {
            this.frequencyString = f;
            return this;
        }

        public Builder ageOfOnsetId(String t) {
            this.ageOfOnsetId=t;
            return this;
        }

        public Builder ageOfOnsetName(String n) {
            this.ageOfOnsetName=n;
            return this;
        }

        public Builder sex(String s) { sex=s; return this; }

        public Builder negation(String n) { this.negation=n; return this; }

        public Builder modifier(String n) { this.modifier=n; return this; }

        public Builder description(String d) { this.description=d; return this;}

        public V2SmallFileEntry build() {
            return new V2SmallFileEntry(diseaseID,
                     diseaseName,
                     phenotypeId,
                     phenotypeName,
                     ageOfOnsetId,
                     ageOfOnsetName,
                     evidenceCode,
                     frequencyString,
                     sex,
                     negation,
                     modifier,
                     description,
                     publication,
                     biocuration);
        }
    }

    private V2SmallFileEntry(String disID,
            String diseaseName,
            TermId phenotypeId,
            String phenotypeName,
            String ageOfOnsetId,
            String ageOfOnsetName,
            String evidenceCode,
            String frequencyString,
            String sex,
            String negation,
            String modifier,
            String description,
            String publication,
            String biocuration) {
        this.diseaseID=disID;
        this.diseaseName=diseaseName;
        this.phenotypeId=phenotypeId;
        this.phenotypeName=phenotypeName;
        this.ageOfOnsetId=ageOfOnsetId;
        this.ageOfOnsetName=ageOfOnsetName;
        this.evidenceCode=evidenceCode;
        this.frequencyModifier =frequencyString;
        this.sex=sex;
        this.negation=negation;
        this.modifier=modifier;
        this.description=description;
        this.publication=publication;
        this.biocuration=biocuration;
    }

    public V2SmallFileEntry clone() {
        return new V2SmallFileEntry(this.diseaseID,
                this.diseaseName,
                this.phenotypeId,
                this.phenotypeName,
                this.ageOfOnsetId,
                this.ageOfOnsetName,
                this.evidenceCode,
                this.frequencyModifier,
                this.sex,
                this.negation,
                this.modifier,
                this.description,
                this.publication,
                this.biocuration);
    }

    /** @return the row that will be written to the V2 file for this entry. */
    @Override public String toString() { return getRow();}

    /**
     * Return the row that will be used to write the V2 small files entries to a file. Note that
     * we replace null strings (which are a signal for no data available) with the empty string
     * to avoid the string "null" being written.
     * @return One row of the "big" file corresponding to this entry
     */
    public String getRow() {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                diseaseID,
                diseaseName,
                phenotypeId.getValue(),
                phenotypeName,
                ageOfOnsetId!=null?ageOfOnsetId:EMPTY_STRING,
                ageOfOnsetName!=null?ageOfOnsetName:EMPTY_STRING,
                frequencyModifier !=null? frequencyModifier:EMPTY_STRING,
                sex!=null?sex:EMPTY_STRING,
                negation!=null?negation:EMPTY_STRING,
                modifier!=null?modifier:EMPTY_STRING,
                description!=null?description:EMPTY_STRING,
                publication!=null?publication:EMPTY_STRING,
                evidenceCode!=null?evidenceCode:"",
                biocuration);
    }



}
