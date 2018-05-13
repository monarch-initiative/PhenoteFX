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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SmallFileValidator {


    private final List<PhenoRow> rows;
    private List<String> errors;


    public SmallFileValidator(List<PhenoRow> rows) {
        errors=new ArrayList<>();
        this.rows=rows;
        analyze();
    }

    /**
     * Look for some common errors in the annotation data.
     */
    private void analyze() {
        Set<String> diseaseIdSet=new HashSet<>(); // check that one and only one disease id is used
        for (PhenoRow row : rows) {
            String label = row.getPhenotypeName();
            String assignedby = row.getAssignedBy();
            if (assignedby.isEmpty()) {
                errors.add(label+": Assigned by entry empty, but needs to be an id such as HPO:rrabbit");
            } else if (assignedby.indexOf(':') <1) {
                errors.add(label+": Malformed Assigned by string empty: needs to be an id such as HPO:rrabbit");
            }
            String diseaseId = row.getDiseaseID();
            diseaseIdSet.add(diseaseId);

        }
        if (diseaseIdSet.isEmpty()) {
            errors.add("Could not find a disease id");
        } else if (diseaseIdSet.size()>1) {
            errors.add("Multiple disease Ids found (this should be unique!): " + diseaseIdSet.stream().collect(Collectors.joining(";")));
        }
    }




    public String errorMessage() {
        return errors.stream().collect(Collectors.joining( "," ));
    }

    /**
     * @return true iff there are no error messages in {@link #errors}
     */
    public boolean isValid() {
        return errors.isEmpty();
    }


}
