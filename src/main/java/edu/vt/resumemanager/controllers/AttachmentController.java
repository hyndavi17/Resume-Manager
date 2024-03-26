
package edu.vt.resumemanager.controllers;

import edu.vt.resumemanager.entity.Account;
import edu.vt.resumemanager.entity.Attachment;
import edu.vt.resumemanager.facade.AttachmentFacade;
import edu.vt.resumemanager.service.ConfigurationService;
import edu.vt.resumemanager.utils.JsfUtil;
import edu.vt.resumemanager.utils.JsfUtil.PersistAction;
import edu.vt.resumemanager.utils.Methods;
import edu.vt.resumemanager.utils.Constants;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("attachmentController")
@SessionScoped
public class AttachmentController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    AttachmentFacade bean into the instance variable 'userFileFacade' after it is instantiated at runtime.
     */
    @EJB
    private AttachmentFacade attachmentFacade;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private AccountController accountController;

    @Inject
    private ProfileController profileController;



    // 'selected' contains the object reference of the selected Attachment object
    private Attachment selected;

    // 'listOfAttachments' is a List containing the object references of Attachment objects
    private List<Attachment> listOfAttachments = null;

    /*
    cleanedFileNameHashMap<KEY, VALUE>
        KEY   = Integer fileId
        VALUE = String cleanedFileNameForSelected
     */
    HashMap<Integer, String> cleanedFileNameHashMap = null;

    // Selected row number in p:dataTable in ListAttachments.xhtml
    private String selectedRowNumber = "0";

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public Attachment getSelected() {
        return selected;
    }

    public void setSelected(Attachment selected) {
        this.selected = selected;
    }

    public String getSelectedRowNumber() {
        return selectedRowNumber;
    }

    public void setSelectedRowNumber(String selectedRowNumber) {
        this.selectedRowNumber = selectedRowNumber;
    }

    /*
    ***************************************************************
    Return the List of Account Files that Belong to the Signed-In Account
    ***************************************************************
     */
    public List<Attachment> getListOfAttachments() {

        if (listOfAttachments == null) {
            Account signedInAccount = accountController.getSignedInUser();

            // Obtain the database primary key of the signedInAccount object
            Integer primaryKey = signedInAccount.getId();

            // Obtain only those files from the database that belong to the signed-in user
            listOfAttachments = attachmentFacade.findAttachmentsByAccount(primaryKey);

            // Instantiate a new hash map object
            cleanedFileNameHashMap = new HashMap<>();

            /*
            cleanedFileNameHashMap<KEY, VALUE>
                KEY   = Integer fileId
                VALUE = String cleanedFileNameForSelected
             */
            listOfAttachments.forEach(attachment -> {

                // Obtain the filename stored in CloudStorage/FileStorage as 'userId_filename'
                String storedFileName = attachment.getName();

                // Remove the "userId_" (e.g., "4_") prefix in the stored filename
                String cleanedFileName = storedFileName.substring(storedFileName.indexOf("_") + 1);

                // Obtain the file database Primary Key id
                Integer fileId = attachment.getId();

                // Create an entry in the hash map as a key-value pair
                cleanedFileNameHashMap.put(fileId, cleanedFileName);
            });
        }
        return listOfAttachments;
    }

    /*
    ================
    Instance Methods
    ================
     */

    // The constants CREATE, DELETE and UPDATE are defined in JsfUtil.java

    /*
    **********************
    Create a New Account File
    **********************
     */
    public void create() {
        persist(PersistAction.CREATE,"Account Attachment was Successfully Created!");
        /*
        JsfUtil.isValidationFailed() returns TRUE if the validationFailed() method has been called
        for the current request. Return of FALSE means that the create operation was successful and 
        we can reset listOfAttachments to null so that it will be recreated with the newly created user file.
         */
        if (!JsfUtil.isValidationFailed()) {
            selected = null;            // Remove selection
            listOfAttachments = null;     // Invalidate listOfAttachments to trigger re-query.
        }
    }

    // We do not allow update of a user file.
    public void update() {
        persist(PersistAction.UPDATE,"Account Attachment was Successfully Updated!");
    }

    /*
     ****************************************************************************
     *   Perform CREATE, EDIT (UPDATE), and DELETE Operations in the Database   *
     ****************************************************************************
     */
    /**
     * @param persistAction refers to CREATE, UPDATE (Edit) or DELETE action
     * @param successMessage displayed to inform the user about the result
     */
    private void persist(PersistAction persistAction, String successMessage) {

        if (selected != null) {
            try {
                if (persistAction != PersistAction.DELETE) {
                    /*
                     -------------------------------------------------
                     Perform CREATE or EDIT operation in the database.
                     -------------------------------------------------
                     The edit(selected) method performs the SAVE (STORE) operation of the "selected"
                     object in the database regardless of whether the object is a newly
                     created object (CREATE) or an edited (updated) object (EDIT or UPDATE).
                    
                     AttachmentFacade inherits the edit(selected) method from the AbstractFacade class.
                     */
                    attachmentFacade.edit(selected);
                } else {
                    /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove method performs the DELETE operation in the database.
                    
                     AttachmentFacade inherits the remove(selected) method from the AbstractFacade class.
                     */
                    attachmentFacade.remove(selected);
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
                    JsfUtil.addErrorMessage(ex,"A Persistence Error Occurred!");
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex,"A Persistence Error Occurred!");
            }
        }
    }

    public Attachment getAttachment(Integer id) {
        return attachmentFacade.find(id);
    }

    @FacesConverter(forClass = Attachment.class)
    public static class AttachmentControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AttachmentController controller = (AttachmentController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "attachmentController");
            return controller.getAttachment(getKey(value));
        }

        Integer getKey(String value) {
            int key;
            key = Integer.parseInt(value);
            return key;
        }

        String getStringKey(Integer value) {
            return String.valueOf(value);
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Attachment) {
                Attachment o = (Attachment) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                        "object {0} is of type {1}; expected type: {2}",
                        new Object[]{object, object.getClass().getName(), Attachment.class.getName()});
                return null;
            }
        }
    }

    /*
    =========================
    Delete Selected Account File
    =========================
     */
    public String deleteSelectedAttachment() {

        Attachment attachmentToDelete = selected;
        /*
        We need to preserve the messages since we will redirect to show a
        different JSF page after successful deletion of the user file.
         */
        Methods.preserveMessages();

        if (attachmentToDelete == null) {
            Methods.showMessage("Fatal Error", "No File Selected!", "You do not have a file to delete!");
            return "";
        } else {
            try {
                // Delete the file from CloudStorage/FileStorage
                Files.deleteIfExists(Paths.get(attachmentToDelete.getFilePath()));

                // Delete the user file record from the database
                attachmentFacade.remove(attachmentToDelete);
                // AttachmentFacade inherits the remove() method from AbstractFacade

                Methods.showMessage("Information", "Success!", "Selected File is Successfully Deleted!");

                // See method below
                refreshAttachmentList();

                return "/profile/attachment/ListAttachments?faces-redirect=true";

            } catch (IOException ex) {
                Methods.showMessage("Fatal Error", "Something went wrong while deleting the user file!",
                        "See: " + ex.getMessage());
                return "";
            }
        }
    }

    /*
    ========================
    Refresh Account's File List
    ========================
     */
    public void refreshAttachmentList() {
        /*
        By setting the listOfAttachments to null, we force the getListOfAttachments
        method above to retrieve all of the user's files again.
         */
        selected = null;            // Remove selection
        listOfAttachments = null;     // Invalidate listOfAttachments to trigger re-query.
        profileController.refreshAttachment();
    }

    /*
    =======================================
    Return Image or Icon based on File Type
    =======================================
    fileId is the database primary key value for a user file
    Return image if it is an image file; otherwise, return file type icon

    Any type of file can be uploaded or downloaded.
    We identify the types of files that can be displayed or played in the web browser.
    */
    public String AttachmentTypeIcon(Integer fileId) {

        // Obtain the object reference of the Attachment whose primary key = fileId
        Attachment attachment = attachmentFacade.getAttachment(fileId);

        // Obtain the attachment's filename as it is stored in the CloudDrive DB database
        String imageAttachmentName = attachment.getName();

        // Extract the file extension from the filename
        String fileExtension = imageAttachmentName.substring(imageAttachmentName.lastIndexOf(".") + 1);

        // Convert file extension to uppercase
        String fileExtensionInCaps = fileExtension.toUpperCase();

        switch (fileExtensionInCaps) {
            case "JPG":
            case "JPEG":
            case "PNG":
            case "GIF":
                // If it is an image file, return the image file URI
                return Constants.FILES_URI + imageAttachmentName;
            case "MP4":
            case "OGG":
            case "WEBM":
                // If it is a video file, return the videoFile icon
                return "/resources/images/videoFile.png";
            default:
                // If it is another file type, return the viewFile icon
                return "/resources/images/viewFile.png";
        }
    }

    /*
    =====================================
    Return Cleaned Filename given File Id
    =====================================
     */
    public String cleanedAttachmentForAttachmentId(Integer fileId) {
        /*
        cleanedFileNameHashMap<KEY, VALUE>
            KEY   = Integer fileId
            VALUE = String cleanedFileNameForSelected
         */
        // Return the cleaned filename for the given fileId
        return cleanedFileNameHashMap.get(fileId);
    }

    /*
    =========================================
    Return Cleaned Filename for Selected File
    =========================================
     */
    // This method is called from Attachments.xhtml by passing the fileId as a parameter.
    public String cleanedAttachmentForSelected() {

        Integer fileId = selected.getId();
        /*
        cleanedFileNameHashMap<KEY, VALUE>
            KEY   = Integer fileId
            VALUE = String cleanedFileNameForSelected
         */

        // Return the cleaned filename for the selected file
        return cleanedFileNameHashMap.get(fileId);
    }

    /*
    ==========================
    Return Selected File's URI
    ==========================
     */
    public String selectedAttachmentURI() {
        return Constants.FILES_URI + selected.getName();
    }

    /*
    =============================================
    Return True if Selected File is an Image File
    =============================================
    Any type of file can be uploaded or downloaded.
    We identify the types of image files that can be displayed in the web browser.
     */
    public boolean isImage() {

        switch (extensionOfSelectedFileInCaps()) {
            case "JPG":
            case "JPEG":
            case "PNG":
            case "GIF":
                // Selected file is an acceptable image file
                return true;
            default:
                return false;
        }
    }

    /*
    ========================================
    Return True if Selected File is Viewable
    ========================================
    Any type of file can be uploaded or downloaded.
    We identify the types of files that can be displayed in the web browser.
     */
    public boolean isViewable() {

        switch (extensionOfSelectedFileInCaps()) {
            case "HTML":
            case "PDF":
            case "TXT":
                // Selected file is viewable within the web browser
                return true;
            default:
                return false;
        }
    }

    /*
    =====================================
    The HTML5 <video> tag can play the
    following video files: MP4, OGG, WEBM
    =====================================
    Any type of file can be uploaded or downloaded.
    We identify the types of video files that can be played in the web browser.
     */
    public boolean isVideo() {
        String fileExtension = extensionOfSelectedFileInCaps();
        return (fileExtension.equals("MP4") || fileExtension.equals("OGG") || fileExtension.equals("WEBM"));
    }

    /*
    ========================================================
    Return Extension of the Selected File in Capital Letters
    ========================================================
     */
    public String extensionOfSelectedFileInCaps() {

        // Obtain the selected filename
        String attachmentName = selected.getName();

        // Extract the file extension from the filename
        String fileExtension = attachmentName.substring(attachmentName.lastIndexOf(".") + 1);

        // Return file extension in capital letters
        return fileExtension.toUpperCase();
    }
    public boolean getCanUpload(){
        return configurationService.getMaximumUploadCount() > getListOfAttachments().size();
    }

    // Returns the maximum number of files the user can upload
    public int getMaximumUploadCount(){
        return configurationService.getMaximumUploadCount() - getListOfAttachments().size();
    }
    public Long getMaximumUploadSize() {
        return  configurationService.getMaximumUploadSize() * 1024L * 1024;
    }

}
