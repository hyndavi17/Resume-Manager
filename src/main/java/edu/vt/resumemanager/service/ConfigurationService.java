

package edu.vt.resumemanager.service;

import edu.vt.resumemanager.facade.ConfigurationFacade;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Named("configurationService")
@ApplicationScoped
@Data
public class ConfigurationService implements Serializable {

    @EJB
    private ConfigurationFacade configurationFacade;
    Map<String, String> redirectPaths= new HashMap<>();

    public ConfigurationService(){
        redirectPaths.put("employee", "/profile/profile.xhtml");
        redirectPaths.put("admin", "/admin/users/list.xhtml");
        redirectPaths.put("manager", "/projects/list.xhtml");
    }
    public String getRedirectPath(String role){
        if(redirectPaths.containsKey(role)){
            return redirectPaths.get(role);
        }else {
            return "/profile/profile.xhtml";
        }
    }
    public int getSuspendTime(){
        return Integer.parseInt(configurationFacade.findByKey("suspendtime").getValue());
    }
    public int getSuspendCount(){
        return Integer.parseInt(configurationFacade.findByKey("suspendcount").getValue());
    }
    public int getRecaptchaCount(){
        return Integer.parseInt(configurationFacade.findByKey("recaptchacount").getValue());
    }

    public int getMaximumUploadCount(){
        return Integer.parseInt(configurationFacade.findByKey("maximumuploadcount").getValue());
    }
    public int getMaximumUploadSize(){
        return Integer.parseInt(configurationFacade.findByKey("maximumUploadSize").getValue());
    }

}
