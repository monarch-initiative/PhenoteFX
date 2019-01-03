package org.monarchinitiative.phenotefx.validation;

public class LoginValidatorDumb implements LoginValidator {

    @Override
    public boolean isValid(String username, String password) {
        return password.equals("monarch");
    }

}
