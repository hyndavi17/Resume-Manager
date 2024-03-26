
package edu.vt.resumemanager.facade;

import edu.vt.resumemanager.dto.ProjectEmployeeDTO;
import edu.vt.resumemanager.entity.Project;
import edu.vt.resumemanager.entity.ProjectEmployee;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class ProjectFacade extends AbstractFacade<Project> {
    /*
    ---------------------------------------------------------------------------------------------
    The EntityManager is an API that enables database CRUD (Create Read Update Delete) operations
    and complex database searches. An EntityManager instance is created to manage entities
    that are defined by a persistence unit. The @PersistenceContext annotation below associates
    the entityManager instance with the persistence unitName identified below.
    ---------------------------------------------------------------------------------------------
     */
    @PersistenceContext(unitName = "ResumeManager")
    private EntityManager entityManager;

    // Obtain the object reference of the EntityManager instance in charge of
    // managing the entities in the persistence context identified above.
    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /*
    This constructor method invokes its parent AbstractFacade's constructor method,
    which in turn initializes its entity class type T and entityClass instance variable.
     */
    public ProjectFacade() {
        super(Project.class);
    }

    /*
     ************************
     *   Instance Methods   *
     ************************
     */

    // Returns the object reference of the Project object whose primary key is id
    public Project getProject(int id) {
        // The find method is inherited from the parent AbstractFacade class
        return entityManager.find(Project.class, id);
    }

    // Returns the object reference of the Project object whose Username is name
    public Project findByName(String  name) {
        if (entityManager.createQuery("SELECT p FROM Project p WHERE p.name = :name")
                .setParameter("name", name)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (Project) (entityManager.createQuery("SELECT p FROM Project p WHERE p.name = :name")
                    .setParameter("name", name)
                    .getSingleResult());
        }
    }

    // Returns the object reference of the Project object whose Username is name
    public List<Project> findByManager(Integer id) {
        return getEntityManager().createQuery(
                        "SELECT p FROM Project p WHERE p.manager = :id")
                .setParameter("id", id)
                .getResultList();
    }

    // Deletes the Project entity object whose primary key is id
    public void deleteProject(int id) {
        
        // The find method is inherited from the parent AbstractFacade class
        Project Project = entityManager.find(Project.class, id);
        
        // The remove method is inherited from the parent AbstractFacade class
        entityManager.remove(Project);
    }

    public List<ProjectEmployee> getTeam(Integer id) {
        return getEntityManager().createQuery(
                        "SELECT pe FROM ProjectEmployee pe WHERE pe.project = :id")
                .setParameter("id", id)
                .getResultList();
    }

}
