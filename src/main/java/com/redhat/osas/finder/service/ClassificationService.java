package com.redhat.osas.finder.service;

import com.redhat.osas.finder.model.Classification;
import com.redhat.osas.finder.model.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ClassificationService {
    @Inject
    EntityManager em;

    public void removeForEntry(Entry oldEntry) {
        Query query = em.createNamedQuery("Classification.deleteForEntry");
        query.setParameter("entry", oldEntry);
        query.executeUpdate();
    }

    public Classification save(Entry entry) {
        Classification classification = Classification.builder()
                .entry(entry)
                .build();
        em.persist(classification);
        return classification;
    }

    public List<Classification> findUnclassifiedEntries() {
        Query query = em.createNamedQuery("Classification.findUnclassifiedEntries");
        //noinspection unchecked
        return (List<Classification>) query.getResultList();
    }

    public void update(Classification classification) {
        em.merge(classification);
    }
}
