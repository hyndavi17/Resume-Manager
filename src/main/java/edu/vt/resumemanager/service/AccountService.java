
package edu.vt.resumemanager.service;

import edu.vt.resumemanager.entity.*;
import edu.vt.resumemanager.utils.*;
import jakarta.enterprise.context.*;
import jakarta.faces.context.*;
import edu.vt.resumemanager.entity.Account;
import edu.vt.resumemanager.facade.BasicInfoFacade;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.*;
import org.primefaces.model.file.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@Named("accountService")
@SessionScoped
@Data
public class AccountService implements Serializable {

    @EJB
    private BasicInfoFacade basicInfoFacade;

    /*
     * ================
     * Instance Methods
     * ================
     */
    public void recoverAccount() {
    }

    /*
     ***********************************************************************************
     * Return the Security Question Selected by the User at the Time of Account
     * Creation
     ***********************************************************************************
     */
    public String getSelectedSecurityQuestionForUsername() {
        String rv = "";

        return rv;
    }

    /*
     *****************************************************
     * Process the Submitted Answer to the Security Question
     *****************************************************
     */
    public void securityAnswerSubmit() {
    }

    /*
     **********************************************************************
     * This method is used to show a JSF (XHTML) page when the page cannot be
     * returned (e.g., return "/userAccount/Profile?faces-redirect=true";)
     * because the user's session is not yet established or is invalidated.
     **********************************************************************
     */
    public void redirectToShowJSFpage(String jsfPageUri) {
    }

    /*
     ******************************************************************
     * Initialize the Session Map to Hold Session Attributes of Interests
     ******************************************************************
     */
    public void initializeSessionMap(Account account) {
    }

    // Generate a random integer number between 100000 and 999999
    public String getRandomCodeString() {
        Random random = new Random();
        int randomNumber = random.nextInt(899999) + 100000;
        return String.format("%06d", randomNumber);
    }

    // Authenticate user with the random code sent via email or SMS
    public void authenticateUserWithCode() {
    }

    /*
     ****************************
     * SIGN IN (LOGIN) User *
     ****************************
     */
    public void loginUser() {
    }

    /*
     ****************************************************
     * Process the Username Submitted for Password Reset
     ****************************************************
     */
    public String usernameSubmit() {
        String rv = "";
        return rv;
    }

    /*
     *************************************************
     * Reset Password and Redirect to Show the Home Page
     *************************************************
     */
    public String resetPassword() {
        String rv = "";
        return rv;
    }

    public AccountService() {
    }

    public String getName(Integer id) {
        return basicInfoFacade.getBasicInfo(id).getFullName();
    }
}
