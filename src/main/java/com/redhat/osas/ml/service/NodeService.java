package com.redhat.osas.ml.service;

import com.redhat.osas.ml.model.Layer;
import com.redhat.osas.ml.model.Node;
import com.redhat.osas.ml.model.Token;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class NodeService {
    @Inject
    EntityManager em;
    @Inject
    CorpusService corpusService;

    public double getStrength(Token from, Token to, Layer layer) {
        Query query = em.createNamedQuery("Node.getStrength");
        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("layer", layer);
        double strength = layer.getStrength();
        try {
            Node result = (Node) query.getSingleResult();
            strength = result.getStrength();
        } catch (NoResultException nre) {
        }
        return strength;
    }

    public void setStrength(Token from, Token to, Layer layer, double strength) {
        Query query = em.createNamedQuery("Node.getStrength");
        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("layer", layer);
        Node result;
        try {
            result = (Node) query.getSingleResult();
            result.setStrength(strength);
        } catch (NoResultException nre) {
            result = new Node();
            result.setFrom(from);
            result.setTo(to);
            result.setLayer(layer);
            result.setStrength(strength);
            em.persist(result);
        }
    }

    private Token getTokenById(Integer id) {
        return em.find(Token.class, id);
    }

    public void setStrength(Integer fromId, Integer toId, Layer source, double strength) {
        setStrength(getTokenById(fromId), getTokenById(toId), source, strength);
    }

    public void generateHiddenNode(List<Token> wordids, List<Token> urls) {
        //if(wordids.size()<3) {
        //    // too small to bother
        //    return;
        //}
        StringBuilder sb = new StringBuilder();
        String separator = "";
        for (Token token : wordids) {
            sb.append(separator).append(String.valueOf(token.getId()));
            separator = ":";
        }
        try {
            Query query = em.createNamedQuery("Token.findByWord");
            query.setParameter("word", sb.toString());
            query.getSingleResult();
        } catch (NoResultException nre) {
            // not there? Well, let's create it and set up the network.
            Token hidden = new Token();
            hidden.setWord(sb.toString());
            em.persist(hidden);
            for (Token from : wordids) {
                setStrength(from, hidden, Layer.SOURCE, 1.0 / wordids.size());
            }
            for (Token to : urls) {
                setStrength(hidden, to, Layer.HIDDEN, 0.1);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public List<Token> getAllHiddenIds(List<Token> from, List<Token> to) {
        Map<Integer, Token> hiddenIds = new HashMap<>();
        for (Token token : from) {
            Query query = em.createQuery("select n from Node n where n.from=:from and n.layer=:layer");
            query.setParameter("from", token);
            query.setParameter("layer", Layer.SOURCE);
            List<Node> hiddenIdList = query.getResultList();
            for (Node hiddenToken : hiddenIdList) {
                hiddenIds.put(hiddenToken.getTo().getId(), hiddenToken.getTo());
            }
        }
        for (Token token : to) {
            Query query = em.createQuery("select n from Node n where n.to=:to and n.layer=:layer");
            query.setParameter("to", token);
            query.setParameter("layer", Layer.HIDDEN);
            List<Node> hiddenIdList = query.getResultList();
            for (Node hiddenToken : hiddenIdList) {
                hiddenIds.put(hiddenToken.getFrom().getId(), hiddenToken.getFrom());
            }
        }
        return new ArrayList<Token>(hiddenIds.values());
    }

}
