
package edu.vt.resumemanager.facade;

import java.util.List;
import jakarta.persistence.EntityManager;

// AbstractFacade is an abstract facade class providing a generic interface to the Entity Manager.
// Parameter T refers to the Class Type.
public abstract class AbstractFacade<T> {

    // An instance variable pointing to an entity class of type T
    private Class<T> entityClass;

    /*
    The following concrete facade classes inherit from this AbstractFacade class:

        - UserFacade        for User entity class        representing database table User
        - UserFileFacade    for UserFile entity class    representing database table UserFile
        - UserPhotoFacade   for UserPhoto entity class   representing database table UserPhoto

     Each concrete facade class calls the following constructor method by passing entity
     class type T as a parameter. A concrete class provides the actual implementation.
     */
    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /*
    Declare the getEntityManager() abstract method as protected.
    The 'protected' keyword is an access modifier making the method
    accessible only in the same package and subclasses.
     */
    protected abstract EntityManager getEntityManager();

    // Stores the newly Created entity object into the database.
    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    // Stores the Edited (updated) entity object into the database.
    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    // Deletes (Removes) entity object from the database.
    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    // Finds an entity object in the database by using its Primary Key (id) and returns it.
    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    // Returns a list of object references of all entity objects found in the database.
    public List<T> findAll() {
        jakarta.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    // Returns a list of object references of all entity objects found in a range in the database.
    public List<T> findRange(int[] range) {
        jakarta.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        jakarta.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    // Obtains and returns the total number of entity objects in the database.
    public int count() {
        jakarta.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        jakarta.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        jakarta.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
