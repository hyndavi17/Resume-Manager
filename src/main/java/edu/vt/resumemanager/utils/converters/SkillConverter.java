

package edu.vt.resumemanager.utils.converters;

import edu.vt.resumemanager.controllers.AccountController;
import edu.vt.resumemanager.entity.Skill;
import edu.vt.resumemanager.facade.SkillFacade;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@FacesConverter(value = "skillConverter", managed = true)
public class SkillConverter implements Converter<Skill> {

    @Inject
    private SkillFacade skillFacade;
    @Inject
    private AccountController accountController;
    @Override
    public Skill getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return new Skill(accountController.getSignedInUser().getId(), value);
            }
            catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid skill."));
            }
        }
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Skill value) {
        if (value != null) {
            return value.getName();
        }
        else {
            return null;
        }
    }
}