
package edu.vt.resumemanager.facade;

import edu.vt.resumemanager.entity.Project;
import edu.vt.resumemanager.entity.ProjectEmployee;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class ProjectEmployeeFacade extends AbstractFacade<ProjectEmployee> {
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
    public ProjectEmployeeFacade() {
        super(ProjectEmployee.class);
    }

    /*
     ************************
     *   Instance Methods   *
     ************************
     */

    // Returns the object reference of the Project object whose primary key is id
    public ProjectEmployee find(int employeeId, int projectId) {
        if (entityManager.createQuery("SELECT p FROM ProjectEmployee p WHERE p.employee = :employee AND p.project = :project")
                .setParameter("employee", employeeId)
                .setParameter("project", projectId)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (ProjectEmployee) (entityManager.createQuery("SELECT p FROM ProjectEmployee p WHERE p.employee = :employee AND p.project = :project")
                    .setParameter("employee", employeeId)
                    .setParameter("project", projectId)
                    .getSingleResult());
        }
    }

    public List<ProjectEmployee> findByProject(int projectId) {
        return entityManager.createQuery("SELECT p FROM ProjectEmployee p WHERE p.project = :project")
                .setParameter("project", projectId)
                .getResultList();
    }


    // Deletes the Project entity object whose primary key is id
    public void deleteProjectEmployee(int employeeId, int projectId) {
        
        // The find method is inherited from the parent AbstractFacade class
        ProjectEmployee projectEmployee = find(employeeId,projectId);
        
        // The remove method is inherited from the parent AbstractFacade class
        entityManager.remove(projectEmployee);
    }

}
