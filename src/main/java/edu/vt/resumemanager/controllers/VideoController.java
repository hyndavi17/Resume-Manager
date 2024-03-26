package edu.vt.resumemanager.controllers;
import java.io.Serializable;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import lombok.Getter;

@Getter
@Named(value = "videoController")
@SessionScoped
public class VideoController implements Serializable  {
    /*
   =========================
   Getter and Setter Methods
   =========================
    */
    /*
     ============================
     Instance Variable (Property)
     ============================
      */
    private String youTubeVideoId;

    public void setYouTubeVideoId(String youTubeVideoId) {
        this.youTubeVideoId = youTubeVideoId;
    }
}
