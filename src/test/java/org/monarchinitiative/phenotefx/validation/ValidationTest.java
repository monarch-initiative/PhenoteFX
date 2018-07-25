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
