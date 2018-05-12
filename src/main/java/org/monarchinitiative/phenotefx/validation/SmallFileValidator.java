package org.monarchinitiative.phenotefx.validation;

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
