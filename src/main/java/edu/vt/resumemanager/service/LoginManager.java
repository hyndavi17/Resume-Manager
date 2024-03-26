
package edu.vt.resumemanager.service;

import edu.vt.resumemanager.controllers.TextMessageController;
import edu.vt.resumemanager.controllers.EmailController;
import edu.vt.resumemanager.entity.Account;
import edu.vt.resumemanager.facade.AccountFacade;
import edu.vt.resumemanager.utils.Methods;

import java.io.IOException;
import java.io.Serializable;

import edu.vt.resumemanager.utils.Password;
import jakarta.ejb.EJB;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import java.util.Map;
import java.util.Random;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.mail.MessagingException;
import lombok.Data;
import lombok.Getter;

@Named("loginManager")
@SessionScoped
@Data
public class LoginManager implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    @Getter
    private int loginAttempt = 0;

    @Getter
    private String username;


    @Getter
    private String password;

    @Getter
    private String generatedCodeFor2FA;
    @Getter
    private String userEnteredCodeFor2FA;

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    UserFacade bean into the instance variable 'userFacade' after it is instantiated at runtime.
     */
    @EJB
    private AccountFacade accountFacade;

    /*
    The @Inject annotation directs the CDI Container Manager to inject (store) the object reference of the
    CDI container-managed EmailController bean into the instance variable 'emailController' after it is instantiated at runtime.
     */
    @Inject
    private EmailController emailController;

    /*
    The @Inject annotation directs the CDI Container Manager to inject (store) the object reference of the
    CDI container-managed TextMessageController bean into the instance variable 'textMessageController' after it is instantiated at runtime.
     */
    @Inject
    private TextMessageController textMessageController;

    public void setLoginAttempt(int loginAttempt) {
        this.loginAttempt = loginAttempt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGeneratedCodeFor2FA(String generatedCodeFor2FA) {
        this.generatedCodeFor2FA = generatedCodeFor2FA;
    }

    public void setUserEnteredCodeFor2FA(String userEnteredCodeFor2FA) {
        this.userEnteredCodeFor2FA = userEnteredCodeFor2FA;
    }
    /*
     ****************************
     *   SIGN IN (LOGIN) User   *
     ****************************
     */
    public void loginUser() {
        // Since we will redirect to show the Profile page, invoke preserveMessages()
        Methods.preserveMessages();

        // Obtain the object reference of the User object from the entered username
        Account user = accountFacade.findByUsername(username);

        if (user == null) {
            Methods.showMessage("Fatal Error", "Unknown Username!",
                    "Entered username " + username + " does not exist!");
        } else {
            String actualUsername = user.getUsername();
            if (actualUsername.equals(username)) {
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

                        switch (user.getTwoFactorAuthenticationStatus()) {
                            // 2FA is turned off
                            case 0:
                                // Initialize the session map with user properties of interest in the method below
                                initializeSessionMap(user);
                                redirectToShowJSFpage("/userAccount/Profile.xhtml");
                                break;
                            // Send 2FA Code via Email
                            case 1:
                                generatedCodeFor2FA = getRandomCodeString();

                                String emailAddress = user.getEmail();
                                emailController.setEmailTo(emailAddress);
                                emailController.setEmailSubject("Your Authentication Code");
                                emailController.setEmailBody("Your Authentication Code: " + generatedCodeFor2FA);
                                try {
                                    emailController.sendEmail();
                                } catch (MessagingException e) {
                                    Methods.showMessage("Fatal Error", "Unable to Send Email",
                                            "Unable to send authentication code to " + emailAddress);
                                }

                                redirectToShowJSFpage("/account/TwoFactorAuthentication.xhtml");
                                break;
                            // Send 2FA Code via SMS
                            case 2:
                                generatedCodeFor2FA = getRandomCodeString();

                                String cellPhoneNumber = user.getCellPhoneNumber();
                                textMessageController.setCellPhoneNumber(cellPhoneNumber);

                                String cellPhoneCarrier = user.getCellPhoneCarrier();
                                textMessageController.setCellPhoneCarrierDomain(cellPhoneCarrier);

                                textMessageController.setMmsTextMessage("Your Authentication Code: " + generatedCodeFor2FA);
                                try {
                                    textMessageController.sendTextMessage();
                                } catch (MessagingException e) {
                                    Methods.showMessage("Fatal Error", "Unable to Send SMS",
                                            "Unable to send authentication code to " + cellPhoneNumber);
                                }

                                redirectToShowJSFpage("/account/TwoFactorAuthentication.xhtml");
                                break;
                            default:
                                // Take no action
                        }
                    } else {
                        Methods.showMessage("Fatal Error", "Invalid Password!",
                                "Please Enter a Valid Password!");
                    }
                } catch (Password.CannotPerformOperationException | Password.InvalidHashException ex) {
                    Methods.showMessage("Fatal Error",
                            "Password Manager was unable to perform its operation!",
                            "See: " + ex.getMessage());
                }
            } else {
                Methods.showMessage("Fatal Error", "Invalid Username!",
                        "Entered Username is Unknown!");
            }
        }
    }

/*
    ================
    Instance Methods
    ================
    */

    // Generate a random integer number between 100000 and 999999
    public String getRandomCodeString() {
        Random random = new Random();
        int randomNumber = random.nextInt(899999) + 100000;
        return String.format("%06d", randomNumber);
    }

    // Authenticate user with the random code sent via email or SMS
    public void authenticateUserWithCode() {
        Methods.preserveMessages();
        if (userEnteredCodeFor2FA.equals(generatedCodeFor2FA)) {
            userEnteredCodeFor2FA = null;
            Account account = accountFacade.findByUsername(username);
            // Initialize the session map with user properties of interest in the method below
            initializeSessionMap(account);
            redirectToShowJSFpage("/userAccount/Profile.xhtml");
        } else {
            Methods.showMessage("Fatal Error", "Code Does Not Match!",
                    "Entered code " + userEnteredCodeFor2FA + " does not match!");
        }
    }



    /*
     **********************************************************************
     This method is used to show a JSF (XHTML) page when the page cannot be
     returned, e.g., return "/userAccount/Profile?faces-redirect=true";
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
        sessionMap.put("user", account);

        // Store the Username of the signed-in user
        sessionMap.put("username", username);

        // Store signed-in user's Primary Key in the database
        sessionMap.put("user_id", account.getId());
    }

}
