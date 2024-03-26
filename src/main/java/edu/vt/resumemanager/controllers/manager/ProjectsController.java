/*
 * Created by Hyndavi Venkatreddygari, Sai Nikhita Nayani, Yoseph Alebachew on 2023.11.21
 * Copyright © 2023 Hyndavi Venkatreddygari, Sai Nikhita Nayani, Yoseph Alebachew. All rights reserved.
 */

package edu.vt.resumemanager.controllers.manager;

import edu.vt.resumemanager.controllers.AccountController;
import edu.vt.resumemanager.dto.Profile;
import edu.vt.resumemanager.entity.Project;
import edu.vt.resumemanager.entity.ProjectEmployee;
import edu.vt.resumemanager.entity.Template;
import edu.vt.resumemanager.facade.*;
import edu.vt.resumemanager.service.*;
import edu.vt.resumemanager.utils.*;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.*;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.*;
import jakarta.faces.context.*;

import lombok.*;
import org.primefaces.PrimeFaces;
import org.primefaces.model.ResponsiveOption;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import java.util.zip.*;

/*
---------------------------------------------------------------------------
The @Named (jakarta.inject.Named) annotation indicates that the objects
instantiated from this class will be managed by the Contexts and Dependency
Injection (CDI) container. The name "ProjectController" is used within
Expression Language (EL) expressions in JSF (XHTML) facelets pages to
access the properties and invoke methods of this class.
---------------------------------------------------------------------------
 */
@Named("projectsController")

/*
The @SessionScoped annotation preserves the values of the ProjectsController
object's instance variables across multiple HTTP request-response cycles
as long as the user's established HTTP session is alive.
 */
@SessionScoped
/*
-----------------------------------------------------------------------------
Marking the ProjectController class as "implements Serializable" implies that
instances of the class can be automatically serialized and deserialized.

Serialization is the process of converting a class instance (object)
from memory into a suitable format for storage in a file or memory buffer,
or for transmission across a network connection link.

Deserialization is the process of recreating a class instance (object)
in memory from the format under which it was stored.
-----------------------------------------------------------------------------
 */
/*
 * The lombok annotations @Data, @AllArgsConstructor, @NoArgsConstructor
 * allow the creation of getter, setters methods and constructors at compile time
 * */
@Data
public class ProjectsController implements Serializable {
     /*
    ===============================
    Instance Variables (Properties)
    ===============================
    */

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    ProjectFacade bean into the instance variable 'projectFacade' after it is instantiated at runtime.
     */
    @EJB
    private ProjectFacade projectFacade;

    @EJB
    private ProjectEmployeeFacade projectEmployeeFacade;

    @EJB
    private TemplateFacade templateFacade;

    @Inject
    private ProfileService profileService;

    @Inject
    private ProjectService projectService;

    @Inject
    private TemplateService templateService;

    @Inject
    private AccountController accountController;

    private List<ResponsiveOption> responsiveOptions;

    // Used to track pdf preparation progress
    private Integer progress;

    // zip file to download when resumes are exported
    private String zipFilePath;

    // showProgress will be set to true while pdf preparation is in progress
    private boolean showProgress = false;

    private boolean showPreview = false;

    private boolean downloadReady = false;
    // List of object references of Project objects
    @Getter(AccessLevel.NONE)
    private List<Project> listOfProjects = null;

    @Setter(AccessLevel.NONE)
    private String[] projectStatus = Constants.PROJECT_STATUS;

    @Setter(AccessLevel.NONE)
    private String[] projectStatusColors = Constants.PROJECT_STATUS_COLORS;

    // List of object references of Template objects
    @Getter(AccessLevel.NONE)
    private List<Template> listOfTemplates = null;

    // selected = object reference of a selected Project object
    private Project selected;

    // Selected export template
    private int selectedTemplate;

    // preview of the export using the selected template
    @Getter(AccessLevel.NONE)
    private String exportPreview;

    // selected = object reference of a selected Project object
    private Profile currentProfile;

    // projectTeam = List of employees references of Project objects found in the search
    @Getter(AccessLevel.NONE)
    private List<Profile> projectTeam = null;

    @Getter(AccessLevel.NONE)
    private List<Profile> listOfProfiles = null;

    @PostConstruct
    public void init() {
        responsiveOptions = new ArrayList<>();
        responsiveOptions.add(new ResponsiveOption("1024px", 3, 3));
        responsiveOptions.add(new ResponsiveOption("768px", 2, 2));
        responsiveOptions.add(new ResponsiveOption("560px", 1, 1));
    }
    public List<Project> getListOfProjects() {
        if (listOfProjects == null) {
            listOfProjects = projectFacade.findByManager(accountController.getSignedInUser().getId());
        }
        return listOfProjects;
    }

    public List<Template> getListOfTemplates(){
        if (listOfTemplates == null) {
            listOfTemplates = templateFacade.findAll();
        }
        return listOfTemplates;
    }
    public List<Profile> getProjectTeam() {
        if(projectTeam == null){
            projectTeam = profileService.getTeam(selected.getId());
        }
        return projectTeam;
    }

    public List<Profile> getListOfProfiles() {
        if(listOfProfiles == null){
            listOfProfiles = profileService.getAll();
        }
        return listOfProfiles;
    }

    /*
     ****************************************
     *   Unselect Selected Project Object   *
     ****************************************
     */
    public void unselect() {
        selected = null;
    }

    /*
     *************************************
     *   Cancel and Display List.xhtml   *
     *************************************
     */
    public String cancel() {
        // Unselect previously selected project object if any
        selected = null;
        return "/projects/list.xhtml?faces-redirect=true";
    }

    /*
     ***************************************
     *   Add Employee to this project      *
     ***************************************
     */
    public String addEmployee() {
        return "/projects/employees/browse?faces-redirect=true";
    }


    public String addProfile(Profile p) {
        ProjectEmployee pe = new ProjectEmployee();
        pe.setProject(selected.getId());
        pe.setEmployee(p.getId());
        pe.setDateAdded(new Timestamp(System.currentTimeMillis()));
        pe.setPosition("TODO:Remove");
        projectEmployeeFacade.create(pe);
        this.projectTeam = null;
        return "/projects/view?faces-redirect=true";

    }
    public String removeProfile(Profile p) {
        projectEmployeeFacade.deleteProjectEmployee(p.getId(), selected.getId());
        this.projectTeam = null;
        return "/projects/view?faces-redirect=true";
    }

    public boolean has(Integer id){
        return (getProjectTeam().stream().anyMatch(p->p.getId().equals(id)));
    }

    /*
     ***************************************
     *   Prepare to Create a New Project   *
     ***************************************
     */
    public void prepareCreate() {
        /*
        Instantiate a new Project object and store its object reference into
        instance variable 'selected'. The Project class is defined in Project.java
         */
        selected = new Project();
    }

    public String view(Integer id) {
        setSelected(id);
        return "/projects/view?faces-redirect=true";
    }

    public void viewProfile(Integer id) {
        this.currentProfile = profileService.getProfile(id);
    }
    /*
     ******************************************
     *     Prepare to Edit a Project     *
     ******************************************
     */
    public void setSelected(Integer id) {
        /*
        Instantiate a new Project object and store its object reference into
        instance variable 'selected'. The Project class is defined in Project.java
         */
        selected = listOfProjects.stream().filter(p->p.getId().equals(id)).findFirst().get();
    }

    /*
     **************************************************
     *   CREATE a New Project Breed in the Database   *
     **************************************************
     */
    public void create() {
        Methods.preserveMessages();
        /*
        An enum is a special Java type used to define a group of constants.
        The constants CREATE, DELETE and UPDATE are defined as follows in JsfUtil.java

                public enum PersistAction {
                    CREATE,
                    DELETE,
                    UPDATE
                }
         */

        /*
         The object reference of the project to be created is stored in the instance variable 'selected'
         See the persist method below.
         */
        selected.setStatus(1);
        selected.setManager(accountController.getSignedInUser().getId());
        persist(JsfUtil.PersistAction.CREATE, "Project was Successfully Created!");
        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The CREATE operation is successfully performed.
            selected = null;            // Remove selection
            listOfProjects = null;     // Invalidate listOfProjectBreeds to trigger re-query.
        }
    }

    /*
     ***********************************************
     *   UPDATE Selected Project in the Database   *
     ***********************************************
     */
    public void update() {
        Methods.preserveMessages();
        /*
         The object reference of the project to be updated is stored in the instance variable 'selected'
         See the persist method below.
         */
        persist(JsfUtil.PersistAction.UPDATE, "Project was Successfully Updated!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The UPDATE operation is successfully performed.
            selected = null;        // Remove selection
            listOfProjects = null;  // Invalidate listOfProjectBreeds to trigger re-query.
        }
    }

    /*
     *************************************************
     *   DELETE Selected Project from the Database   *
     *************************************************
     */
    public void destroy(Integer id) {
        Methods.preserveMessages();
        selected = listOfProjects.stream().filter(p->p.getId().equals(id)).findFirst().get();

        /*
         The object reference of the project to be deleted is stored in the instance variable 'selected'
         See the persist method below.
         */
        persist(JsfUtil.PersistAction.DELETE, "Project was Successfully Deleted!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The DELETE operation is successfully performed.
            selected = null;            // Remove selection
            listOfProjects = null;     // Invalidate list of projects to trigger re-query.
        }
    }

    /*
     **********************************************************************************************
     *   Perform CREATE, UPDATE (EDIT), and DELETE (DESTROY, REMOVE) Operations in the Database   *
     **********************************************************************************************
     */

    /**
     * @param persistAction  refers to CREATE, UPDATE (Edit) or DELETE action
     * @param successMessage displayed to inform the user about the result
     */
    private void persist(JsfUtil.PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != JsfUtil.PersistAction.DELETE) {
                    /*
                     -------------------------------------------------
                     Perform CREATE or EDIT operation in the database.
                     -------------------------------------------------
                     The edit(selected) method performs the SAVE (STORE) operation of the "selected"
                     object in the database regardless of whether the object is a newly
                     created object (CREATE) or an edited (updated) object (EDIT or UPDATE).

                     projectFacade inherits the edit(selected) method from the AbstractFacade class.
                     */
                    projectFacade.edit(selected);
                } else {
                    /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove(selected) method performs the DELETE operation of the "selected"
                     object in the database.

                     projectFacade inherits the remove(selected) method from the AbstractFacade class.
                     */
                    projectFacade.remove(selected);
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
                    JsfUtil.addErrorMessage(ex, "A persistence error occurred!");
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, "A persistence error occurred");
            }
        }
    }

    public void prepareExport(){
        showProgress = false;
        zipFilePath = null;
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
            int i = 100/(getProjectTeam().size());
            for (Profile profile : getProjectTeam()) {
                String file = templateService.toPDF(template, profile, selected.getId());
                if(file != null){
                    files.add(file);
                }
                //increment the progress by a unit but make sure it does not exceed 100%
                setProgress((getProgress()+i)%100);
            }

            //ZIP files
            String zipFileName = selected.getId()+"_"+template.getName().toLowerCase().replace(" ","_")+".zip";
            try(ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(Constants.FILES_ABSOLUTE_PATH+zipFileName))){
                for (String sourceFile: files ) {
                    File fileToZip = new File(sourceFile);
                    FileInputStream fis = new FileInputStream(fileToZip);
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                    zipOut.putNextEntry(zipEntry);
                    byte[] bytes = new byte[1024];
                    int length;
                    while((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    fis.close();
                }
            }catch (Exception ex){
                System.out.println("Error while zipping the resumes:" + ex.getMessage());
            }
            zipFilePath = Constants.FILES_URI + zipFileName;
            setProgress(100);
            downloadReady = true;
        }
    }
    public void exportComplete() {
        downloadReady = true;
//        PrimeFaces.current().ajax().update("ExportResumeForm");
    }
    private Profile getDemoProfile(){
        //TODO: replace with demo content
        return profileService.getProfile(projectTeam.get(0).getId());
    }
    public String getExportPreview(){
        String rv = "";
        Profile demo = getDemoProfile();
        Template template = templateFacade.getTemplate(selectedTemplate);
        if(template!=null){
            rv = templateService.process( template, demo);
        }
        showPreview = true;
        return rv;
    }

    public String shortenDisc(String desc) {
        String rv = "";
        if(desc!=null && desc.length()>500){
            rv = desc.substring(0, 500)+"...";
        }else {
            rv = desc;
        }
        return rv;
    }
}
