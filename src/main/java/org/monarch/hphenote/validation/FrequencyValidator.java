package org.monarch.hphenote.validation;

import org.monarch.hphenote.model.Frequency;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by peter on 27.05.17.
 */
public class FrequencyValidator {




    /**
     * A valid frequency can be an HPO term, n/m or nn%
     * @return true if s is a string like HP:0003214 */
    public static  boolean isValid(String s) {
        if (HPOValidator.isValid(s)) {
            return true;
        }
        Map<String,String> freqmap = Frequency.factory().getFrequency2NameMap();
        if (freqmap.containsKey(s))
            return true;
        if (s.equals("Rare"))
            return true; /* todo seems to be missing */
        String pattern = "\\d+/\\d+"; /* e.g., 7/12 */
        if (s.matches(pattern))
            return true;
        String pattern2 ="\\d\\d%";
        if (s.matches(pattern2))
            return true;
        return false;
    }
}
