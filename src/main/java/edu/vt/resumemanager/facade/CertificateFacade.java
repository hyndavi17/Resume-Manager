
package edu.vt.resumemanager.facade;

import edu.vt.resumemanager.entity.Certificate;
import edu.vt.resumemanager.entity.Experience;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class CertificateFacade extends AbstractFacade<Certificate> {
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
    public CertificateFacade() {
        super(Certificate.class);
    }

    /*
     ************************
     *   Instance Methods   *
     ************************
     */

    // Returns the object reference of the Certificate object whose primary key is id
    public Certificate getCertificate(int id) {
        // The find method is inherited from the parent AbstractFacade class
        return entityManager.find(Certificate.class, id);
    }

    // Returns the object reference of the Certificate object whose Certificate user is id
    public Certificate findByAccount(Integer id) {
        if (entityManager.createQuery("SELECT e FROM Certificate e WHERE e.user = :id")
                .setParameter("id", id)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (Certificate) (entityManager.createQuery("SELECT e FROM Certificate e WHERE e.id = :id")
                    .setParameter("id", id)
                    .getSingleResult());
        }
    }

    public List<Certificate> findCertificatesByAccount(Integer id) {
        return getEntityManager().createQuery(
                        "SELECT c FROM Certificate c WHERE c.user = :id")
                .setParameter("id", id)
                .getResultList();
    }

    // Deletes the Certificate entity object whose primary key is id
    public void deleteCertificate(int id) {
        
        // The find method is inherited from the parent AbstractFacade class
        Certificate Certificate = entityManager.find(Certificate.class, id);
        
        // The remove method is inherited from the parent AbstractFacade class
        entityManager.remove(Certificate);
    }

}
