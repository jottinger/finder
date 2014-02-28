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
    @Inject
    TokenService tokenService;

    public double getStrength(Token from, Token to, Layer layer) {
        Query query = em.createNamedQuery("Node.getStrength");
        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("layer", layer);
        double strength = layer.getStrength();
        try {
            Node result = (Node) query.getSingleResult();
            strength = result.getStrength();
        } catch (NoResultException ignored) {
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

    public void setStrength(Integer fromId, Integer toId, Layer source, double strength) {
        setStrength(tokenService.findToken(fromId),
                tokenService.findToken(toId), source, strength);
    }

    public void generateHiddenNodes(List<Token> inputs, List<Token> outputs) {
        //if(inputs.size()<3) {
        //    // too small to bother
        //    return;
        //}
        StringBuilder sb = new StringBuilder();
        String separator = "";
        for (Token token : inputs) {
            sb.append(separator).append(String.valueOf(token.getId()));
            separator = ":";
        }
        if (tokenService.findToken(sb.toString()) == null) {
            // not there? Well, let's create it and set up the network.
            Token hidden = tokenService.saveToken(sb.toString());
            for (Token from : inputs) {
                setStrength(from, hidden, Layer.SOURCE, 1.0 / inputs.size());
            }
            for (Token to : outputs) {
                setStrength(hidden, to, Layer.HIDDEN, 0.1);
            }
        }
    }

    public List<Token> getAllHiddenIds(List<Token> from, List<Token> to) {
        Map<Integer, Token> hiddenIds = new HashMap<>();
        for (Token token : from) {
            Query query = em.createNamedQuery("Node.byFromAndLayer");
            query.setParameter("from", token);
            query.setParameter("layer", Layer.SOURCE);
            @SuppressWarnings("unchecked")
            List<Node> hiddenIdList = (List<Node>) query.getResultList();
            for (Node hiddenToken : hiddenIdList) {
                hiddenIds.put(hiddenToken.getTo().getId(), hiddenToken.getTo());
            }
        }
        for (Token token : to) {
            Query query = em.createNamedQuery("Node.byToAndLayer");
            query.setParameter("to", token);
            query.setParameter("layer", Layer.HIDDEN);
            @SuppressWarnings("unchecked")
            List<Node> hiddenIdList = (List<Node>) query.getResultList();
            for (Node hiddenToken : hiddenIdList) {
                hiddenIds.put(hiddenToken.getFrom().getId(), hiddenToken.getFrom());
            }
        }
        return new ArrayList<>(hiddenIds.values());
    }


    public List<Token> findAllOutputs() {
        Query query = em.createNamedQuery("Node.getAllOutputsByLayer");
        query.setParameter("layer", Layer.HIDDEN);
        //noinspection unchecked
        return (List<Token>) query.getResultList();
    }
}
