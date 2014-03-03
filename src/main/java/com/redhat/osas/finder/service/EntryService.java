package com.redhat.osas.finder.service;

import com.redhat.osas.finder.model.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class EntryService {
    @Inject
    EntityManager em;
    @Inject
    Logger log;

    public List<Entry> getEntries() {
        Query query = em.createQuery("select e from Entry e order by e.created");

        //noinspection unchecked
        return (List<Entry>) query.getResultList();
    }

    public List<Entry> getEntries(int page) {
        Query query = em.createQuery("select e from Entry e order by e.created");
        query.setFirstResult(page * 20);
        query.setMaxResults(page * 21);

        //noinspection unchecked
        return (List<Entry>) query.getResultList();
    }
}
