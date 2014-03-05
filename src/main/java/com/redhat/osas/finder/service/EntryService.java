package com.redhat.osas.finder.service;

import com.redhat.osas.finder.model.Entry;
import com.redhat.osas.ml.model.Token;
import com.redhat.osas.ml.service.CorpusService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class EntryService {
    @Inject
    EntityManager em;
    @Inject
    Logger log;
    @Inject
    CorpusService corpusService;

    public List<Entry> getEntries() {
        Query query = em.createNamedQuery("Entry.allOrdered");

        //noinspection unchecked
        return (List<Entry>) query.getResultList();
    }

    public List<Entry> getEntries(int page) {
        Query query = em.createNamedQuery("Entry.allOrdered");

        query.setFirstResult(page * 20);
        query.setMaxResults(page * 21);

        //noinspection unchecked
        return (List<Entry>) query.getResultList();
    }

    public Entry getEntry(int id) {
        return em.find(Entry.class, id);
    }

    public List<Token> getTokensForEntry(Entry entry) {
        StringBuilder unstemmedText = new StringBuilder(entry.getTitle().toLowerCase().trim());
        for (String tag : entry.getTags()) {
            unstemmedText.append(" ").append(tag);
        }
        List<Token> unstemmedTokens = corpusService.getTokensForCorpus(unstemmedText.toString(), false);
        List<Token> descriptionTokens = corpusService.getTokensForCorpus(entry.getDescription(), true);
        List<Token> stemmedTokens = corpusService.getTokensForCorpus(entry.getContent(), true);
        List<Token> corpus = new ArrayList<>(unstemmedTokens);
        corpus.addAll(descriptionTokens);
        corpus.addAll(stemmedTokens);
        return corpus;
    }

    public List<Token> getTokensForEntry(int id) {
        List<Token> tokens = new ArrayList<>();
        Entry entry = getEntry(id);
        if (entry != null) {
            tokens.addAll(getTokensForEntry(entry));
        }
        return tokens;
    }
}
