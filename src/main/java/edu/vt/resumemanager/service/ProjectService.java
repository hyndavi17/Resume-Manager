
package edu.vt.resumemanager.service;

import edu.vt.resumemanager.dto.Profile;
import edu.vt.resumemanager.dto.ProjectEmployeeDTO;
import edu.vt.resumemanager.entity.*;
import edu.vt.resumemanager.facade.*;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named("projectService")

@SessionScoped
@Data
public class ProjectService implements Serializable {

    @EJB
    private AccountFacade accountFacade;

    @EJB
    private ProjectFacade projectFacade;
    @EJB
    private BasicInfoFacade basicInfoFacade;
    @EJB
    private AddressFacade addressFacade;
    @EJB
    private EducationFacade educationFacade;
    @EJB
    private ExperienceFacade experienceFacade;
    @EJB
    private SkillFacade skillFacade;
    @EJB
    private CertificateFacade certificateFacade;
    @EJB
    private AttachmentFacade attachmentFacade;


    public void saveBasicInfo(BasicInfo basicInfo){
        this.basicInfoFacade.edit(basicInfo);
    }

    public void saveAddress(Address address){
        this.addressFacade.edit(address);
    }
    public void saveEducation(Education education){
        this.educationFacade.edit(education);
    }
    public void removeEducation(Integer id){
        this.educationFacade.deleteEducation(id);
    }




}
