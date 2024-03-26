
package edu.vt.resumemanager.controllers;

import edu.vt.resumemanager.dto.*;
import edu.vt.resumemanager.entity.*;
import edu.vt.resumemanager.facade.EducationFacade;
import edu.vt.resumemanager.facade.SkillFacade;
import edu.vt.resumemanager.facade.TemplateFacade;
import edu.vt.resumemanager.service.*;
import edu.vt.resumemanager.utils.*;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Named("profileController")
@SessionScoped
@Data
public class ProfileController implements Serializable {

    @Getter(AccessLevel.NONE)
    private Profile currentProfile;
    private BasicInfo currentBasicInfo;
    private Address currentAddress;
    private Education currentEducation;
    private Experience currentExperience;
    private Skill currentSkill;
    private Certificate currentCertificate;
    private Attachment currentAttachment;

    @Inject
    private AccountController accountController;

    @Inject
    private AttachmentController attachmentController;

    @Inject
    private ProfileService profileService;

    @EJB
    private SkillFacade skillFacade;

    @EJB
    private EducationFacade educationFacade;

    @Getter(AccessLevel.NONE)
    private List<Skill> skillNames;

    private boolean editPicture = false;
    private boolean editBasicInfo = false;
    private boolean editAddress = false;
    private boolean editEducation = false;
    private boolean editExperience = false;
    private boolean editSkill = false;
    private boolean editCertificate = false;
    private boolean editAttachment = false;

    @Setter(AccessLevel.NONE)
    private String photoURIPrefix = Constants.FILES_URI;

    @Setter(AccessLevel.NONE)
    private String[] stateNames = Constants.US_STATE_NAMES;

    @Setter(AccessLevel.NONE)
    private String[] worldCountries = Constants.WORLD_COUNTRIES;

    @Setter(AccessLevel.NONE)
    private String attachemtURLPrefix = Constants.FILES_URI;


    // Selected export template
    private int selectedTemplate;

    // List of object references of Template objects
    @Getter(AccessLevel.NONE)
    private List<Template> listOfTemplates = null;

    // showProgress will be set to true while pdf preparation is in progress
    private boolean showProgress = false;

    @Getter(AccessLevel.NONE)
    private String exportPreview;

    // Used to track pdf preparation progress
    private Integer progress;

    private boolean showPreview = false;

    private boolean downloadReady = false;

    private String filePath;

    private String skillName;

    @EJB
    private TemplateFacade templateFacade;

    @Inject
    private TemplateService templateService;
    public void enableBasic() {
        this.currentBasicInfo = currentProfile.getBasicInfo();
        this.setEditBasicInfo(true);
    }

    public void disableBasic() {
        this.setEditBasicInfo(false);
    }


    public void saveBasicInfo() {
        Methods.preserveMessages();
        currentBasicInfo.getFullName();
        profileService.saveBasicInfo(currentBasicInfo);
        // Reload profile
        this.currentProfile = profileService.getProfile(currentProfile.getAccount().getId());
        Methods.showMessage("Information", "Success!",
                "Basic Info Updated Successfully!");
        this.disableBasic();
    }

    public void enablePicture() {
        this.setEditPicture(true);
    }

    public void disablePicture() {
        this.setEditPicture(false);
    }

    public void enableAddress() {
        this.currentAddress = currentProfile.getAddress();
        this.setEditAddress(true);
    }

    public void disableAddress() {
        this.setEditAddress(false);
    }

    public void saveAddress() {
        Methods.preserveMessages();
        profileService.saveAddress(currentAddress);
        // Reload profile
        this.currentProfile = profileService.getProfile(currentProfile.getAccount().getId());
        Methods.showMessage("Information", "Success!",
                "Address Updated Successfully!");
        this.disableAddress();
    }

    public void enableEducation() {
        this.setEditEducation(true);
    }

    public void disableEducation() {
        this.setEditEducation(false);
    }

    public void prepareCreateEducation() {
        Education education = new Education();
        education.setUser(currentProfile.getAccount().getId());
        setCurrentEducation(education);

    }

    public void saveEducation() {
        Methods.preserveMessages();
        profileService.saveEducation(currentEducation);
        // Reload profile
        this.currentProfile = profileService.getProfile(currentProfile.getAccount().getId());
        prepareCreateEducation();
        Methods.showMessage("Information", "Success!",
                "Education Saved Successfully!");
    }

    public void setEditableEducation(int id) {
        setCurrentEducation(currentProfile.getEducations().stream().filter(e->e.getId()==id).findFirst().get());
    }

    public void removeEducation(int id) {
        Methods.preserveMessages();

        profileService.removeEducation(id);
        this.currentProfile = profileService.getProfile(currentProfile.getAccount().getId());
        Methods.showMessage("Information", "Success!",
                "Education Removed Successfully!");
    }

    public void enableExperience() {
        this.setEditExperience(true);
    }

    public void disableExperience() {
        this.setEditExperience(false);
    }

    public void prepareCreateExperience() {
        Experience experience = new Experience();
        experience.setUser(currentProfile.getAccount().getId());
        setCurrentExperience(experience);
    }

    public void saveExperience() {
        Methods.preserveMessages();
        profileService.saveExperience(currentExperience);
        // Reload profile
        this.currentProfile = profileService.getProfile(currentProfile.getAccount().getId());
        prepareCreateExperience();
        Methods.showMessage("Information", "Success!",
                "Experience Saved Successfully!");
    }

    public void removeExperience(int index) {
        Methods.preserveMessages();

        profileService.removeExperience(index);
        this.currentProfile = profileService.getProfile(currentProfile.getAccount().getId());
        Methods.showMessage("Information", "Success!",
                "Experience Removed Successfully!");
    }

    public void setEditableExperience(int id) {
        Optional<Experience> experience = currentProfile.getExperiences().stream().filter(e -> e.getId() == id)
                .findFirst();
        if (experience.isPresent()) {
            setCurrentExperience(experience.get());
        } else {
            setCurrentExperience(new Experience());
            getCurrentExperience().setUser(currentProfile.getAccount().getId());
        }

    }

    public void enableSkill() {
        this.skillNames = null;
        this.setEditSkill(true);
    }

    public void disableSkill() {
        currentProfile = profileService.getProfile(currentProfile.getAccount().getId());
        this.setEditSkill(false);
    }

    public void removeSkill(int index) {
         skillFacade.edit(currentSkill);
         Methods.showMessage("Information", "Success!",
         "Skill Removed Successfully!");
    }

    public List<Skill> getSkillNames() {
        if (skillNames == null) {
            this.skillNames = skillFacade.findAll();
        }
        return this.skillNames;
    }

    public List<Skill> completeSkill(String query) {
        String queryLowerCase = query.toLowerCase();
        return getSkillNames().stream().filter(Methods.distinctByKey(Skill::getName))
                .filter(s -> s.getName().toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
    }

    public void onSkillSelect(SelectEvent<String> event) {
        this.currentSkill = new Skill(currentProfile.getAccount().getId(), event.getObject());
        this.skillFacade.create(currentSkill);
        this.currentProfile = this.profileService.getProfile(currentProfile.getAccount().getId());
        Methods.showMessage("Information", "Success!",
                "Skill Added Successfully!");

    }

    public void onSkillUnSelect(SelectEvent<String> event) {
        this.currentSkill = new Skill(currentProfile.getAccount().getId(), event.getObject());
        this.skillFacade.remove(currentSkill);
        this.currentProfile = this.profileService.getProfile(currentProfile.getAccount().getId());
        Methods.showMessage("Information", "Success!",
                "Skill Removed Successfully!");

    }

    public void enableCertificate() {
        this.setEditCertificate(true);
    }

    public void disableCertificate() {
        this.setEditCertificate(false);
    }

    public void prepareCreateCertificate() {
        Certificate certificate = new Certificate();
        certificate.setUser(currentProfile.getAccount().getId());
        setCurrentCertificate(certificate);
    }

    public void saveCertificate() {
        Methods.preserveMessages();
        profileService.saveCertificate(currentCertificate);
        // Reload profile
        this.currentProfile = profileService.getProfile(currentProfile.getAccount().getId());
        prepareCreateCertificate();
        Methods.showMessage("Information", "Success!",
                "Certificate Saved Successfully!");
    }

    public void removeCertificate(int index) {
        Methods.preserveMessages();

        profileService.removeCertificate(index);
        this.currentProfile = profileService.getProfile(currentProfile.getAccount().getId());
        Methods.showMessage("Information", "Success!",
                "Certificate Removed Successfully!");
    }

    public void setEditableCertificate(int id) {
        Optional<Certificate> certificates = currentProfile.getCertificates().stream().filter(e -> e.getId() == id)
                .findFirst();
        if (certificates.isPresent()) {
            setCurrentCertificate(certificates.get());
        } else {
            setCurrentCertificate(new Certificate());
            getCurrentCertificate().setUser(currentProfile.getAccount().getId());
        }

    }

    public void enableAttachment() {
        this.setEditAttachment(true);
    }

    public void disableAttachment() {
        this.setEditAttachment(false);
    }

    public void refreshAttachment() {
        this.currentProfile.setAttachments(attachmentController.getListOfAttachments());
    }

    public Profile getCurrentProfile() {
        if (currentProfile == null) {
            // Get current logged-in user from the session storage
            Integer userId = accountController.getSignedInUser().getId();
            // Get Profile for the logged-in user
            currentProfile = profileService.getProfile(userId);
        }
        if (currentProfile.isFirstTime()) {
            enableBasic();
        }
        return currentProfile;
    }

    public void reload() {
        this.currentProfile = null;
        this.editPicture = false;
    }

    public void upload(){
        accountController.upload();
        editPicture = false;
        currentProfile = profileService.getProfile(accountController.getSignedInUser().getId());
    }

    public Date getToday() {
        return new Date();
    }

    public List<Template> getListOfTemplates(){
        if (listOfTemplates == null) {
            listOfTemplates = templateFacade.findAll();
        }
        return listOfTemplates;
    }
    public void prepareExport(){
        showProgress = false;
        filePath = null;
    }

    public void exportComplete() {
        downloadReady = true;
    }

    public void export() throws InterruptedException {
        downloadReady = false;
        showProgress = true;
        showPreview = false;
        List<String> files = new ArrayList<>();
        Template template = templateFacade.getTemplate(selectedTemplate);
        setProgress(0);
        if (template != null) {
            //Unit of increment for the progress bar;
            String fileName = templateService.toPDF(template, currentProfile);

            filePath = Constants.FILES_URI + fileName;
            setProgress(100);
            downloadReady = true;
        }
    }
    public String getExportPreview(){
        String rv = "";
        Template template = templateFacade.getTemplate(selectedTemplate);
        if(template!=null){
            rv = templateService.process( template, currentProfile);
        }
        showPreview = true;
        return rv;
    }

    public void saveSkill(){
        this.currentSkill = new Skill(currentProfile.getAccount().getId(), skillName);
        skillName = "";
        this.skillFacade.create(currentSkill);
        this.currentProfile = this.profileService.getProfile(currentProfile.getAccount().getId());
        Methods.showMessage("Information", "Success!",
                "Skill Added Successfully!");
    }
}
