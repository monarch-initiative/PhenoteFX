package org.monarchinitiative.phenotefx.io;

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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.exception.PhenoteFxException;
import org.monarchinitiative.phenotefx.model.PhenoRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SmallfileParser {
    private static final Logger logger = LogManager.getLogger();

    private final String currentPhenoteFileFullPath;
    /** The are the valid names of the head of any valid V2 small file. */
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

    private final HpoOntology ontology;

    public SmallfileParser(File file, HpoOntology onto) {
        this.currentPhenoteFileFullPath = file.getAbsolutePath();
        this.ontology=onto;
    }

    public static String getStandardHeaderLine() {
        return Arrays.stream(expectedFields).collect(Collectors.joining("\t"));
    }



    public ObservableList<PhenoRow> parse() throws PhenoteFxException {
        ObservableList<PhenoRow> phenolist = FXCollections.observableArrayList();
             try {
            BufferedReader br = new BufferedReader(new FileReader(this.currentPhenoteFileFullPath));
            String line=br.readLine();
            qcHeaderLine(line);
            while ((line=br.readLine())!=null) {
                //System.out.println(line);
                if (line.startsWith("#")) {
                    throw new PhenoteFxException(String.format("Invalid comment line in annotation file: %s",line));
                }
                String A[] = line.split("\t");
                if (A.length!= expectedFields.length) {
                    throw new PhenoteFxException(String.format("We were expecting %d fields but got %d for line %s",
                            expectedFields.length,
                            A.length,line ));
                }
                String diseaseID=A[0];
                String diseaseName=A[1];
                TermId phenotypeId = TermId.constructWithPrefix(A[2]);
                if (! ontology.getTermMap().containsKey(phenotypeId)) {
                    throw new PhenoteFxException(String.format("HPO TermId %s was not found in ontology. " +
                            "Are you using the same ontology and annotation file versions?", A[2]));
                }
                String phenotypeName=A[3];
                TermId ageOfOnsetId=null;
                if (A[4]!=null && A[4].startsWith("HP")) {
                    ageOfOnsetId=TermId.constructWithPrefix(A[4]);
                }
                String ageOfOnsetName=A[5];
                String frequencyString=A[6];
                String sex=A[7];
                String negation=A[8];
                String modifier=A[9];
                String description=A[10];
                String publication=A[11];
                String evidenceCode=A[12];
                String biocuration=A[13];

                PhenoRow row = new PhenoRow(diseaseID,diseaseName,phenotypeId,phenotypeName,ageOfOnsetId,ageOfOnsetName,
                        frequencyString,sex,negation,modifier,description,publication,evidenceCode,biocuration);
                phenolist.add(row);
                //System.err.println(row.toString());
            }
            br.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return phenolist;
    }

    /**
     * This method checks that the nead has the expected number and order of lines.
     * If it doesn't, then a serious error has occured somewhere and it is better to
     * die and figure out what is wrong than to attempt error correction
     * @param line a header line of a V2 small file
     */
    private void qcHeaderLine(String line) throws PhenoteFxException {
        String fields[] = line.split("\t");
        if (fields.length != expectedFields.length) {
            String badHeader = String.format("Malformed header line\n"+line+
                            "\nExpecting %d fields but got %d",
                    expectedFields.length,
                    fields.length);
            throw new PhenoteFxException(badHeader);
        }
        for (int i=0;i<fields.length;i++) {
            if (! fields[i].equals(expectedFields[i])) {
                String badHeader = String.format("Malformed header in file: %s\nMalformed field %s. Expected %s but got %s"
                        ,this.currentPhenoteFileFullPath,fields[i],expectedFields.length,fields.length);
                throw new PhenoteFxException(badHeader);
            }
        }
        // if we get here, all is good
    }

}
