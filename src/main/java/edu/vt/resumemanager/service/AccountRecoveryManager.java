
package edu.vt.resumemanager.service;

import edu.vt.resumemanager.entity.Account;
import edu.vt.resumemanager.facade.AccountFacade;
import edu.vt.resumemanager.utils.Methods;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import edu.vt.resumemanager.utils.Password;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Data;

@Named("accountRecoveryManager")
@SessionScoped
@Data
public class AccountRecoveryManager implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String username;
    private String password;
    private String answerToSecurityQuestion;

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    UserFacade bean into the instance variable 'userFacade' after it is instantiated at runtime.
     */
    @EJB
    private AccountFacade accountFacade;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAnswerToSecurityQuestion() {
        return answerToSecurityQuestion;
    }

    public void setAnswerToSecurityQuestion(String answerToSecurityQuestion) {
        this.answerToSecurityQuestion = answerToSecurityQuestion;
    }

    /*
    ================
    Instance Methods
    ================
    */
    public void recoverAccount() {
        Methods.preserveMessages();

        String enteredUsername = username;

        // Obtain the object reference of the User object from the entered username
        Account user = accountFacade.findByUsername(enteredUsername);

        if (user == null) {
            Methods.showMessage("Fatal Error", "Unknown Username!",
                    "Entered username " + enteredUsername + " does not exist!");
        } else {
            String actualUsername = user.getUsername();
            if (actualUsername.equals(enteredUsername)) {
                /*
                 Call the getter method to obtain the user's coded password stored in the database.
                 The coded password contains the following parts:
                    "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
                 */
                String codedPassword = user.getPassword();

                // Call the getter method to get the password entered by the user
                String enteredPassword = getPassword();

                /*
                 Obtain the user's password String containing the following parts from the database
                      "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
                 Extract the actual password from the parts and compare it with the password String
                 entered by the user by using Key Stretching to prevent brute-force attacks.
                 */
                try {
                    if (Password.verifyPassword(enteredPassword, codedPassword)) {
                        // Verification Successful: Entered password = User's actual password
                        redirectToShowJSFpage("/userAccountRecovery/SecurityQuestion.xhtml");
                    } else {
                        Methods.showMessage("Fatal Error", "Invalid Password!",
                                "Please Enter a Valid Password!");
                    }
                } catch (Password.CannotPerformOperationException | Password.InvalidHashException ex) {
                    Methods.showMessage("Fatal Error",
                            "Account Recovery Manager was unable to perform its operation!",
                            "See: " + ex.getMessage());
                }
            } else {
                Methods.showMessage("Fatal Error", "Invalid Username!",
                        "Entered Username is Unknown!");
            }
        }
    }



    /*
    *****************************************************
    Process the Submitted Answer to the Security Question
    *****************************************************
     */
    public void securityAnswerSubmit() {

        // Since we will redirect to show the Profile page, invoke preserveMessages()
        Methods.preserveMessages();

        // Obtain the object reference of the User object with username
        Account user = accountFacade.findByUsername(username);

        String actualSecurityAnswer = user.getSecurityAnswer();
        String enteredSecurityAnswer = getAnswerToSecurityQuestion();

        if (actualSecurityAnswer.equals(enteredSecurityAnswer)) {
            // Answer to the security question is correct. Redirect to show the Profile page.

            // Initialize the session map with user properties of interest in the method below
            initializeSessionMap(user);
            redirectToShowJSFpage("/userAccount/Profile.xhtml");
        } else {
            Methods.showMessage("Error",
                    "Answer to the Security Question is Incorrect!", "");
        }
    }


    /*
    ****************************************
    Return the Security Question Selected by
    the User at the Time of Account Creation
    ****************************************
     */
    public String getSelectedSecurityQuestionForUsername() {

        // Obtain the object reference of the User object with username
        Account account = accountFacade.findByUsername(username);

        // Return the security question selected by the user
        return account.getSecurityQuestion();
    }



    /*
     **********************************************************************
     This method is used to show a JSF (XHTML) page when the page cannot be
     returned (e.g., return "/userAccount/Profile?faces-redirect=true";)
     because the user's session is not yet established or is invalidated.
     **********************************************************************
     */
    public void redirectToShowJSFpage(String jsfPageUri) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        /*
         getRequestContextPath() returns the URI of the webapp directory of the application.
         Obtain the URI of the JSF (XHTML) page to redirect to with respect to webapp directory.
         Example jsfPageUri: "/userAccount/Profile.xhtml"
         */
        String redirectPageURI = externalContext.getRequestContextPath() + jsfPageUri;

        // Execute the redirect to show the JSF (XHTML) page
        try {
            externalContext.redirect(redirectPageURI);
        } catch (IOException e) {
            Methods.showMessage("Fatal Error", "Unable to Navigate",
                    "External context redirect failed!");
        }
    }

    /*
    ******************************************************************
    Initialize the Session Map to Hold Session Attributes of Interests
    ******************************************************************
     */
    public void initializeSessionMap(Account account) {

        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        // Store the object reference of the signed-in user
        sessionMap.put("account", account);

        // Store the First Name of the signed-in user

        // Store the Username of the signed-in user
        sessionMap.put("username", username);

        // Store signed-in user's Primary Key in the database
        sessionMap.put("user_id", account.getId());
    }

}
