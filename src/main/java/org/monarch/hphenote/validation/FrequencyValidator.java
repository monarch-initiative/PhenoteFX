package org.monarch.hphenote.validation;

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
        if (s.matches("\\w+"))
            return true; // A string like obbligate TODO -- be specific!
        String pattern = "\\d+/\\d+"; /* e.g., 7/12 */
        if (s.matches(pattern))
            return true;
        String pattern2 ="\\d\\d%";
        if (s.matches(pattern2))
            return true;
        return false;
    }
}
