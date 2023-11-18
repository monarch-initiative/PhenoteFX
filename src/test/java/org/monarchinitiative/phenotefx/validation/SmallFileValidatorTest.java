package org.monarchinitiative.phenotefx.validation;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.model.PhenoRow;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SmallFileValidatorTest {

    private final static String EMPTY_STRING = "";

    @Test
    public void testInvalidPmid() {
        PhenoRow row = new PhenoRow("OMIM:557000",
                "Pearson marrow-pancreas syndrome",
                TermId.of("HP:0001994"),
                "Renal Fanconi syndrome",
                null,
                EMPTY_STRING,
                "1/1",
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                "PMID:UNKNOWN",
                "PCS",
                "HPO:iea[2009-02-17];HPO:probinson[2022-03-27]");
        List<PhenoRow> rows = List.of(row);
        SmallFileValidator validator = new SmallFileValidator(rows);
        assertFalse(validator.isValid());
    }

    @Test
    public void testValidPmid() {
        PhenoRow row = new PhenoRow("OMIM:557000",
                "Pearson marrow-pancreas syndrome",
                TermId.of("HP:0001994"),
                "Renal Fanconi syndrome",
                null,
                EMPTY_STRING,
                "1/1",
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                "PMID:3123123",
                "PCS",
                "HPO:iea[2009-02-17];HPO:probinson[2022-03-27]");
        List<PhenoRow> rows = List.of(row);
        SmallFileValidator validator = new SmallFileValidator(rows);
        assertTrue(validator.isValid());
    }
}
