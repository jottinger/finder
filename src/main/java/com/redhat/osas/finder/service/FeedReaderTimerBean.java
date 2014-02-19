package com.redhat.osas.finder.service;

import com.redhat.osas.finder.model.Feed;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class FeedReaderTimerBean {
    @Inject
    FeedService feedService;

    @Inject
    Logger log;

    @Inject
    private JMSContext context;

    @Resource(mappedName = "java:/queue/FeedReadQueue")
    private Queue queue;

    @Schedule(minute = "*", hour = "*")
    public void handleTimeout() {
        JMSProducer producer=context.createProducer();
        // log.severe("TRYING TO READ FEEDS");
        // feedService.readFeeds();
        TextMessage textMessage = context.createTextMessage("NO_URL: This is a feed request message");
        producer.send(queue, textMessage);

        // what we really want to send is a set of URLs to read, though.
        List<Feed> feedList=feedService.getScheduledFeeds();
        for(Feed feed:feedList) {
            TextMessage urlMessage=context.createTextMessage(feed.getUri());
            producer.send(queue, urlMessage);
        }
    }
}