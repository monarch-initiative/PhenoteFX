package org.monarchinitiative.phenotefx.smallfile;

import javafx.collections.ObservableList;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenotefx.exception.PhenoteFxException;
import org.monarchinitiative.phenotefx.model.PhenoRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class SmallFileMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmallFileMerger.class);

    private final  List<PhenoRow> existingAnnotationRows;

    private final  List<PhenoRow> additionalAnnotationRows;

    private final List<String> errorList;

    private final Ontology ontology;
    public SmallFileMerger(List<PhenoRow> existingRows, List<PhenoRow> additionalRows, Ontology ontology) {
        existingAnnotationRows = existingRows;
        additionalAnnotationRows = additionalRows;
        this.ontology = ontology;
        errorList = new ArrayList<>();
    }



    private void checkUniqueIdentifiers() {
        Set<String> identifier = existingAnnotationRows.stream().map(PhenoRow::getDiseaseID).collect(Collectors.toSet());
        if (identifier.isEmpty() ) {
            errorList.add("Existing annotations have no disease identifier");
        } else if (identifier.size() >1) {
            String ids = String.join(";", identifier);
            errorList.add(String.format("Found multiple disease identifers in existing annotations: %s", ids));
        }
        Set<String> identifierAdditional = additionalAnnotationRows.stream().map(PhenoRow::getDiseaseID).collect(Collectors.toSet());
        if (identifierAdditional.isEmpty() ) {
            errorList.add("Additional annotations have no disease identifier");
        } else if (identifierAdditional.size() >1) {
            String ids = String.join(";", identifier);
            errorList.add(String.format("Found multiple disease identifers in additional annotations: %s", ids));
        }
        // if we get here we have a unique identifier in both sets
        String originalId = identifier.stream().findFirst().orElse("na");
        String addtionalId = identifierAdditional.stream().findFirst().orElse("na2");
        if (! originalId.equals(addtionalId)) {
            errorList.add(String.format("Existing id: %s; additional id %s.s", originalId, addtionalId));
        }
    }

    private void checkUniqueDiseaseLabels() {
        Set<String> identifier = existingAnnotationRows.stream().map(PhenoRow::getDiseaseName).collect(Collectors.toSet());
        if (identifier.isEmpty() ) {
            errorList.add("Existing annotations have no disease label");
        } else if (identifier.size() >1) {
            String ids = String.join(";", identifier);
            errorList.add(String.format("Found multiple disease labels in existing annotations: %s", ids));
        }
        Set<String> identifierAdditional = additionalAnnotationRows.stream().map(PhenoRow::getDiseaseName).collect(Collectors.toSet());
        if (identifierAdditional.isEmpty() ) {
            errorList.add("Additional annotations have no disease label");
        } else if (identifierAdditional.size() >1) {
            String ids = String.join(";", identifier);
            errorList.add(String.format("Found multiple disease labels in additional annotations: %s", ids));
        }
        // if we get here we have a unique identifier in both sets
        String originalLabel = identifier.stream().findFirst().orElse("na");
        String additionalLabel = identifierAdditional.stream().findFirst().orElse("na2");
        if (! originalLabel.equals(additionalLabel)) {
            errorList.add(String.format("Existing id: %s; additional id %s.s", originalLabel, additionalLabel));
        }
    }


    private void checkForDuplicates() {
        Map<AnnotationUnit, List<PhenoRow>> annotationMap = new HashMap<>();
        for (var row : existingAnnotationRows) {
            AnnotationUnit aunit = new AnnotationUnit(row.getPhenotypeID(), row.getPublication());
            annotationMap.putIfAbsent(aunit, new ArrayList<>());
            annotationMap.get(aunit).add(row);
        }
        for (var row : additionalAnnotationRows) {
            AnnotationUnit aunit = new AnnotationUnit(row.getPhenotypeID(), row.getPublication());
            annotationMap.putIfAbsent(aunit, new ArrayList<>());
            annotationMap.get(aunit).add(row);
        }
        for (var e: annotationMap.entrySet()) {
            List<PhenoRow> rowlist = e.getValue();
            if (rowlist.size() > 1) {
                AnnotationUnit aunit = e.getKey();
                errorList.add(String.format("Found multiple entries for %s (%s) - see following lines", aunit.hpoId(), aunit.pmid()));
                for (var row : rowlist) {
                    errorList.add(row.toString());
                }
            }
        }
    }


    public boolean hasError() {
        return errorList.size() > 0;
    }

    public void doQualityControl() {
        checkUniqueIdentifiers();
        checkUniqueDiseaseLabels();
        if (hasError()) return; // different ids, no use in continuing
        checkForDuplicates();
    }


    public String getErrorHtml() {
        if (! hasError()) {
            return "<p>No errors found.</p>";
        }
        List<String> rows = new ArrayList<>();
        rows.add(String.format("<h1>%d Errors found merging HPOA data</h1>", errorList.size()));
        rows.add("<table>");
        rows.add("<<tr>\n" +
                "    <th scope=\"col\">n</th>\n" +
                "    <th scope=\"col\">Error</th>\n" +
                "  </tr>");
        int i = 0;
        for (String row : errorList) {
            i++;
            rows.add(String.format("<tr><td>%d</td><td>%s</td></tr>", i, row));
        }
        rows.add("</table>");
        return String.join("\n", rows);
    }


}
