/*
 * Created by Hyndavi Venkatreddygari, Sai Nikhita Nayani, Yoseph Alebachew on 2023.11.21
 * Copyright © 2023 Hyndavi Venkatreddygari, Sai Nikhita Nayani, Yoseph Alebachew. All rights reserved.
 */

package edu.vt.resumemanager.controllers.manager;

import edu.vt.resumemanager.controllers.AccountController;
import java.util.stream.*;

import edu.vt.resumemanager.dto.Profile;
import edu.vt.resumemanager.entity.Certificate;
import edu.vt.resumemanager.entity.Project;
import edu.vt.resumemanager.entity.Skill;
import edu.vt.resumemanager.facade.CertificateFacade;
import edu.vt.resumemanager.facade.SkillFacade;
import edu.vt.resumemanager.service.ProfileService;
import edu.vt.resumemanager.utils.Constants;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.optionconfig.animation.Animation;
import org.primefaces.model.tagcloud.DefaultTagCloudItem;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudItem;
import org.primefaces.model.tagcloud.TagCloudModel;

import java.io.Serializable;
import java.util.*;

/*
---------------------------------------------------------------------------
The @Named (jakarta.inject.Named) annotation indicates that the objects
instantiated from this class will be managed by the Contexts and Dependency
Injection (CDI) container. The name "ProjectController" is used within
Expression Language (EL) expressions in JSF (XHTML) facelets pages to
access the properties and invoke methods of this class.
---------------------------------------------------------------------------
 */
@Named("employeesController")

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
public class EmployeesController implements Serializable {
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
    private SkillFacade skillFacade;

    @EJB
    private CertificateFacade certificateFacade;

    @Inject
    private ProfileService profileService;

    // selected = object reference of a selected Profile object
    @Setter(AccessLevel.NONE)
    private Profile selected;

    @Getter(AccessLevel.NONE)
    private TagCloudModel skillsModel;

    @Getter(AccessLevel.NONE)
    private TagCloudModel certificatesModel;

    @Getter(AccessLevel.NONE)
    private List<Skill> skills;

    @Getter(AccessLevel.NONE)
    private List<Certificate> certificates;

    @Getter
    private BarChartModel skillBarModel;

    @Getter
    private BarChartModel certificatesBarModel;

    @Inject
    private AccountController accountController;

    @Setter(AccessLevel.NONE)
    private String attachemtURLPrefix = Constants.FILES_URI;

    @Getter(AccessLevel.NONE)
    private List<Profile> listOfProfiles;

    private String skillFilter;
    public TagCloudModel getSkillsModel() {
         if (skillsModel == null) {
            skillsModel = new DefaultTagCloudModel();
            Map<String, Long> scs = getSkills().stream().collect(Collectors.groupingBy(Skill::getName, Collectors.counting()));
            scs.forEach((k,v) -> skillsModel.addTag(new DefaultTagCloudItem(k,v.intValue())));
        }
        return skillsModel;
    }

    public TagCloudModel getCertificatesModel() {
        if (certificatesModel == null) {
            certificatesModel = new DefaultTagCloudModel();
            Map<String, Long> scs = getCertificates().stream().collect(Collectors.groupingBy(Certificate::getName, Collectors.counting()));
            scs.forEach((k,v) -> certificatesModel.addTag(new DefaultTagCloudItem(k,v.intValue())));
        }
        return certificatesModel;
    }

    public List<Profile> getListOfProfiles() {
        if(listOfProfiles == null){
            listOfProfiles = profileService.getAll();
        }
        return listOfProfiles;
    }
    /*
     ****************************************
     *   Unselect Selected Profile Object   *
     ****************************************
     */
    public void unselect() {
        selected = null;
    }

    public String viewProfile(Profile p) {
        this.selected = profileService.getProfile(p.getId());
        System.out.println("View Employee");
        System.out.println(p);
        return "/manager/employees/view?faces-redirect=true";
    }

    public void filterSelect(SelectEvent<TagCloudItem> event) {
        skillFilter = event.getObject().getLabel();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect("list.xhtml");
        }catch (Exception e){

        }
    }
    public List<Skill> getSkills(){
        if(skills==null) {
            skills = skillFacade.findAll();
        }
        return skills;
    }

    public List<Certificate> getCertificates(){
        if(certificates==null) {
            certificates = certificateFacade.findAll();
        }
        return certificates;
    }

    private void createSkillBarModel(){
        List<String> bgColor = new ArrayList<>();
        List<String> borderColor = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Map<String, Long> scs = getSkills().stream().collect(Collectors.groupingBy(Skill::getName, Collectors.counting()));
        skillBarModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Skills");
        bgColor.add("rgba(255, 99, 132, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 205, 86, 0.2)");
        bgColor.add("rgba(75, 192, 192, 0.2)");
        bgColor.add("rgba(54, 162, 235, 0.2)");
        bgColor.add("rgba(153, 102, 255, 0.2)");
        bgColor.add("rgba(201, 203, 207, 0.2)");
        barDataSet.setBackgroundColor(bgColor);

        borderColor.add("rgb(255, 99, 132)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 205, 86)");
        borderColor.add("rgb(75, 192, 192)");
        borderColor.add("rgb(54, 162, 235)");
        borderColor.add("rgb(153, 102, 255)");
        borderColor.add("rgb(201, 203, 207)");
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<Number> values = new ArrayList<>();
        scs.forEach((k,v)->{
            values.add(v);
            labels.add(k);
        });


        barDataSet.setData(values);
        data.setLabels(labels);
        skillBarModel.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        options.setMaintainAspectRatio(false);
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        linearAxes.setBeginAtZero(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("italic");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(24);
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        // disable animation
        Animation animation = new Animation();
        animation.setDuration(0);
        options.setAnimation(animation);

        skillBarModel.setOptions(options);
    }
    private void createCertificateBarModel(){
        List<String> bgColor = new ArrayList<>();
        List<String> borderColor = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Map<String, Long> scs = getCertificates().stream().collect(Collectors.groupingBy(Certificate::getName, Collectors.counting()));
        certificatesBarModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Certificates");
        bgColor.add("rgba(255, 99, 132, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 205, 86, 0.2)");
        bgColor.add("rgba(75, 192, 192, 0.2)");
        bgColor.add("rgba(54, 162, 235, 0.2)");
        bgColor.add("rgba(153, 102, 255, 0.2)");
        bgColor.add("rgba(201, 203, 207, 0.2)");
        barDataSet.setBackgroundColor(bgColor);

        borderColor.add("rgb(255, 99, 132)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 205, 86)");
        borderColor.add("rgb(75, 192, 192)");
        borderColor.add("rgb(54, 162, 235)");
        borderColor.add("rgb(153, 102, 255)");
        borderColor.add("rgb(201, 203, 207)");
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<Number> values = new ArrayList<>();
        scs.forEach((k,v)->{
            values.add(v);
            labels.add(k);
        });


        barDataSet.setData(values);
        data.setLabels(labels);
        certificatesBarModel.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        options.setMaintainAspectRatio(false);
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        linearAxes.setBeginAtZero(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("italic");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(24);
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        // disable animation
        Animation animation = new Animation();
        animation.setDuration(0);
        options.setAnimation(animation);

        certificatesBarModel.setOptions(options);
    }
    @PostConstruct
    public void createBarModel() {
        createSkillBarModel();
        createCertificateBarModel();
    }

    public void setSelected(Profile p) {
        this.selected = profileService.getProfile(p.getId());
    }
}
