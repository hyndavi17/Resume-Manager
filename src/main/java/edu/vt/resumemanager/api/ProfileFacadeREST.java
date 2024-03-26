package edu.vt.resumemanager.api;

import edu.vt.resumemanager.dto.Profile;
import edu.vt.resumemanager.service.ProfileService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless

// The following default path name is changed to a simpler one from @Path("edu.vt.entity.Building")
@Path("Profiles")

public class ProfileFacadeREST {
    @Inject
    private ProfileService profileService;

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Profile find(@PathParam("id") Integer id) {
        // Super class AbstractFacade provides the find method
        return profileService.getProfile(id);
    }

    @GET
    @Path("getAll")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Profile> findAllBuildings() {
        // Super class AbstractFacade provides the findAll method
        return profileService.getAllWithoutAccount();
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String numberOfBuildings() {
        // Super class AbstractFacade provides the count method
        return String.valueOf(profileService.getAllWithoutAccount().size());
    }

    @GET
    @Path("skill/{skill}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Profile> findProfilesBySkill(@PathParam("skill") String skill) {
        return profileService.getAllWithoutAccount().stream().filter(p-> p.getSkills().stream().anyMatch(s -> s.getName().equalsIgnoreCase(skill))).toList();
    }

    @GET
    @Path("name/{name}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Profile> findProfilesByName(@PathParam("name") String name) {
        return profileService.getAllWithoutAccount().stream().filter(p-> p.getFullName().toLowerCase().contains(name.toLowerCase())).toList();
    }
}
