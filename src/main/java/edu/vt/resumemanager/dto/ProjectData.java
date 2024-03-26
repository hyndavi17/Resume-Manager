
package edu.vt.resumemanager.dto;

import edu.vt.resumemanager.entity.Project;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProjectData implements Serializable {
    private Project project;
    private List<Profile> employeeList;
//    private List<ProjectResumeSection> sections;
}
