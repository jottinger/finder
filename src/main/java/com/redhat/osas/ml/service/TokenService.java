package com.redhat.osas.ml.service;

import com.redhat.osas.ml.model.Token;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Stateless
public class TokenService {
    @Inject
    EntityManager em;

    public Token findToken(String corpus) {
        try {
            Query query = em.createNamedQuery("Token.findByWord");
            query.setParameter("word", corpus.trim().toLowerCase());
            return (Token) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
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
