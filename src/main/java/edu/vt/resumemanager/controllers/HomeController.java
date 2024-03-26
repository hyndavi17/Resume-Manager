

package edu.vt.resumemanager.controllers;

import edu.vt.resumemanager.dto.Photo;
import edu.vt.resumemanager.service.PhotoService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import lombok.Data;
import org.primefaces.model.ResponsiveOption;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named(value = "homeController")
@SessionScoped
@Data
public class HomeController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private List<Photo> listOfPhotos;
    private List<ResponsiveOption> responsiveOptions1;
    private List<ResponsiveOption> responsiveOptions2;
    private List<ResponsiveOption> responsiveOptions3;
    private int activeIndex = 0;

    /*
    The @Inject annotation directs the CDI Container Manager to inject (store) the
    object reference of the CDI container-managed PhotoService bean into the
    instance variable 'photoService' after it is instantiated at runtime.
     */
    @Inject
    private PhotoService photoService;

    /*
    The PostConstruct annotation is used on a method that needs to be executed after
    dependency injection is done to perform any initialization. The initialization
    method init() is the first method invoked before this class is put into service.
     */
    @PostConstruct
    public void init() {
        listOfPhotos = photoService.getListOfPhotos();

        responsiveOptions1 = new ArrayList<>();
        responsiveOptions1.add(new ResponsiveOption("1024px", 5));
        responsiveOptions1.add(new ResponsiveOption("768px", 3));
        responsiveOptions1.add(new ResponsiveOption("560px", 1));

        responsiveOptions2 = new ArrayList<>();
        responsiveOptions2.add(new ResponsiveOption("1024px", 5));
        responsiveOptions2.add(new ResponsiveOption("960px", 4));
        responsiveOptions2.add(new ResponsiveOption("768px", 3));
        responsiveOptions2.add(new ResponsiveOption("560px", 1));

        responsiveOptions3 = new ArrayList<>();
        responsiveOptions3.add(new ResponsiveOption("1500px", 5));
        responsiveOptions3.add(new ResponsiveOption("1024px", 3));
        responsiveOptions3.add(new ResponsiveOption("768px", 2));
        responsiveOptions3.add(new ResponsiveOption("560px", 1));
    }

    /*
    ===============
    Instance Method
    ===============
     */
    public void changeActiveIndex() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        this.activeIndex = Integer.parseInt(params.get("index"));
    }

}
