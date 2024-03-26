
package edu.vt.resumemanager.service;

import edu.vt.resumemanager.controllers.AccountController;
import edu.vt.resumemanager.controllers.AttachmentController;
import edu.vt.resumemanager.controllers.ProfileController;
import edu.vt.resumemanager.dto.Profile;
import edu.vt.resumemanager.entity.Attachment;
import edu.vt.resumemanager.entity.Account;
import edu.vt.resumemanager.facade.AttachmentFacade;
import edu.vt.resumemanager.utils.Constants;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import java.io.*;
import java.util.List;
import java.util.Map;

@Named("fileUploadManager")
@SessionScoped
public class FileUploadManager implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */

    // Used by PrimeFaces fileUpload
    private UploadedFile uploadedFile;

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    AttachmentFacade bean into the instance variable 'attachmentFacade' after it is instantiated at runtime.
     */
    @EJB
    private AttachmentFacade attachmentFacade;

    /*
    The @Inject annotation directs the CDI Container Manager to inject (store) the object reference of the
    CDI container-managed AttachmentController bean into the instance variable 'attachmentController' after it is instantiated at runtime.
     */
    @Inject
    private AttachmentController attachmentController;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private AccountController accountController;

    @Inject
    private ProfileController profileController;
    /*
    =========================
    Getter and Setter Methods
    =========================
     */

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public AttachmentFacade getAttachmentFacade() {
        return attachmentFacade;
    }

    public void setAttachmentFacade(AttachmentFacade attachmentFacade) {
        this.attachmentFacade = attachmentFacade;
    }

    public AttachmentController getAttachmentController() {
        return attachmentController;
    }

    public void setAttachmentController(AttachmentController attachmentController) {
        this.attachmentController = attachmentController;
    }

    /*
    ================
    Instance Methods
    ================
     */

    /*
     **************************
     *   Handle File Upload   *
     **************************
     */
    public String handleFileUpload(FileUploadEvent event) throws IOException {

        // This sets the necessary flag to ensure the messages are preserved.
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

        try {
            /*
            "user", the object reference of the signed-in user, was put into the SessionMap
            in the initializeSessionMap() method of LoginManager upon user's sign in.
             */
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            Account signedInAccount = accountController.getSignedInUser();

            /*
            To associate the file to the user, record "userId_filename" in the database.
            Since each file has its own primary key (unique id), the user can upload
            multiple files with the same name.
             */
            String userId_filename = signedInAccount.getId() + "_" + event.getFile().getFileName();

            /*
            "The try-with-resources statement is a try statement that declares one or more resources. 
            A resource is an object that must be closed after the program is finished with it. 
            The try-with-resources statement ensures that each resource is closed at the end of the
            statement." [Oracle] 
             */
            try (InputStream inputStream = event.getFile().getInputStream();) {
                /*
                 The method inputStreamToFile given below stores the uploaded file into a file
                 with filename userId_filename
                 */
                inputStreamToFile(inputStream, userId_filename);
            }

            // Instantiate a new Attachment entity object using the constructor method
            Attachment newAttachment = new Attachment(userId_filename, signedInAccount);

            /*
            ----------------------------------------------------------------
            If the userId_filename was used before, delete the earlier file.
            ----------------------------------------------------------------
             */
            List<Attachment> filesFound = attachmentFacade.findByFilename(userId_filename);

            // If the userId_filename already exists in the database, the filesFound List will not be empty.
            if (!filesFound.isEmpty()) {
                // Remove the file with the same name from the database
                attachmentFacade.remove(filesFound.get(0));
            }

            // Create the new Attachment entity (row) in the database
            attachmentFacade.create(newAttachment);

            // Ask the AttachmentController bean to refresh the files list
            attachmentController.refreshAttachmentList();


            FacesMessage infoMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Success!", "File(s) Uploaded Successfully!");
            FacesContext.getCurrentInstance().addMessage(null, infoMessage);

        } catch (IOException e) {
            FacesMessage fatalErrorMessage = new FacesMessage(FacesMessage.SEVERITY_FATAL,
                    "Something went wrong during file upload!", "See: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, fatalErrorMessage);
        }
        
        return "/profile/attachment/ListAttachments?faces-redirect=true";
    }

    /*
     ***************************
     *   Write Uploaded File   *
     ***************************
     */
    // Writes the uploaded file into the Constants.FILES_ABSOLUTE_PATH directory
    private File inputStreamToFile(InputStream inputStream, String file_name) throws IOException {

        // Read the series of bytes from the input stream into the buffer
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);

        // Set target file to be located in Constants.FILES_ABSOLUTE_PATH directory with given name file_name
        File targetFile = new File(Constants.FILES_ABSOLUTE_PATH, file_name);

        // Declare local variable
        OutputStream outStream;

        // Set output stream to be the target file
        outStream = new FileOutputStream(targetFile);

        // Write the series of bytes from the buffer into the output stream which is the target file
        outStream.write(buffer);
        outStream.close();

        return targetFile;
    }



}
