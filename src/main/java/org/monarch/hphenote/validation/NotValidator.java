package org.monarch.hphenote.validation;

/**
 * Created by robinp on 5/25/17.
 */
public class NotValidator {


    public static  boolean isValid(String s) {
        System.out.println("NotVA \""+s+"\"");
        if (s==null || s.isEmpty() || s.equals("") || s.equals("NOT"))
            return true;
        else
            return false;

    }

}
