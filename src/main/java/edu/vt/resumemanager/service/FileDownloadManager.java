
package edu.vt.resumemanager.service;

import edu.vt.resumemanager.controllers.AttachmentController;
import edu.vt.resumemanager.entity.Attachment;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;

@Named(value = "fileDownloadManager")
@RequestScoped
public class FileDownloadManager implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */

    /*
    The @Inject annotation directs the CDI Container Manager to inject (store) the object reference of the
    CDI container-managed AttachmentController bean into the instance variable 'userFileController' after it is instantiated at runtime.
     */
    @Inject
    private AttachmentController attachmentController;

    /*
    DefaultStreamedContent and StreamedContent classes are
    imported from the org.primefaces.model packages above.
     */
    private StreamedContent file;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public StreamedContent getFile() throws FileNotFoundException {

        Attachment fileToDownload = attachmentController.getSelected();

        // getFilename() and getFilePath() are given in Attachment.java
        String nameOfFileToDownload = fileToDownload.getName();
        String absolutePathOfFileToDownload = fileToDownload.getFilePath();

        // Returns the MIME type of the specified file or null if the MIME type is not known
        String contentMimeType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(absolutePathOfFileToDownload);

        // Obtain the filename without the prefix "userId_" inserted to associate the file to a user
        String downloadedFileName = nameOfFileToDownload.substring(nameOfFileToDownload.indexOf("_") + 1);

        // FileInputStream must be used here since the files are stored in a directory external to our application
        FileInputStream streamOfFileToDownload = new FileInputStream(absolutePathOfFileToDownload);

        file = DefaultStreamedContent.builder().contentType(contentMimeType).name(downloadedFileName).stream(() -> streamOfFileToDownload).build();

        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }
}
