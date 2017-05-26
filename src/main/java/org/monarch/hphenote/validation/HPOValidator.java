package org.monarch.hphenote.validation;

/**
 * Created by robinp on 5/26/17.
 */
public class HPOValidator {

    /** @return true if s is a string like HP:0003214 */
    public static  boolean isValid(String s) {
        if (s.length() != 10) {
            return false;
        }
        if (! s.startsWith("HP:"));
        try {
            Integer i = Integer.parseInt(s.substring(3));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


}
