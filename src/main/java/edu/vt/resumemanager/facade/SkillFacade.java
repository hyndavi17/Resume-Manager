
package edu.vt.resumemanager.facade;

import edu.vt.resumemanager.entity.Skill;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class SkillFacade extends AbstractFacade<Skill> {
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
    public SkillFacade() {
        super(Skill.class);
    }

    /*
     ************************
     *   Instance Methods   *
     ************************
     */

    // Returns the object reference of the Skill object whose primary key is id
    public Skill getSkill(int id) {
        // The find method is inherited from the parent AbstractFacade class
        return entityManager.find(Skill.class, id);
    }

    // Returns the object reference of the Skill object whose Skill name is name
    public Skill findBySkillName(String name) {
        if (entityManager.createQuery("SELECT s FROM Skill s WHERE s.name = :name")
                .setParameter("name", name)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (Skill) (entityManager.createQuery("SELECT s FROM Skill s WHERE s.name = :name")
                    .setParameter("name", name)
                    .getSingleResult());
        }
    }

    // Returns the list of objects whose user id is user
    public List<Skill> findByUser(Integer user) {
        return getEntityManager().createQuery(
                        "SELECT s FROM Skill s WHERE s.user = :user")
                .setParameter("user", user)
                .getResultList();
    }

    public List<String> findSkillNames() {
        return getEntityManager().createQuery(
                        "SELECT DISTINCT name FROM Skill ").getResultList();
    }

    // Deletes the Skill entity object whose primary key is id
    public void deleteSkill(int id) {
        
        // The find method is inherited from the parent AbstractFacade class
        Skill Skill = entityManager.find(Skill.class, id);
        
        // The remove method is inherited from the parent AbstractFacade class
        entityManager.remove(Skill);
    }


}
