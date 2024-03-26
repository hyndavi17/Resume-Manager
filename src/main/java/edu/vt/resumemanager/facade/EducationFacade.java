
package edu.vt.resumemanager.facade;

import edu.vt.resumemanager.entity.Education;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class EducationFacade extends AbstractFacade<Education> {
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
    public EducationFacade() {
        super(Education.class);
    }

    /*
     ************************
     *   Instance Methods   *
     ************************
     */

    // Returns the object reference of the Education object whose primary key is id
    public Education getEducation(int id) {
        // The find method is inherited from the parent AbstractFacade class
        return entityManager.find(Education.class, id);
    }

    // Returns the object reference of the Education object whose Education user is id
    public List<Education> findByAccount(Integer id) {
        return getEntityManager().createQuery(
                        "SELECT e FROM Education e WHERE e.user = :id")
                .setParameter("id", id)
                .getResultList();
    }

    // Deletes the Education entity object whose primary key is id
    public void deleteEducation(int id) {
        
        // The find method is inherited from the parent AbstractFacade class
        Education Education = entityManager.find(Education.class, id);
        
        // The remove method is inherited from the parent AbstractFacade class
        entityManager.remove(Education);
    }

}
