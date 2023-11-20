package org.monarchinitiative.phenotefx.validation;

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

import org.monarchinitiative.phenotefx.model.PhenoRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SmallFileValidator {


    private final List<PhenoRow> rows;
    private final List<String> errors;


    public SmallFileValidator(List<PhenoRow> rows) {
        errors=new ArrayList<>();
        this.rows=rows;
        checkMandatoryFields();
        checkForUniqueDiseaseIds();
        checkBiocuratorEntries();
        checkFrequencyFormat();
        checkPmidFormat();
    }

    private void checkPmidFormat() {
        for (var row : rows) {
            if (row.getPublication() == null || row.getPublication().isEmpty()) {
                errors.add("Citation is empty");
            }
            String citation = row.getPublication();
            if (citation.contains(" ")) {
                errors.add("Citation not allowed to contain whitespaces");
            }
            String [] fields = citation.split(":");
            if (fields.length != 2) {
                errors.add("Malformed citation: " + citation);
            }
            // Currently, every citation must beeither OMIM or PMID or ISBN
            var allowedPrefixes = Set.of("OMIM", "PMID", "ISBN");
            if (! allowedPrefixes.contains(fields[0])) {
                errors.add("Unrecognized prefix in " + citation);
            }
            try {
                Integer i = Integer.parseInt(fields[1]);
            } catch (NumberFormatException e) {
                errors.add("Citation must have the form prefix:integer but was " + citation);
            }
        }
    }

    private void checkMandatoryFields() {
        for (var row : rows) {
            if (row.getDiseaseName() == null || row.getDiseaseName().isEmpty()) {
                errors.add("Disease name empty");
                return;
            }
            if (row.getDiseaseID() == null || row.getDiseaseID().isEmpty()) {
                errors.add("Disease id empty");
                return;
            }
            if (row.getPhenotypeID() == null || row.getPhenotypeID().isEmpty()) {
                errors.add("No phenotype ID found");
                return;
            }
            if (row.getPhenotypeLabel() == null || row.getPhenotypeLabel().isEmpty()) {
                errors.add("No phenotype label found");
                return;
            }
            if (row.getPublication() == null || row.getPublication().isEmpty()) {
                errors.add("NEmpty publication field");
                return;
            }
        }
    }

    private void checkFrequencyFormat() {
        for (var row : rows) {
            String frequency = row.getFrequency();
            if (frequency.isEmpty()) continue;
            if (frequency.startsWith("HP")) continue;
            if (frequency.endsWith("/") || frequency.startsWith("/")) {
                errors.add(String.format("Bad frequency format: %s", frequency));
            } else {
                String [] f = frequency.split("/");
                if (f.length != 2) {
                    errors.add(String.format("Bad frequency format: \"%s\"", frequency));
                } else {
                    try {
                        int m = Integer.parseInt(f[0]);
                        int n = Integer.parseInt(f[1]);
                        if (n < m) {
                            errors.add(String.format("Bad frequency: %s", frequency));
                        }
                    } catch (NumberFormatException e) {
                        errors.add(String.format("Bad frequency: %s (%s)", frequency, e.getMessage()));
                    }
                }
            }
        }
    }

    /**
     * Check that one and only one disease ID is being used.
     */
    private void checkForUniqueDiseaseIds() {
        Set<String> uniqueDiseaseIds = rows
                .stream()
                .map(PhenoRow::getDiseaseID)
                .collect(Collectors.toSet());
        if (uniqueDiseaseIds.isEmpty()) {
            errors.add("No disease ids found");
        } else if (uniqueDiseaseIds.size()>1) {
            errors.add("Multiple disease Ids found (this should be unique!): " + String.join(";",uniqueDiseaseIds));
        }
    }


    /**
     * Look for some common errors in the annotation data.
     */
    private void checkBiocuratorEntries() {
        for (PhenoRow row : rows) {
            String label = row.getPhenotypeLabel();
            String biocurator = row.getBiocuration();
            if (biocurator.isEmpty()) {
                errors.add(label+": Assigned by entry empty, but needs to be an id such as HPO:rrabbit");
            } else if (biocurator.indexOf(':') <1) {
                errors.add(label+": Malformed Assigned by string empty: needs to be an id such as HPO:rrabbit");
            }
        }
    }

    public String errorMessage() {
        return String.join(",", errors);
    }

    /**
     * @return true iff there are no error messages in {@link #errors}
     */
    public boolean isValid() {
        return errors.isEmpty();
    }


}
