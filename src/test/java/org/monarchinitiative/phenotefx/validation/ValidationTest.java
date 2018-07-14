package org.monarchinitiative.phenotefx.validation;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationTest {


    @Test
    public void testSingleBiocurationValidity() {
        String biocuration="HPO:skoehler[2017-02-13]";
        assertTrue(BiocurationValidator.isValid(biocuration));
    }


    @Test
    public void testInvalidSingleBiocuration() {
        String biocuration="HPO:skoehler[2017-02-13";
        assertFalse(BiocurationValidator.isValid(biocuration));
    }


    @Test
    public void testDoubleBiocurationValidity() {
        String biocuration="HPO:skoehler[2017-02-13];HPO:lcarmody[2018-07-15]";
        assertTrue(BiocurationValidator.isValid(biocuration));
    }

    @Test
    public void testInvalidDoubleBiocurationValidity() {
        String biocuration="HPO:skoehler[2017-02-13;HPO:lcarmody[2018-07-15]";
        assertFalse(BiocurationValidator.isValid(biocuration));
    }


}
