package com.redhat.osas.finder.service;

import com.redhat.osas.finder.model.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class UserService {
    @Inject
    EntityManager em;
    @Inject
    Logger log;

    List<User> findAll() {
        Query query = em.createNamedQuery("User.findAll");
        //noinspection unchecked
        return (List<User>) query.getResultList();
    }
}
