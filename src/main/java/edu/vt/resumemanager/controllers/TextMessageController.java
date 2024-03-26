
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

@Named("textMessageController")
@RequestScoped

// This class sends a Text Message (a.k.a. SMS) to a cellular (mobile) phone
public class TextMessageController {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String cellPhoneNumber;         // Cell phone number to which Text Message to be sent
    private String cellPhoneCarrierDomain;  // Cell phone carrier company's gateway domain name
    private String mmsTextMessage;          // Text message content

    Properties emailServerProperties;       // java.util.Properties
    Session emailSession;                   // jakarta.mail.Session
    MimeMessage mimeEmailMessage;           // jakarta.mail.internet.MimeMessage

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String enteredCellPhoneNumber) {
        /*
        The input mask we used in the JSF XHTML page formats the cell phone number as
        (540) 123-4567 to make it visually easy for the user to enter the phone number.

        However, the cell phone number must be formatted only with numbers as 5401234567
        since it is required by the cell phone carrier's gateway for processing.

        Therefore, we need to remove the non-numeric characters inserted by the input mask.

        We remove all non-numeric characters from the entered cell phone number by using
        Regular Expression (RegEx). RegEx "[^0-9.]" means "if not a digit 0 to 9".
         */
        this.cellPhoneNumber = enteredCellPhoneNumber.replaceAll("[^0-9.]", "");
    }

    public String getCellPhoneCarrierDomain() {
        return cellPhoneCarrierDomain;
    }

    public void setCellPhoneCarrierDomain(String cellPhoneCarrierDomain) {
        this.cellPhoneCarrierDomain = cellPhoneCarrierDomain;
    }

    public String getMmsTextMessage() {
        return mmsTextMessage;
    }

    public void setMmsTextMessage(String mmsTextMessage) {
        this.mmsTextMessage = mmsTextMessage;
    }

    /*
    ================
    Instance Methods
    ================
     */
    public void clearTextMessage() {
        cellPhoneNumber = "";
        cellPhoneCarrierDomain = null;
        mmsTextMessage = "";
    }

    /*
    ===================================================================================
    We send the text message in an email to the cell phone carrier's MMS gateway, which
    converts the email into a text message and sends it to the subscriber's cell phone.
    ===================================================================================
     */
    public void sendTextMessage() throws AddressException, MessagingException {

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
            mimeEmailMessage = new MimeMessage(emailSession);

            /*
            All major U.S. wireless carriers offer both Short Message Service (SMS) gateway
            and Multimedia Messaging Service (MMS) gateway.

            Carrier	            SMS Gateway Domain	                MMS Gateway Domain
            AT&T	            number@txt.att.net	                number@mms.att.net
            Boost Mobile	    number@sms.myboostmobile.com	    number@myboostmobile.com
            Cricket Wireless	number@mms.cricketwireless.net	    number@mms.cricketwireless.net
            Google Project Fi	number@msg.fi.google.com	        number@msg.fi.google.com
            Republic Wireless	number@text.republicwireless.com	None
            Sprint	            number@messaging.sprintpcs.com	    number@pm.sprint.com
            Straight Talk	    number@vtext.com	                number@mypixmessages.com
            T-Mobile	        number@tmomail.net	                number@tmomail.net
            Ting	            number@message.ting.com	None
            Tracfone	        Depends on underlying carrier	    number@mmst5.tracfone.com
            U.S. Cellular	    number@email.uscc.net	            number@mms.uscc.net
            Verizon	            number@vtext.com	                number@vzwpix.com
            Virgin Mobile	    number@vmobl.com	                number@vmpix.com
            Xfinity Mobile      number@vtext.com                    number@mypixmessages.com

            SMS message is restricted to be no more than 160 characters.
            MMS has no restriction. Therefore, we will use MMS.

            Specify the email address to send the email message containing the text message as

                10-digit Cell Phone Number@CellPhoneCarrier's MMS gateway domain

            The designated cell phone number will be charged for the text messaging by its carrier.

            When the email message is sent to the cell phone number @ the MMS gateway domain,
            the message is automatically sent to the cell phone as an MMS text message by the
            domain and the carrier charges the cell phone number for the text messaging.
             */
            mimeEmailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(cellPhoneNumber + "@" + cellPhoneCarrierDomain));

            /*
             Since some cell phones may not be able to process text messages in the HTML format,
             send the email message containing the text message in Plain Text format.
             */
            mimeEmailMessage.setContent(mmsTextMessage, "text/plain");

            // Create a transport object that implements the Simple Mail Transfer Protocol (SMTP)
            Transport transport = emailSession.getTransport("smtp");

            /*
            Connect to Gmail's SMTP server using the username and password provided.
            For the Gmail's SMTP server to accept the unsecure connection, the
            Cloud.Software.Engineering@gmail.com account's "Allow less secure apps" option is set to be ON.
             */
            transport.connect("smtp.gmail.com", "yosephcs@gmail.com", "crywzfhppwqqjkvf");


            // Send the email message containing the text message to the specified email address
            transport.sendMessage(mimeEmailMessage, mimeEmailMessage.getAllRecipients());

            // Close this service and terminate its connection
            transport.close();

            Methods.showMessage("Information", "Success!", "Text Message is Sent!");
            clearTextMessage();

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
