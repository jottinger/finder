package com.redhat.osas.finder.service;

import com.redhat.osas.finder.model.Classification;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.*;
import java.util.List;
import java.util.logging.Logger;

@MessageDriven(name = "ClassificationBean", activationConfig = {
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/ClassificationTriggerQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),})
public class ClassificationTriggerBean implements MessageListener {
    @Inject
    ClassificationService classificationService;
    @Inject
    JMSContext context;

    @Resource(mappedName = "java:/queue/ClassificationQueue")
    private Queue classificationQueue;


    @Inject
    Logger log;

    @Override
    public void onMessage(Message message) {
        JMSProducer producer = context.createProducer();
        try {
            // What we need to do is find classification entries that have not been
            // filled out. When we find them, we send a message to *another* queue,
            // saying "run this classification" - with an expiry of 45 seconds.
            List<Classification> classifications = classificationService.findUnclassifiedEntries();
            for (Classification classification : classifications) {
                ObjectMessage classificationMessage = context.createObjectMessage(classification);
                classificationMessage.setJMSExpiration(45 * 1000);
                producer.send(classificationQueue, classificationMessage);
            }
        } catch (JMSException jmsException) {
            jmsException.printStackTrace();
        }
    }
}
