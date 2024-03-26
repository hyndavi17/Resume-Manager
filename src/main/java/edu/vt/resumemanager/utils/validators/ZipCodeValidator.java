/*
 * Created by Osman Balci on 2023.9.14
 * Copyright © 2023 Osman Balci. All rights reserved.
 */
package edu.vt.resumemanager.utils.validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/*
The @FacesValidator annotation on a class automatically registers the class with the runtime as a Validator. 
The "zipCodeValidator" attribute is the validator-id used in the JSF facelets page create.xhtml within

    <f:validator validatorId="zipCodeValidator" />

to invoke the "validate" method of the annotated class given below.
*/
@FacesValidator("zipCodeValidator")

public class ZipCodeValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        
        // Type cast the inputted "value" to String type
        String zipcode = (String) value;

        if (zipcode == null || zipcode.isEmpty()) {
            // Do not take any action. 
            // The required="true" in the XHTML file will catch this and produce an error message.
            return;
        }
        
        /* REGular EXpression (regex) for validating the U.S. Postal ZIP code
        
            ^           start of regex
            [0-9]{5}    match a digit, exactly five times
            (?:         group but don't capture
            -           match a hyphen
            [0-9]{4}    match a digit, exactly four times
            )           end of the non-capturing group
            ?           make the group optional
            $           end of regex
        */

        // REGular EXpression (regex) to validate the U.S. Postal ZIP code entered
        String regex = "^[0-9]{5}(?:-[0-9]{4})?$";
        
        if (!zipcode.matches(regex)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, 
                    "Zip Code Failed!", "The U.S. Postal ZIP code can consist of only 5 digits or "
                    + "5 digits, hyphen, and 4 digits!"));
        } 
    } 
    
}