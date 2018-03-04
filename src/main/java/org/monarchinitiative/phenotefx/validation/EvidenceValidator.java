package org.monarchinitiative.phenotefx.validation;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 Peter Robinson
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

/**
 * Convenience class that is designed to validate user edits of the evidence field.
 * Evidence codes currently can only be IEA,ICE,TAS,or PCS
 * Created by robinp on 5/25/17.
 */
public class EvidenceValidator {


    public static  boolean isValid(String s) {
        if (s.equals("IEA"))
            return true;
        else if (s.equals("ICE"))
            return true;
        else if (s.equals("TAS"))
            return true;
        else if (s.equals("PCS"))
            return true;
        else
            return false;

    }


}
