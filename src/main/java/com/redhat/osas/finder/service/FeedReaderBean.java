package com.redhat.osas.finder.service;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.logging.Logger;

//import javax.annotation.Resource;
//import javax.ejb.MessageDrivenContext;

@MessageDriven(name = "FeedReaderBean", activationConfig = {
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/FeedReadQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),})
public class FeedReaderBean implements MessageListener {
    @Inject
    FeedService feedService;

    @Inject
    Logger log;

    @PostConstruct
    public void showDeployment() {
        log.severe("MDB created");
    }

    @Override
    public void onMessage(Message message) {
        log.severe("Received Feed Message (" + message.getClass().getName() + ") " + message);
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                String text = textMessage.getText();
                log.severe("Message contents: " + text);
                if (!text.startsWith("NO_URL")) {
                    // this is a url to read.
                    feedService.readFeed(text);
                }

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

}
