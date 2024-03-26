
package edu.vt.resumemanager.service;

import edu.vt.resumemanager.controllers.AttachmentController;
import edu.vt.resumemanager.dto.Profile;
import edu.vt.resumemanager.dto.ProjectEmployeeDTO;
import edu.vt.resumemanager.entity.*;
import edu.vt.resumemanager.facade.*;
import edu.vt.resumemanager.utils.Constants;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Named("profileService")

@SessionScoped
@Data
public class ProfileService implements Serializable {

    @EJB
    private AccountFacade accountFacade;
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

    @EJB
    private ProjectEmployeeFacade projectEmployeeFacade;

    public Profile getProfile(Integer id) {
        Profile rv = new Profile();
        Account account = accountFacade.getAccount(id);
        rv.setAccount(account);
        BasicInfo bf = basicInfoFacade.getBasicInfo(id);
        if (bf == null) {
            bf = new BasicInfo();
            rv.setFirstTime(true);
        } else {
            rv.setFirstTime(false);
        }
        bf.setId(id);
        rv.setBasicInfo(bf);

        Address address = addressFacade.getAddress(id);
        if (address == null) {
            address = new Address();
            address.setId(id);

        }
        rv.setAddress(address);

        List<Education> educations = educationFacade.findByAccount(id).stream()
                .sorted((e1, e2) -> e2.getGraduationDate().compareTo(e1.getGraduationDate()))
                .collect(Collectors.toList());
        rv.setEducations(educations);

        List<Experience> experiences = experienceFacade.findExperiencesByAccount(id).stream()
                .sorted((e1, e2) -> e2.getEndDate().compareTo(e1.getEndDate())).collect(Collectors.toList());
        rv.setExperiences(experiences);

        List<Certificate> certificates = certificateFacade.findCertificatesByAccount(id).stream()
                .sorted((c1, c2) -> c2.getDateObtained().compareTo(c1.getDateObtained())).collect(Collectors.toList());
        rv.setCertificates(certificates);

        List<Skill> skills = skillFacade.findByUser(id);
        if (skills == null) {
            skills = new ArrayList<>();
        }

        List<Attachment> attachments = attachmentFacade.findAttachmentsByAccount(id).stream()
                .sorted((c1, c2) -> c2.getName().compareTo(c1.getName())).collect(Collectors.toList());
        rv.setAttachments(attachments);

        rv.setSkills(skills);
        return rv;
    }

    public void saveBasicInfo(BasicInfo basicInfo) {
        this.basicInfoFacade.edit(basicInfo);
    }

    public void saveAddress(Address address) {
        this.addressFacade.edit(address);
    }

    public void saveEducation(Education education) {
        this.educationFacade.edit(education);
    }

    public void removeEducation(Integer id) {
        this.educationFacade.deleteEducation(id);
    }

    public List<Profile> getAll() {
        List<Profile> rv = new ArrayList<>();
        List<Account> accounts = accountFacade.findByRole("employee");
        for (Account a : accounts) {
            BasicInfo bf = basicInfoFacade.getBasicInfo(a.getId());
            if (bf == null) {
                continue;
            }
            bf.setId(a.getId());
            Profile p = new Profile();
            p.setAccount(a);
            p.setBasicInfo(bf);
            Address address = addressFacade.getAddress(a.getId());
            if (address == null) {
                address = new Address();
                address.setId(a.getId());
            }
            p.setAddress(address);

            List<Education> educations = educationFacade.findByAccount(a.getId()).stream()
                    .sorted((e1, e2) -> e2.getGraduationDate().compareTo(e1.getGraduationDate()))
                    .collect(Collectors.toList());
            p.setEducations(educations);
            List<Experience> experiences = experienceFacade.findExperiencesByAccount(a.getId()).stream()
                    .sorted((e1, e2) -> e2.getEndDate().compareTo(e1.getEndDate())).collect(Collectors.toList());
            p.setExperiences(experiences);

            List<Certificate> certificates = certificateFacade.findCertificatesByAccount(a.getId()).stream()
                    .sorted((c1, c2) -> c2.getDateObtained().compareTo(c1.getDateObtained())).collect(Collectors.toList());
            p.setCertificates(certificates);

            List<Skill> skills = skillFacade.findByUser(a.getId());
            if (skills == null) {
                skills = new ArrayList<>();
            }
            p.setSkills(skills);

            List<Attachment> attachments = attachmentFacade.findAttachmentsByAccount(a.getId()).stream()
                    .sorted((c1, c2) -> c2.getName().compareTo(c1.getName())).collect(Collectors.toList());
            p.setAttachments(attachments);



            rv.add(p);
        }
        return rv;
    }
    public List<Profile> getAllWithoutAccount() {
        List<Profile> rv = new ArrayList<>();
        List<Account> accounts = accountFacade.findByRole("employee");
        for (Account a : accounts) {
            BasicInfo bf = basicInfoFacade.getBasicInfo(a.getId());
            if (bf == null) {
                continue;
            }
            Account acc = new Account();
            acc.setProfilePicture(Constants.FILES_URI+a.getProfilePicture());
            acc.setId(a.getId());
            Profile p = new Profile();
            p.setAccount(acc);
            p.setBasicInfo(bf);
            Address address = addressFacade.getAddress(a.getId());
            if (address == null) {
                address = new Address();
                address.setId(a.getId());
            }
            p.setAddress(address);

            List<Education> educations = educationFacade.findByAccount(a.getId()).stream()
                    .sorted((e1, e2) -> e2.getGraduationDate().compareTo(e1.getGraduationDate()))
                    .collect(Collectors.toList());
            p.setEducations(educations);
            List<Experience> experiences = experienceFacade.findExperiencesByAccount(a.getId()).stream()
                    .sorted((e1, e2) -> e2.getEndDate().compareTo(e1.getEndDate())).collect(Collectors.toList());
            p.setExperiences(experiences);

            List<Certificate> certificates = certificateFacade.findCertificatesByAccount(a.getId()).stream()
                    .sorted((c1, c2) -> c2.getDateObtained().compareTo(c1.getDateObtained())).collect(Collectors.toList());
            p.setCertificates(certificates);

            List<Skill> skills = skillFacade.findByUser(a.getId());
            if (skills == null) {
                skills = new ArrayList<>();
            }
            p.setSkills(skills);

            List<Attachment> attachments = attachmentFacade.findAttachmentsByAccount(a.getId()).stream()
                    .sorted((c1, c2) -> c2.getName().compareTo(c1.getName())).collect(Collectors.toList());
            p.setAttachments(attachments);



            rv.add(p);
        }
        return rv;
    }

    public List<Profile> getTeam(int projectId) {
        List<Profile> rv = new ArrayList<>();
        List<ProjectEmployee> team = projectEmployeeFacade.findByProject(projectId);
        for (ProjectEmployee pe : team) {
            Profile p = new Profile();
            int id = pe.getEmployee();
            Account account = accountFacade.getAccount(id);
            if (account == null) {
                continue;
            }

            p.setAccount(account);
            BasicInfo bf = basicInfoFacade.getBasicInfo(id);
            if (bf == null) {
                continue;
            }
            bf.setId(pe.getEmployee());
            p.setBasicInfo(bf);

            Address address = addressFacade.getAddress(id);
            if (address == null) {
                address = new Address();
                address.setId(id);
            }
            p.setAddress(address);

            List<Education> educations = educationFacade.findByAccount(id).stream()
                    .sorted((e1, e2) -> e2.getGraduationDate().compareTo(e1.getGraduationDate()))
                    .collect(Collectors.toList());
            p.setEducations(educations);

            List<Skill> skills = skillFacade.findByUser(id);
            if (skills == null) {
                skills = new ArrayList<>();
            }
            p.setSkills(skills);

            List<Attachment> attachments = attachmentFacade.findAttachmentsByAccount(id).stream()
                    .sorted((c1, c2) -> c2.getName().compareTo(c1.getName())).collect(Collectors.toList());
            p.setAttachments(attachments);

            rv.add(p);
            // TODO:Add other sections
        }
        return rv;
    }

    public void saveExperience(Experience experience) {
        this.experienceFacade.edit(experience);
    }

    public void removeExperience(Integer id) {
        this.experienceFacade.deleteExperience(id);
    }

    public void saveCertificate(Certificate certificate) {
        this.certificateFacade.edit(certificate);
    }

    public void removeCertificate(Integer id) {
        this.certificateFacade.deleteCertificate(id);
    }

}
