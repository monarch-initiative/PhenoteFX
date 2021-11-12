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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by robinp on 5/25/17.
 */
public class BiocurationValidator {


    public static  boolean isValid(String s) {
        String[] fields =s.split(";");
        if (fields.length<1) return false;
        for (String f : fields) {
            if (! isValidEntry(f))
                return false;
        }
        return true;
    }

    /**
     * CHeck whether one entry, e.e., HPO:skoehler[2017-02-17], is valid
     * @param field
     * @return true if entry is OK
     */
    private static boolean isValidEntry(String field) {
        //
        String regex = "\\w+:\\w+\\[\\d{4}-\\d{2}-\\d{2}\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(field);
        return matcher.matches();
    }



}
