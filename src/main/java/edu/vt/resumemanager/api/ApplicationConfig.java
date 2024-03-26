
package edu.vt.resumemanager.api;

import jakarta.ws.rs.core.Application;
import java.util.Set;

@jakarta.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(ProfileFacadeREST.class);
    }

}