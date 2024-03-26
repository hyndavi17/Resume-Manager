/*
 * Created by Hyndavi Venkatreddygari, Sai Nikhita Nayani, Yoseph Alebachew on 2023.11.13
 * Copyright © 2023 Hyndavi Venkatreddygari, Sai Nikhita Nayani, Yoseph Alebachew. All rights reserved.
 */

package edu.vt.resumemanager.facade;

import edu.vt.resumemanager.entity.Configuration;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class ConfigurationFacade extends AbstractFacade<Configuration> {
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
    public ConfigurationFacade() {
        super(Configuration.class);
    }

    /*
     ************************
     *   Instance Methods   *
     ************************
     */

    // Returns the object reference of the Account object whose primary key is id
    public Configuration getConfiguration(int id) {
        // The find method is inherited from the parent AbstractFacade class
        return entityManager.find(Configuration.class, id);
    }

    // Returns the object reference of the Account object whose Username is username
    public Configuration findByKey(String  key) {
        if (entityManager.createQuery("SELECT c FROM Configuration c WHERE c.configKey = :key")
                .setParameter("key", key)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (Configuration) (entityManager.createQuery("SELECT c FROM Configuration c WHERE c.configKey = :key")
                    .setParameter("key", key)
                    .getSingleResult());
        }
    }

}
