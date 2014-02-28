package com.redhat.osas.ml.service;

import com.redhat.osas.ml.model.Token;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Stateless
public class TokenService {
    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    EntityManager em;

    public Token findToken(String corpus, boolean force) {
        Token token = findToken(corpus);
        if (force && token == null) {
            token = saveToken(corpus);
        }
        return token;
    }

    public Token findToken(String corpus) {
        String normalizedCorpus = corpus.trim().toLowerCase();

        Query query = em.createNamedQuery("Token.findByHashCode");
        try {
            query.setParameter("hashCode", normalizedCorpus.hashCode());
            Token token = (Token) query.getSingleResult();
            if (!token.getWord().equals(normalizedCorpus)) {
                // we do this mostly because we want to make sure we don't have any hash code
                // collisions. How likely is this? Um... not very.
                throw new NoResultException("no match");
            }
            return token;
        } catch (NoResultException nre) {
            try {
                query = em.createNamedQuery("Token.findByWord");
                query.setParameter("word", normalizedCorpus);
                return (Token) query.getSingleResult();
            } catch (NoResultException nre2) {
                return null;
            }
        }
    }

    public Token findToken(Integer k) {
        return em.find(Token.class, k);
    }

    public Token saveToken(String word) {
        Token hidden = new Token();
        hidden.setWord(word.trim().toLowerCase());
        em.persist(hidden);
        return hidden;
    }
}
