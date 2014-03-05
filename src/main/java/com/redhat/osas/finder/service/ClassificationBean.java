package com.redhat.osas.finder.service;

import com.redhat.osas.finder.model.Classification;
import com.redhat.osas.finder.model.Entry;
import com.redhat.osas.ml.model.Token;
import com.redhat.osas.ml.service.CorpusService;
import com.redhat.osas.ml.service.PerceptronService;
import com.redhat.osas.util.Pair;
import org.json.simple.JSONObject;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.*;
import java.util.logging.Logger;

@MessageDriven(name = "ClassificationBean", activationConfig = {
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/ClassificationQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),})
public class ClassificationBean implements MessageListener {
    @Inject
    PerceptronService perceptronService;
    @Inject
    CorpusService corpusService;
    @Inject
    EntryService entryService;
    @Inject
    ClassificationService classificationService;
    @Inject
    Logger log;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                Classification classification = (Classification) objectMessage.getObject();
                List<Token> targets = corpusService.getTokensForCorpus("wGood wNeutral wBad");
                Entry entry = classification.getEntry();
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
                Queue<Pair<Token, Double>> results = perceptronService.search(corpus, targets);
                Map<String, Double> jsonData = new HashMap<>();
                while (!results.isEmpty()) {
                    Pair<Token, Double> pair = results.poll();
                    if (classification.getAutoClassification() == null) {
                        classification.setAutoClassification(pair.getK());
                    }
                    jsonData.put(pair.getK().getWord(), pair.getV());
                }
                // need to serialize into JSON, and save off results
                JSONObject jsonObject = new JSONObject();
                jsonObject.putAll(jsonData);
                classification.setScoresJSON(jsonObject.toJSONString());
                // TODO need to sync with database now...

            }
        } catch (JMSException jmsException) {
            jmsException.printStackTrace();
        }
    }
}
