
package edu.vt.resumemanager.controllers;

import edu.vt.resumemanager.utils.Methods;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/*
 The @Named class annotation designates the bean object created by this class
 as a Contexts and Dependency Injection (CDI) managed bean. The object reference
 of a CDI-managed bean can be @Inject'ed in another CDI-Managed bean so that
 the other CDI-managed bean can access the methods and properties of this bean.

 Using the Expression Language (EL) in a JSF XHTML page, you can invoke a CDI-managed
 bean's method or set/get its property by using the logical name given with the 'value'
 parameter of the @Named annotation, e.g., #{emailController.methodName() or property name}
 */
@Named(value = "emailController")
/*
 The @RequestScoped annotation indicates that the user’s interaction with
 this CDI-managed bean will be active only in a single HTTP request.
 */
@RequestScoped

public class EmailController {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String emailTo;             // Contains only one email address to send email to
    private String emailCc;             // Contains comma separated multiple email addresses with no spaces
    private String emailSubject;        // Subject line of the email message
    private String emailBody;           // Email content created in HTML format

    Properties emailServerProperties;   // java.util.Properties
    Session emailSession;               // jakarta.mail.Session
    MimeMessage htmlEmailMessage;       // jakarta.mail.internet.MimeMessage

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getEmailCc() {
        return emailCc;
    }

    public void setEmailCc(String emailCc) {
        this.emailCc = emailCc;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    /*
    =======================================================
    Create Email Session and Transport Email in HTML Format
    =======================================================
     */
    public void sendEmail() throws AddressException, MessagingException {

        // Email message content cannot be empty
        if (emailBody.isEmpty()) {
            Methods.showMessage("Error", "Please enter your email message!", "");
            return;
        }

        // Set Email Server Properties
        emailServerProperties = System.getProperties();
        emailServerProperties.put("mail.smtp.port", "587");
        emailServerProperties.put("mail.smtp.auth", "true");
        emailServerProperties.put("mail.smtp.starttls.enable", "true");

        try {
            // Create an email session using the email server properties set above
            emailSession = Session.getDefaultInstance(emailServerProperties, null);

            /*
            Create a Multi-purpose Internet Mail Extensions (MIME) style email
            message from the MimeMessage class under the email session created.
             */
            htmlEmailMessage = new MimeMessage(emailSession);

            // Set the email TO field to emailTo, which can contain only one email address
            htmlEmailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));

            if (emailCc != null && !emailCc.isEmpty()) {
                /*
                Using the setRecipients method (as opposed to addRecipient),
                the CC field is set to contain multiple email addresses
                separated by comma with no spaces in the emailCc string given.
                 */
                htmlEmailMessage.setRecipients(Message.RecipientType.CC, emailCc);
            }
            // It is okay for emailCc to be empty or null since the CC is optional

            // Set the email subject line
            htmlEmailMessage.setSubject(emailSubject);

            htmlEmailMessage.setFrom(new InternetAddress("CS5704 Fitness App <yosephcs@gmail.com>"));

            // Set the email body to the HTML type text
            htmlEmailMessage.setContent(emailBody, "text/html");

            // Create a transport object that implements the Simple Mail Transfer Protocol (SMTP)
            Transport transport = emailSession.getTransport("smtp");

            /*
            Connect to Gmail's SMTP server using the username and password provided.
            For the Gmail's SMTP server to accept the unsecure connection, the
            Cloud.Software.Engineering@gmail.com account's "Allow less secure apps" option is set to ON.
             */
            transport.connect("smtp.gmail.com", "yosephcs@gmail.com", "crywzfhppwqqjkvf");


            // Send the htmlEmailMessage created to the specified list of addresses (recipients)
            transport.sendMessage(htmlEmailMessage, htmlEmailMessage.getAllRecipients());

            // Close this service and terminate its connection
            transport.close();

            Methods.showMessage("Information", "Success!", "Email Message is Sent!");

        } catch (AddressException ae) {
            Methods.showMessage("Fatal Error", "Email Address Exception Occurred!",
                    "See: " + ae.getMessage());

        } catch (MessagingException me) {
            Methods.showMessage("Fatal Error",
                    "Email Messaging Exception Occurred! Internet Connection Required!",
                    "See: " + me.getMessage());
        }
    }

}
