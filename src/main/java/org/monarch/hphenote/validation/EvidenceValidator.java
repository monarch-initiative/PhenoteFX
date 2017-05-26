package org.monarch.hphenote.validation;

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
