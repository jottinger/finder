package com.redhat.osas.finder.service;

import com.redhat.osas.finder.model.Entry;
import com.redhat.osas.finder.model.Feed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.impl.HttpURLFeedFetcher;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class FeedService {
    @Inject
    EntityManager em;
    @Inject
    ClassificationService classificationService;
    @Inject
    Logger log;

    public Set<Entry> updateFeed(SyndFeed syndFeed) {
        Feed feed = forceGetFeed(syndFeed.getUri());
        feed.setActive(true);
        return updateFeed(feed, syndFeed);
    }

    public Set<Entry> updateFeed(Feed feed, SyndFeed syndFeed) {
        Query query;
        Set<Entry> newEntries = new HashSet<>();
        // this is the timestamp for the entries and feed.
        Date now = new Date();

        // next read should be two hours from now
        Date then = new Date();
        then.setTime(now.getTime() + 120 * 60 * 1000);

        feed.setTitle(syndFeed.getTitle());

        // update the lastread to a known value.
        feed.setLastRead(now);
        feed.setNextRead(then);

        // now let's iterate over the SyndEntries...
        for (SyndEntry syndEntry : syndFeed.getEntries()) {
            Entry entry = null;
            // find the corresponding Entry
            query = em.createNamedQuery("Entry.findByUri");
            query.setParameter("uri", syndEntry.getUri());
            query.setParameter("feed", feed);
            try {
                entry = (Entry) query.getSingleResult();
            } catch (NoResultException nre) {
                // no Entry? Create one.
                entry = new Entry(feed, syndEntry);
                log.severe("Wrote " + entry);
                em.persist(entry);
                newEntries.add(entry);
            }
            // set the lastread to a known value.
            entry.setLastRead(now);
        }
        // now let's clear out the entries that don't match the feed's
        // timestamp...
        query = em.createNamedQuery("Entry.getOldEntries");
        query.setParameter("feed", feed);
        query.setParameter("now", now);
        @SuppressWarnings("unchecked")
        List<Entry> oldEntries = (List<Entry>) query.getResultList();
        for (Entry oldEntry : oldEntries) {
            classificationService.removeForEntry(oldEntry);
            em.remove(oldEntry);
        }
        return newEntries;
    }

    public Feed forceGetFeed(String uri) {
        Feed feed = null;
        // first, find the Feed for this syndFeed
        Query query = em.createNamedQuery("Feed.findByUri");
        query.setParameter("uri", uri);
        try {
            feed = (Feed) query.getSingleResult();
        } catch (NoResultException nre) {
            // no feed? Well, let's create one.
            feed = new Feed();
            // yes, it's active, and read it as soon as possible
            feed.setUri(uri);
            feed.setActive(true);
            feed.setNextRead(new Date(0));
            em.persist(feed);
        }

        return feed;

    }

    public Set<Entry> readFeed(String uri) {
        FeedFetcher feedFetcher = new HttpURLFeedFetcher();
        Feed feed = forceGetFeed(uri);
        Set<Entry> newEntries = new HashSet<>();
        try {
            log.severe("Trying to read " + uri);
            URL url = new URL(uri);
            newEntries = updateFeed(feed, feedFetcher.retrieveFeed(url));
        } catch (Exception exception) {
            log.log(Level.SEVERE, "deactivating feed " + feed.getUri(),
                    exception);
            feed.setActive(false);
        }
        return newEntries;
    }

    @SuppressWarnings("unchecked")
    public List<Feed> listAllFeeds() {
        Query query = em.createNamedQuery("Feed.findAll");
        return (List<Feed>) query.getResultList();
    }

    public List<Feed> getScheduledFeeds() {
        Query query = em.createNamedQuery("Feed.findFeedsToRead");
        query.setParameter("now", new Date());
        @SuppressWarnings("unchecked")
        List<Feed> feeds = (List<Feed>) query.getResultList();
        return feeds;
    }
}
