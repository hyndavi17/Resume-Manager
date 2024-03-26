

package edu.vt.resumemanager.controllers;

import edu.vt.resumemanager.entity.Account;
import edu.vt.resumemanager.entity.Configuration;
import edu.vt.resumemanager.facade.AccountFacade;
import edu.vt.resumemanager.service.AccountService;
import edu.vt.resumemanager.service.ConfigurationService;
import edu.vt.resumemanager.utils.Constants;
import edu.vt.resumemanager.utils.Methods;
import edu.vt.resumemanager.utils.Password;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.imgscalr.Scalr;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.file.UploadedFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

@Named(value = "accountController")
@SessionScoped
@Data
public class AccountController implements Serializable {

    @EJB
    private AccountFacade accountFacade;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private AccountService accountService;

    @Getter
    @Setter(AccessLevel.NONE)
    private String photoURIPrefix = Constants.FILES_URI;

    @Getter
    @Setter(AccessLevel.NONE)
    private String[] securityQuestions = Constants.SECURITY_QUESTIONS;;

    @Getter
    private Account account = new Account();

    private boolean editPassword = false;
    @Getter
    private String username;

    @Getter
    private String password;

    @Getter
    private String confirmPassword;

    @Getter
    private String oldPassword;

    @Getter
    private String securityAnswer;

    private boolean twoFAonViaEmail;

    private boolean twoFAonViaSMS;

    @Getter
    private String generatedCodeFor2FA;

    @Getter
    private int loginAttempt;
    @Getter
    private String cellPhoneCarrier = "";

    private UploadedFile file;


    ///TODO: Chane these to services
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

    public void setPhotoURIPrefix(String photoURIPrefix) {
        this.photoURIPrefix = photoURIPrefix;
    }

    public void setSecurityQuestions(String[] securityQuestions) {
        this.securityQuestions = securityQuestions;
    }


    public void togglePassword(){
        this.editPassword = !this.editPassword;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public void setTwoFAonViaEmail(boolean twoFAonViaEmail) {
        this.twoFAonViaEmail = twoFAonViaEmail;
    }

    public void setTwoFAonViaSMS(boolean twoFAonViaSMS) {
        this.twoFAonViaSMS = twoFAonViaSMS;
    }

    public void setGeneratedCodeFor2FA(String generatedCodeFor2FA) {
        this.generatedCodeFor2FA = generatedCodeFor2FA;
    }

    public void setLoginAttempt(int loginAttempt) {
        this.loginAttempt = loginAttempt;
    }

    public void setCellPhoneCarrier(String cellPhoneCarrier) {
        this.cellPhoneCarrier = cellPhoneCarrier;
    }


    /*
    ================
    Instance Methods
    ================
    */

    public Account getSignedInUser() {
        /*
         "account", the object reference of the signed-in user, was put into the SessionMap
         in the initializeSessionMap() method of LoginManager upon user's sign in.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        // Return the object reference of the signed-in User object
        return (Account) sessionMap.get("account");
    }

    /*
    **********************************
    Return True if a User is Signed In
    **********************************
     */
    public boolean userIsSignedIn() {
        return getSignedInUser() != null;
    }

    /*
    ********************************************************
      Return True if a User is Signed In with Employee Role
    ********************************************************
     */
    public boolean userIsEmployee() {
        boolean rv = false;
        if(userIsSignedIn()){
            rv = getSignedInUser().getRole().equalsIgnoreCase("employee");
        }
        return rv;
    }
    /*
    ********************************************************
      Return True if a User is Signed In with Admin Role
    ********************************************************
     */
    public boolean userIsAdmin() {
        boolean rv = false;
        if(userIsSignedIn()){
            rv = getSignedInUser().getRole().equalsIgnoreCase("admin");
        }
        return rv;
    }
    /*
    ********************************************************
      Return True if a User is Signed In with Manager Role
    ********************************************************
     */
    public boolean userIsManager() {
        boolean rv = false;
        if(userIsSignedIn()){
            rv = getSignedInUser().getRole().equalsIgnoreCase("manager");
        }
        return rv;
    }

    public boolean accountIsSuspended(Timestamp suspendedUntil){
        if(suspendedUntil == null) return false;
        // Check if the suspended_until time is in the past
        return !suspendedUntil.before(new Timestamp(System.currentTimeMillis()));
    }

    public void suspendAccount(Account account) {
        // Set suspended_until to a future date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 10); // Add 10 years to the current date

        Timestamp futureTimestamp = new Timestamp(calendar.getTimeInMillis());
        account.setSuspended_until(futureTimestamp);

        // Update the account in the database
        accountFacade.edit(account);

        Methods.showMessage("Information", "Success!",
                "User account suspended successfully!");
    }

    public void suspendAccount(Account account, int mins) {
        // Set suspended_until to a future date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, mins); // Add 10 years to the current date

        Timestamp futureTimestamp = new Timestamp(calendar.getTimeInMillis());
        account.setSuspended_until(futureTimestamp);

        // Update the account in the database
        accountFacade.edit(account);

    }

    public void activateAccount(Account account) {

        // Set suspended_until to null
        account.setSuspended_until(null);

        // Update the account in the database
        accountFacade.edit(account);

        Methods.showMessage("Information", "Success!",
                "User account activated successfully!");
    }

    /*
    **********************************************************
    Create User's Account and Redirect to Show the SignIn Page
    **********************************************************
    */
    public String createAccount() {
        /*
        ----------------------------------------------------------------
        Password and Confirm Password are validated under 3 tests:

        <1> Non-empty (tested with required="true" in JSF page)
        <2> Correct composition satisfying the regex rule.
            (tested with <f:validator validatorId="passwordValidator" /> in the JSF page)
        <3> Password and Confirm Password must match (tested below)
        ----------------------------------------------------------------
         */
        if (!account.getPassword().equals(confirmPassword)) {
            Methods.showMessage("Fatal Error", "Unmatched Passwords!",
                    "Password and Confirm Password must Match!");
            return "";
        }

        //--------------------------------------------
        // Password and Confirm Password are Validated
        //--------------------------------------------

        /*
        Redirecting to show a JSF page involves more than one subsequent requests and
        the messages would die from one request to another if not kept in the Flash scope.
        Since we will redirect to show the SignIn page, we invoke preserveMessages().
         */
        Methods.preserveMessages();

        //-----------------------------------------------------------
        // First, check if the entered username is already being used
        //-----------------------------------------------------------
        // Obtain the object reference of a User object with the username entered by the user
        Account aUser = accountFacade.findByUsername(account.getUsername());

        if (aUser != null) {
            // A user already exists with the username entered by the user
            account.setUsername("");
            Methods.showMessage("Fatal Error", "Username Already Exists!",
                    "Please Select a Different One!");
            return "";
        }

        //----------------------------------
        // The entered username is available
        //----------------------------------
        try {


            /*
             Two-Factor Authentication Status:
                 = 0 Off
                 = 1 Send random code via email
                 = 2 Send random code via text message
             */
            if (twoFAonViaEmail) {
                account.setTwoFactorAuthenticationStatus(1);  // Send random code via Email
            } else if (twoFAonViaSMS) {
                account.setTwoFactorAuthenticationStatus(2);  // Send random code via text message
            } else {
                account.setTwoFactorAuthenticationStatus(0);  // 2FA is OFF
            }
            /*
            Invoke class Password's createHash() method to convert the user-entered String
            password to a String containing the following parts
                  "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
            for secure storage and retrieval with Key Stretching to prevent brute-force attacks.
             */
            String parts = Password.createHash(account.getPassword());
            account.setPassword(parts);

            // Set the default role to employee
            account.setRole("employee");
//            if(account.getProfilePicture().isBlank()){
//                account.setProfilePicture("defaultUserPhoto.png");
//            }

            // Create the user in the database
            accountFacade.create(account);
        } catch (EJBException | Password.CannotPerformOperationException ex) {
            Methods.showMessage("Fatal Error",
                    "Something went wrong while creating user's account!",
                    "See: " + ex.getMessage());
            return "";
        }finally {
            account = new Account();
            twoFAonViaEmail = false;
            twoFAonViaSMS = false;
        }

        Methods.showMessage("Information", "Success!",
                "User Account is Successfully Created!");

        return "/account/signIn?faces-redirect=true";
    }


 /**********************************************
    Set 2FA Flags before Showing EditAccount Page
    *********************************************
            */
    public String prepareForEdit() {
        account = getSignedInUser();
        switch(account.getTwoFactorAuthenticationStatus()) {
            // 2FA is turned off
            case 0:
                twoFAonViaEmail = false;
                twoFAonViaSMS = false;
                break;
            // Send 2FA Code via Email
            case 1:
                twoFAonViaEmail = true;
                twoFAonViaSMS = false;
                break;
            // Send 2FA Code via SMS
            case 2:
                twoFAonViaEmail = false;
                twoFAonViaSMS = true;
                break;
            default:
        }
        return "/account/edit?faces-redirect=true";
    }
    /*
     ****************************
     *   SIGN IN (LOGIN) User   *
     ****************************
     */
    public void login() {
        // Since we will redirect to show the Profile page, invoke preserveMessages()
        Methods.preserveMessages();

        loginAttempt++;
        // Obtain the object reference of the User object from the entered username
        account = accountFacade.findByUsername(username);

        if (account == null) {
            Methods.showMessage("Fatal Error", "Unknown Username!",
                    "Entered username " + username + " does not exist!");
        } else {
            String actualUsername = account.getUsername();
            if (actualUsername.equals(username)) {
                /*
                 Call the getter method to obtain the user's coded password stored in the database.
                 The coded password contains the following parts:
                    "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
                 */
                String codedPassword = account.getPassword();

                // Call the getter method to get the password entered by the user
                String enteredPassword = password;

                /*
                 Obtain the user's password String containing the following parts from the database
                      "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
                 Extract the actual password from the parts and compare it with the password String
                 entered by the user by using Key Stretching to prevent brute-force attacks.
                 */
                try {
                    if (Password.verifyPassword(enteredPassword, codedPassword)) {
                        // Verification Successful: Entered password = User's actual password
                        loginAttempt = 0;

                        boolean suspended = false;
                        if(account.getSuspended_until()!=null){
                            suspended = account.getSuspended_until().after(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                        }
                        if (suspended) {
                            Methods.showMessage("Error", "Your account is suspended",
                                    "Account has been suspended try again later !");
                        } else {
                            switch (account.getTwoFactorAuthenticationStatus()) {
                                // 2FA is turned off
                                case 0:
                                    // Initialize the session map with user properties of interest in the method below
                                    initializeSessionMap(account);
                                    redirectToShowJSFpage(configurationService.getRedirectPath(account.getRole()));
                                    break;
                                // Send 2FA Code via Email
                                case 1:
                                    generatedCodeFor2FA = getRandomCodeString();

                                    String emailAddress = account.getEmail();
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

                                    String cellPhoneNumber = account.getCellPhoneNumber();
                                    textMessageController.setCellPhoneNumber(cellPhoneNumber);

                                    String cellPhoneCarrier = account.getCellPhoneCarrier();
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
                        }
                    } else {
                        if(loginAttempt>configurationService.getSuspendCount()){
                            suspendAccount(account, configurationService.getSuspendTime());
                            Methods.showMessage("Fatal Error", "Multiple invalid attempts!",
                                    "Account will be locked due to multiple unsuccessful login attempts!");
                        }
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
    // Generate a random integer number between 100000 and 999999
    public String getRandomCodeString() {
        Random random = new Random();
        int randomNumber = random.nextInt(899999) + 100000;
        return String.format("%06d", randomNumber);
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
        sessionMap.put("account", account);

        // Store the Username of the signed-in user
        sessionMap.put("username", username);

        // Store signed-in user's Primary Key in the database
        sessionMap.put("user_id", account.getId());
    }


    /*
    **********************************************
    Logout User and Redirect to Show the Home Page
    **********************************************
     */
    public void logout() {

        // Clear the signed-in User's session map
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.clear();

        // Reset the signed-in User's properties
        username = password = confirmPassword = "";
        account = new Account();

        // Since we will redirect to show the home page, invoke preserveMessages()
        Methods.preserveMessages();

        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

            // Invalidate the signed-in User's session
            externalContext.invalidateSession();

            /*
            getRequestContextPath() returns the URI of the webapp directory of the application.
            Obtain the URI of the index (home) page to redirect to.
             */
            String redirectPageURI = externalContext.getRequestContextPath() + "/index.xhtml";

            // Redirect to show the index (home) page
            externalContext.redirect(redirectPageURI);

            /*
            NOTE: We cannot use: return "/index?faces-redirect=true"; here because the user's session is invalidated.
             */
        } catch (IOException ex) {
            Methods.showMessage("Fatal Error",
                    "Unable to redirect to the index (home) page!",
                    "See: " + ex.getMessage());
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

        // Obtain the object reference of the Account object with id
        Account account = accountFacade.findByUsername(username);

        String actualSecurityAnswer = account.getSecurityAnswer();

        if (actualSecurityAnswer.equalsIgnoreCase(securityAnswer)) {
            // Answer to the security question is correct. Redirect to show the Profile page.

            // Initialize the session map with user properties of interest in the method below
            initializeSessionMap(account);
            redirectToShowJSFpage(configurationService.getRedirectPath(account.getRole()));
        } else {
            Methods.showMessage("Error",
                    "Answer to the Security Question is Incorrect!", "");
        }
    }

    public void recoverAccount() {
        Methods.preserveMessages();

        // Obtain the object reference of the User object from the entered username
        Account ac = accountFacade.findByUsername(username);

        if (ac == null) {
            Methods.showMessage("Fatal Error", "Unknown Username!",
                    "Entered username " + username + " does not exist!");
        } else {
            String actualUsername = ac.getUsername();
            if (actualUsername.equals(username)) {
                /*
                 Call the getter method to obtain the user's coded password stored in the database.
                 The coded password contains the following parts:
                    "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
                 */
                String codedPassword = ac.getPassword();

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
                        account = ac;
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

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (newValue != null && !newValue.equals(oldValue)) {
            Account editedAccount = accountFacade.getAccount(Integer.parseInt(event.getRowKey()));
            editedAccount.setRole(event.getNewValue().toString());
            accountFacade.edit(editedAccount);
            Methods.showMessage("Information", "Success!",
                    "User Role is Successfully Updated!");
        }
    }


    /*
    ***********************************************************
    Update User's Account and Redirect to Show the Profile Page
    ***********************************************************
     */
    public String updateAccount() {
        // Since we will redirect to show the Profile page, invoke preserveMessages()
        Methods.preserveMessages();
        /*
         Signed-in user's properties are changed directly in EditAccount.xhtml
         */

        try {
            if(editPassword) {
                //CHeck if the old password matches
                /*
                Invoke class Password's createHash() method to convert the user-entered String
                password to a String containing the following parts
                      "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
                for secure storage and retrieval with Key Stretching to prevent brute-force attacks.
                 */
                String parts = Password.createHash(oldPassword);
                if(!account.getPassword().equals(parts)){
                    Methods.showMessage("Error", "Error!",
                            "Wrong current password!");
                    return "/account/edit?faces-redirect=true";
                }
                if(confirmPassword.equals(password)) {
                    Methods.showMessage("Error", "Error!",
                            "Passwords don't much!");
                    return "/account/edit?faces-redirect=true";
                }
                account.setPassword(Password.createHash(password));
            }
            /*
             Two-Factor Authentication Status:
                 = 0 Off
                 = 1 Send random code via email
                 = 2 Send random code via text message
             */
            if (twoFAonViaEmail) {
                account.setTwoFactorAuthenticationStatus(1);  // Send random code via Email
            } else if (twoFAonViaSMS) {
                account.setTwoFactorAuthenticationStatus(2);  // Send random code via text message
            } else {
                account.setTwoFactorAuthenticationStatus(0);  // 2FA is OFF
            }

            // Store the changes in the database
            accountFacade.edit(account);

            Methods.showMessage("Information", "Success!",
                    "User's Account is Successfully Updated!");

        } catch (Exception ex) {
            username = "";
            Methods.showMessage("Fatal Error",
                    "Something went wrong while updating user's profile!",
                    "See: " + ex.getMessage());
            return "";
        }

        // Account update is completed, redirect to show the Profile page.
        return "/account/view?faces-redirect=true";
    }

    /*
    *****************************************************************
    Delete User's Account, Logout, and Redirect to Show the Home Page
    *****************************************************************
     */
    public void deleteAccount() {
        Methods.preserveMessages();
        /*
        The database primary key of the signed-in User object was put into the SessionMap
        in the initializeSessionMap() method of LoginManager upon user's sign in.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        int userPrimaryKey = (int) sessionMap.get("user_id");

        try {
            // Delete all photo files associated with signed-in user whose primary key is userPrimaryKey
            // deleteAllUserPhotos() is given below
//            deleteAllUserPhotos(userPrimaryKey);
//
//            // Delete all user files associated with signed-in user whose primary key is userPrimaryKey
//            // deleteAllUserFiles() is given below
//            deleteAllUserFiles(userPrimaryKey);

            // Delete the User entity from the database
            accountFacade.deleteAccount(userPrimaryKey);

            Methods.showMessage("Information", "Success!",
                    "Your Account is Successfully Deleted!");

        } catch (EJBException ex) {
            username = "";
            Methods.showMessage("Fatal Error",
                    "Something went wrong while deleting user's account!",
                    "See: " + ex.getMessage());
            return;
        }

        // Execute the logout() method given below
        logout();
    }

    /*
    **********************************************
    Logout User and Redirect to Show the Home Page
    **********************************************
     */

    /*
    ************************
    Handle User Photo Upload
    ************************
     */
    public void upload() {
        /*
        Redirecting to show a JSF page involves more than one subsequent requests and
        the messages would die from one request to another if not kept in the Flash scope.
        Since we will redirect to show the Profile page, we invoke preserveMessages().
         */
        Methods.preserveMessages();

        // Check if a file is selected
        if (file.getSize() == 0) {
            Methods.showMessage("Information", "No File Selected!",
                    "You need to choose a file first before clicking Upload.");
        }

        /*
        MIME (Multipurpose Internet Mail Extensions) is a way of identifying files on
        the Internet according to their nature and format.

        A "Content-type" is simply a header defined in many protocols, such as HTTP, that
        makes use of MIME types to specify the nature of the file currently being handled.

        Some MIME file types: (See http://www.freeformatter.com/mime-types-list.html)

            JPEG Image      = image/jpeg or image/jpg
            PNG image       = image/png
            GIF image       = image/gif
            Plain text file = text/plain
            HTML file       = text/html
            JSON file       = application/json

         Some of the MIME type mappings are specified in web.xml
         */
        // Obtain the uploaded file's MIME file type
        String mimeFileType = file.getContentType();

        if (mimeFileType.startsWith("image/")) {
            // The uploaded file is an image file
            /*
            The subSequence() method returns the portion of the mimeFileType string from the 6th
            position to the last character. Note that it starts with "image/" which has 6 characters at
            positions 0,1,2,3,4,5. Therefore, we start the subsequence at position 6 to obtain the file extension.
             */
            String fileExtension = mimeFileType.subSequence(6, mimeFileType.length()).toString();

            String fileExtensionInCaps = fileExtension.toUpperCase();

            switch (fileExtensionInCaps) {
                case "JPG":
                case "JPEG":
                case "PNG":
                case "GIF":
                    // File is an acceptable image type
                    break;
                default:
                    Methods.showMessage("Fatal Error", "Unrecognized File Type!",
                            "Selected file type is not a JPG, JPEG, PNG, or GIF!");
            }
        } else {
            Methods.showMessage("Fatal Error", "Unrecognized File Type!",
                    "Selected file type is not a JPG, JPEG, PNG, or GIF!");
        }

        storePhotoFile(file);
        // Redirect to show the Profile page
    }

    public void storePhotoFile(UploadedFile file) {

        // Since we will redirect to show the Profile page, invoke preserveMessages()
        Methods.preserveMessages();

        try {
            // Obtain the object reference of the signed-in User object
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            // Delete signedInUser's earlier uploaded photo file, its thumbnail file, and its database record.
            deletePhoto();

            // Obtain the uploaded file's MIME file type
            String mimeFileType = file.getContentType();

            // If it is an image file, obtain its file extension; otherwise, set JPEG as the file extension anyway.
            String fileExtension = mimeFileType.startsWith("image/") ? mimeFileType.subSequence(6, mimeFileType.length()).toString() : "jpeg";
            String filePath = Constants.FILES_ABSOLUTE_PATH + account.getId()+"."+fileExtension;
            String thumbnailFileName = Constants.FILES_ABSOLUTE_PATH + account.getId()+"_thumbnail."+fileExtension;

            account.setProfilePicture(Constants.FILES_URI+ account.getId()+"."+fileExtension);
            accountFacade.edit(account);


            /*
            InputStream is an abstract class, which is the superclass of all classes representing
            an input stream of bytes. It is imported as: import java.io.InputStream;
            Convert the uploaded file into an input stream of bytes.
             */
            InputStream inputStream = file.getInputStream();

            // Write the uploaded file's input stream of bytes under the photo object's
            // filename using the inputStreamToFile method given below
            File uploadedFile = inputStreamToFile(inputStream, filePath);

            // Create and save the thumbnail version of the uploaded file
            saveThumbnail(uploadedFile, thumbnailFileName,fileExtension);

            Methods.showMessage("Information", "Success!",
                    "User's Photo File is Successfully Uploaded!");

        } catch (IOException ex) {
            Methods.showMessage("Fatal Error",
                    "Something went wrong while storing the user's photo file!",
                    "See: " + ex.getMessage());
        }
    }

    /*
    ********************************************
    Store Signed-In User's Thumbnail Photo Image
    ********************************************

    When signedInUser uploads a photo, a thumbnail (small) version of the photo image
    is created in this method by using the Scalr.resize method provided in the
    imgscalr (Java Image Scaling Library) imported as imgscalr-lib-4.2.jar
     */
    private void saveThumbnail(File inputFile, String thumbnailFileName,String extension ) {

        try {
            // Buffer the photo image from the uploaded inputFile
            BufferedImage uploadedPhoto = ImageIO.read(inputFile);

            /*
            The thumbnail photo image size is set to 200x200px in Constants.java as follows:
            public static final Integer THUMBNAIL_SIZE = 200;

            If the signedInUser uploads a large photo file, we will scale it down to THUMBNAIL_SIZE
            and use it so that the size is reasonable for performance reasons.

            The photo image scaling is properly done by using the imgscalr-lib-4.2.jar file.

            The thumbnail file is named as "userId_thumbnail.fileExtension",
            e.g., 5_thumbnail.jpg for signedInUser with id 5.
             */
            // Scale the uploaded photo image to the THUMBNAIL_SIZE using imgscalr.
            BufferedImage thumbnailPhoto = Scalr.resize(uploadedPhoto, Constants.THUMBNAIL_SIZE);

            // Create the thumbnail photo file in the UserPhotoStorage directory
            File thumbnailPhotoFile = new File(thumbnailFileName);

            /*
            NOTE: ImageIO is imported as: import javax.imageio.ImageIO;
            Write the thumbnailPhoto into thumbnailPhotoFile with the file extension.
             */
            ImageIO.write(thumbnailPhoto, extension, thumbnailPhotoFile);

        } catch (IOException ex) {
            Methods.showMessage("Fatal Error",
                    "Something went wrong while saving the thumbnail file!",
                    "See: " + ex.getMessage());
        }
    }

    /*
    *******************************************************
    Delete Signed-In User's Photo and Thumbnail Image Files
    *******************************************************
     */
    public void deletePhoto() {

        // Obtain the id (primary key in the database) of the signedInUser object
        Integer primaryKey = getSignedInUser().getId();
        account.setProfilePicture(null);
        accountFacade.edit(account);
        try {
            /*
            NOTE: Files and Paths are imported as
                    import java.nio.file.Files;
                    import java.nio.file.Paths;

             Delete the user's photo and thumbnailif it exists.
             */
            String pattern = account.getId()+"{_thumbnail,}.*";
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

            Stream<Path> paths = Files.find(Path.of(Constants.FILES_ABSOLUTE_PATH), Integer.MAX_VALUE, (path, f)->matcher.matches(path));

            paths.forEach(p-> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    Methods.showMessage("Fatal Error",
                            "Something went wrong while deleting the user photo file!",
                            "See: " + e.getMessage());
                }
            });


        } catch (Exception ex) {
            Methods.showMessage("Fatal Error",
                    "Something went wrong while deleting the user photo file!",
                    "See: " + ex.getMessage());
        }

    }

    /*
    ***************************************************
    Write Given InputStream into a File with Given Name
    ***************************************************
     */
    /**
     * @param inputStream of bytes to be written into file with name targetFilename
     * @return the created file targetFile
     */
    private File inputStreamToFile(InputStream inputStream, String targetFilename) throws IOException {

        System.out.println("Saving to :" + targetFilename);
        File targetFile = null;

        try {
            /*
            inputStream.available() returns an estimate of the number of bytes that can be read from
            the inputStream without blocking by the next invocation of a method for this input stream.
            A memory buffer of bytes is created with the size of estimated number of bytes.
             */
            byte[] buffer = new byte[inputStream.available()];

            // Read the bytes of data from the inputStream into the created memory buffer.
            inputStream.read(buffer);

            // Create a new empty file with the given name targetFilename in the UserPhotoStorage directory
            targetFile = new File(targetFilename);

            // A file OutputStream is an output stream for writing data to a file
            OutputStream outStream;

            /*
            FileOutputStream is intended for writing streams of raw bytes such as image data.
            Create a new FileOutputStream for writing to the empty targetFile
             */
            outStream = new FileOutputStream(targetFile);

            // Create the targetFile in the UserPhotoStorage directory with the inputStream given
            outStream.write(buffer);

            // Close the output stream and release any system resources associated with it.
            outStream.close();

        } catch (IOException ex) {
            Methods.showMessage("Fatal Error",
                    "Something went wrong in input stream to file!",
                    "See: " + ex.getMessage());
        }

        // Return the created targetFile
        return targetFile;
    }
}
