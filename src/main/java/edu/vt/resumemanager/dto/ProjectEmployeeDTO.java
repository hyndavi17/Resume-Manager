
package edu.vt.resumemanager.dto;

import edu.vt.resumemanager.entity.*;
import lombok.Data;

import java.util.List;

@Data
public class ProjectEmployeeDTO {
    private ProjectEmployee projectEmployee;
    private Account account;
    private Integer id;
    private BasicInfo basicInfo;
    private Address address;
    private List<Education> educations;
    private List<Experience> experiences;
    private List<Skill> skills;
    private List<Certificate> certificates;
    private List<Attachment> attachments;

    public String getFullName(){
        return basicInfo.getFullName();
    }
    public String getSummary(){
        return  basicInfo.getSummary();
    }

}
