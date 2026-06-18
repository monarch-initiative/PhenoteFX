package org.monarchinitiative.phenotefx.smallfile;

import org.monarchinitiative.phenotefx.model.PhenoRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class SmallFileMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmallFileMerger.class);

    private final  List<PhenoRow> existingAnnotationRows;

    private final  List<PhenoRow> additionalAnnotationRows;
    /**
     * These rows are exactly the same as an existing row -- OMIM id, HPO id, frequency, sex, modifiers
     * and we will not add them to the table in the GUI but will show a warning.
     */
    private final List<PhenoRow> duplicateAnnotationRows;

    private final List<String> errorList;
    private final  Map<HpoPmidPair, AnnotationUnit> annotationMap;

    public SmallFileMerger(List<PhenoRow> existingRows, List<PhenoRow> additionalRows) {
        existingAnnotationRows = existingRows;
        additionalAnnotationRows = additionalRows;
        duplicateAnnotationRows = new ArrayList<>();
        annotationMap = new HashMap<>();
        errorList = new ArrayList<>();
        doQualityControl();
    }



    private void checkUniqueIdentifiers() {
        Set<String> identifier = existingAnnotationRows.stream().map(PhenoRow::getDiseaseID).collect(Collectors.toSet());
        if (identifier.isEmpty() ) {
            errorList.add("Existing annotations have no disease identifier");
        } else if (identifier.size() >1) {
            String ids = String.join(";", identifier);
            errorList.add(String.format("Found multiple disease identifiers in existing annotations: %s", ids));
        }
        Set<String> identifierAdditional = additionalAnnotationRows.stream().map(PhenoRow::getDiseaseID).collect(Collectors.toSet());
        if (identifierAdditional.isEmpty() ) {
            errorList.add("Additional annotations have no disease identifier");
        } else if (identifierAdditional.size() >1) {
            String ids = String.join(";", identifier);
            errorList.add(String.format("Found multiple disease identifiers in additional annotations: %s", ids));
        }
        // if we get here we have a unique identifier in both sets
        String originalId = identifier.stream().findFirst().orElse("na");
        String additionalId = identifierAdditional.stream().findFirst().orElse("na2");
        if (! originalId.equals(additionalId)) {
            errorList.add(String.format("Existing id: %s; additional id %s.s", originalId, additionalId));
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

        for (var row : existingAnnotationRows) {
            AnnotationUnit aunit = new AnnotationUnit(row.getPhenotypeID(), row.getPublication(), row);
            annotationMap.putIfAbsent(aunit.getHpoPmidPair(), aunit);
        }
        for (var row : additionalAnnotationRows) {
            HpoPmidPair pair = new HpoPmidPair(row.getPhenotypeID(), row.getPublication());
            if (annotationMap.containsKey(pair)) {
                annotationMap.get(pair).addAnnotationRow(row);
            } else {
                AnnotationUnit aunit = new AnnotationUnit(pair, row);
                annotationMap.put(aunit.getHpoPmidPair(), aunit);
                annotationMap.get(pair).addUniqueAnnotationRow(row);
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
        rows.add("""
                <<tr>
                    <th scope="col">n</th>
                    <th scope="col">Error</th>
                  </tr>""");
        int i = 0;
        for (String row : errorList) {
            i++;
            rows.add(String.format("<tr><td>%d</td><td>%s</td></tr>", i, row));
        }
        rows.add("</table>");
        return String.join("\n", rows);
    }


    public List<PhenoRow> getNovelAdditionalRows() {
        List<PhenoRow> rows = new ArrayList<>();
        for (AnnotationUnit annotationUnit : annotationMap.values()) {
            if (annotationUnit.additionalIsUnique()) {
                Optional<PhenoRow> opt = annotationUnit.getAdditionalRow();
                rows.add(opt.get());
            } else if (annotationUnit.isDuplicate()) {
                errorList.add(annotationUnit.duplicateErrorMessage());
            }
        }
        return rows;
    }
}
