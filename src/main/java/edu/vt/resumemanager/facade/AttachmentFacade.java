
package edu.vt.resumemanager.facade;

import edu.vt.resumemanager.entity.Attachment;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class AttachmentFacade extends AbstractFacade<Attachment> {
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
    public AttachmentFacade() {
        super(Attachment.class);
    }

    /*
     ************************
     *   Instance Methods   *
     ************************
     */

    // Returns the object reference of the Attachment object whose primary key is id
    public Attachment getAttachment(int id) {
        // The find method is inherited from the parent AbstractFacade class
        return entityManager.find(Attachment.class, id);
    }

    // Returns the object reference of the Attachment object whose Attachment user is id
    public Attachment findByAccount(Integer id) {
        if (entityManager.createQuery("SELECT e FROM Attachment e WHERE e.user = :id")
                .setParameter("id", id)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (Attachment) (entityManager.createQuery("SELECT e FROM Attachment e WHERE e.id = :id")
                    .setParameter("id", id)
                    .getSingleResult());
        }
    }

    public List<Attachment> findAttachmentsByAccount(Integer id) {
        return entityManager.createQuery("SELECT e FROM Attachment e WHERE e.user = :id")
                .setParameter("id", id)
                .getResultList();

    }

    public List<Attachment> findByFilename(String file_name) {
        /*
        The following @NamedQuery definition is given in UserFile entity class file:
        @NamedQuery(name = "UserFile.findByFilename", query = "SELECT u FROM UserFile u WHERE u.filename = :filename")

        The following statement obtains the results from the named database query.
         */
        return entityManager.createNamedQuery("Attachment.findByFilename")
                .setParameter("filename", file_name)
                .getResultList();
    }

    // Deletes the Attachment entity object whose primary key is id
    public void deleteAttachment(int id) {
        
        // The find method is inherited from the parent AbstractFacade class
        Attachment Attachment = entityManager.find(Attachment.class, id);
        
        // The remove method is inherited from the parent AbstractFacade class
        entityManager.remove(Attachment);
    }

}
