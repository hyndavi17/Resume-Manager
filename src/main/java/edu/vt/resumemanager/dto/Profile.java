
package edu.vt.resumemanager.dto;

import edu.vt.resumemanager.entity.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Profile implements Serializable {
    private Account account;
    private BasicInfo basicInfo;
    private Address address;
    private List<Education> educations;
    private List<Experience> experiences;
    private List<Skill> skills;
    private List<Certificate> certificates;
    private List<Attachment> attachments;

    private boolean isFirstTime = true;

    public String getProfilePicture(){
        return account.getProfilePicture();
    }
    public Integer getId(){
        return this.getAccount().getId();
    }
    public String getFullName(){
        return this.getBasicInfo().getFirstName();
    }
    public String getSkillNames(){
        return String.join(";" , skills.stream().map(Skill::getName).toList());
    }
    public String getSummary(){
        return basicInfo.getSummary();
    }

    public void setAttachments(List<Attachment> att){
        this.attachments = att;
    }
}
