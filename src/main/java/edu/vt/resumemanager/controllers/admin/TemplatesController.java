

package edu.vt.resumemanager.controllers.admin;

import edu.vt.resumemanager.entity.Template;
import edu.vt.resumemanager.facade.TemplateFacade;
import edu.vt.resumemanager.utils.JsfUtil;
import edu.vt.resumemanager.utils.Methods;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
---------------------------------------------------------------------------
The @Named (jakarta.inject.Named) annotation indicates that the objects
instantiated from this class will be managed by the Contexts and Dependency
Injection (CDI) container. The name "templateController" is used within
Expression Language (EL) expressions in JSF (XHTML) facelets pages to
access the properties and invoke methods of this class.
---------------------------------------------------------------------------
 */
@Named("templatesController")

/*
The @SessionScoped annotation preserves the values of the TemplatesController
object's instance variables across multiple HTTP request-response cycles
as long as the user's established HTTP session is alive.
 */
@SessionScoped
/*
-----------------------------------------------------------------------------
Marking the TemplateController class as "implements Serializable" implies that
instances of the class can be automatically serialized and deserialized.

Serialization is the process of converting a class instance (object)
from memory into a suitable format for storage in a file or memory buffer,
or for transmission across a network connection link.

Deserialization is the process of recreating a class instance (object)
in memory from the format under which it was stored.
-----------------------------------------------------------------------------
 */
/*
 * The lombok annotations @Data, @AllArgsConstructor, @NoArgsConstructor
 * allow the creation of getter, setters methods and constructors at compile time
 * */
@Data
public class TemplatesController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
    */

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    TemplateFacade bean into the instance variable 'templateFacade' after it is instantiated at runtime.
     */
    @EJB
    private TemplateFacade templateFacade;

    // List of object references of Template objects
    @Getter(AccessLevel.NONE)
    private List<Template> listOfTemplates = null;

    // selected = object reference of a selected Template object
    private Template selected;

    public List<Template> getListOfTemplates() {
        if (listOfTemplates == null) {
            listOfTemplates = templateFacade.findAll();
        }
        return listOfTemplates;
    }


    /*
     ****************************************
     *   Unselect Selected Template Object   *
     ****************************************
     */
    public void unselect() {
        selected = null;
    }

    /*
     *************************************
     *   Cancel and Display List.xhtml   *
     *************************************
     */
    public String cancel() {
        // Unselect previously selected template object if any
        selected = null;
        return "/admin/templates/list.xhtml?faces-redirect=true";
    }

    /*
     ***************************************
     *   Prepare to Create a New Template   *
     ***************************************
     */
    public void prepareCreate() {
        /*
        Instantiate a new Template object and store its object reference into
        instance variable 'selected'. The Template class is defined in Template.java
         */
        selected = new Template();
    }

    /*
     **************************************************
     *   CREATE a New Template Breed in the Database   *
     **************************************************
     */
    public void create() {
        Methods.preserveMessages();
        /*
        An enum is a special Java type used to define a group of constants.
        The constants CREATE, DELETE and UPDATE are defined as follows in JsfUtil.java

                public enum PersistAction {
                    CREATE,
                    DELETE,
                    UPDATE
                }
         */

        /*
         The object reference of the template to be created is stored in the instance variable 'selected'
         See the persist method below.
         */
        persist(JsfUtil.PersistAction.CREATE, "Template was Successfully Created!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The CREATE operation is successfully performed.
            selected = null;            // Remove selection
            listOfTemplates = null;     // Invalidate listOfTemplateBreeds to trigger re-query.
        }
    }

    /*
     ***********************************************
     *   UPDATE Selected Template in the Database   *
     ***********************************************
     */
    public void update() {
        Methods.preserveMessages();
        /*
         The object reference of the template to be updated is stored in the instance variable 'selected'
         See the persist method below.
         */
        persist(JsfUtil.PersistAction.UPDATE, "Template was Successfully Updated!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The UPDATE operation is successfully performed.
            selected = null;        // Remove selection
            listOfTemplates = null;  // Invalidate listOfTemplateBreeds to trigger re-query.
        }
    }

    /*
     *************************************************
     *   DELETE Selected Template from the Database   *
     *************************************************
     */
    public void destroy(Template temp) {
        selected = temp;
        Methods.preserveMessages();
        /*
         The object reference of the template to be deleted is stored in the instance variable 'selected'
         See the persist method below.
         */
        persist(JsfUtil.PersistAction.DELETE, "Template was Successfully Deleted!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The DELETE operation is successfully performed.
            selected = null;            // Remove selection
            listOfTemplates = null;     // Invalidate list of templates to trigger re-query.
        }
    }

    /*
     *************************************************
     *   Suspend Selected Template from the Database  *
     *************************************************
     */
    public void suspend() {
        Methods.preserveMessages();
        /*
         The object reference of the template to be deleted is stored in the instance variable 'selected'
         See the persist method below.
         */
        //selected.setStatus();
        persist(JsfUtil.PersistAction.UPDATE, "Template was Successfully Suspended!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The DELETE operation is successfully performed.
            selected = null;            // Remove selection
            listOfTemplates = null;     // Invalidate list of templates to trigger re-query.
        }
    }

    /*
     **********************************************************************************************
     *   Perform CREATE, UPDATE (EDIT), and DELETE (DESTROY, REMOVE) Operations in the Database   *
     **********************************************************************************************
     */

    /**
     * @param persistAction  refers to CREATE, UPDATE (Edit) or DELETE action
     * @param successMessage displayed to inform the user about the result
     */
    private void persist(JsfUtil.PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != JsfUtil.PersistAction.DELETE) {
                    /*
                     -------------------------------------------------
                     Perform CREATE or EDIT operation in the database.
                     -------------------------------------------------
                     The edit(selected) method performs the SAVE (STORE) operation of the "selected"
                     object in the database regardless of whether the object is a newly
                     created object (CREATE) or an edited (updated) object (EDIT or UPDATE).

                     templateFacade inherits the edit(selected) method from the AbstractFacade class.
                     */
                    templateFacade.edit(selected);
                } else {
                    /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove(selected) method performs the DELETE operation of the "selected"
                     object in the database.

                     templateFacade inherits the remove(selected) method from the AbstractFacade class.
                     */
                    templateFacade.remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, "A persistence error occurred!");
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, "A persistence error occurred");
            }
        }
    }
}