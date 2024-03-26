
package edu.vt.resumemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Photo implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */

    // Filepath for regular photo,   e.g., "/resources/images/photos/photo1.jpg"
    private String regularPhotoFilepath;

    // Filepath for thumbnail photo, e.g., "/resources/images/photos/photo1s.jpg"
    private String thumbnailPhotoFilepath;

    // Description of photo
    private String alt;

    // Title of photo
    private String title;
    /*
       ==================
       Constructor Method
       ==================
        */

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getRegularPhotoFilepath() {
        return regularPhotoFilepath;
    }

    public void setRegularPhotoFilepath(String regularPhotoFilepath) {
        this.regularPhotoFilepath = regularPhotoFilepath;
    }

    public String getThumbnailPhotoFilepath() {
        return thumbnailPhotoFilepath;
    }

    public void setThumbnailPhotoFilepath(String thumbnailPhotoFilepath) {
        this.thumbnailPhotoFilepath = thumbnailPhotoFilepath;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
