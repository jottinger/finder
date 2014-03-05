package com.redhat.osas.finder.service;

import com.redhat.osas.finder.model.Entry;
import com.redhat.osas.finder.model.User;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.*;
import java.util.List;
import java.util.Set;
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
    UserService userService;
    @Inject
    ClassificationService classificationService;

    @Inject
    Logger log;

    @Inject
    private JMSContext context;

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
                    // this is a url to read. Get the new entries, if any
                    Set<Entry> newEntries = feedService.readFeed(text);
                    // now for each user, create a new Classification
                    List<User> users = userService.findAll();
                    for (Entry entry : newEntries) {
                        for (User user : users) {
                            classificationService.save(entry, user);
                        }
                    }
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

}
