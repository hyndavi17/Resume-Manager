
package edu.vt.resumemanager.facade;

import edu.vt.resumemanager.entity.Account;
import edu.vt.resumemanager.entity.Education;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class AccountFacade extends AbstractFacade<Account> {
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
    public AccountFacade() {
        super(Account.class);
    }

    /*
     ************************
     *   Instance Methods   *
     ************************
     */

    // Returns the object reference of the Account object whose primary key is id
    public Account getAccount(int id) {
        // The find method is inherited from the parent AbstractFacade class
        return entityManager.find(Account.class, id);
    }

    // Returns the object reference of the Account object whose Username is username
    public Account findByUsername(String  username) {
        if (entityManager.createQuery("SELECT a FROM Account a WHERE a.username = :username")
                .setParameter("username", username)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (Account) (entityManager.createQuery("SELECT a FROM Account a WHERE a.username = :username")
                    .setParameter("username", username)
                    .getSingleResult());
        }
    }

    // Deletes the Account entity object whose primary key is id
    public void deleteAccount(int id) {
        
        // The find method is inherited from the parent AbstractFacade class
        Account Account = entityManager.find(Account.class, id);
        
        // The remove method is inherited from the parent AbstractFacade class
        entityManager.remove(Account);
    }

    // Returns the object reference of the Education object whose Education user is id
    public List<Account> findByRole(String role) {
        return getEntityManager().createQuery(
                        "SELECT a FROM Account a WHERE a.role = :role")
                .setParameter("role", role)
                .getResultList();
    }
}
