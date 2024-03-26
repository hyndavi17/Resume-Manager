/*
 * Created by Osman Balci on 2023.7.25
 * Copyright © 2023 Osman Balci. All rights reserved.
 */
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