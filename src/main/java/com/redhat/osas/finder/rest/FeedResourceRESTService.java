package com.redhat.osas.finder.rest;

import com.redhat.osas.finder.model.Feed;
import com.redhat.osas.finder.service.FeedService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;
import java.util.logging.Logger;

@RequestScoped
@Produces("application/json")
@Path("/feed")
public class FeedResourceRESTService {
    @Inject
    Logger log;

    @Inject
    FeedService feedService;

    @GET
    public List<Feed> getFeeds() {
        //return feedService.listAllFeeds();
        log.severe("getFeeds() called");
        return feedService.listAllFeeds();
        //return new ArrayList<>();
    }

    @PUT
    @Path("{uri}")
    public Feed addFeed(@PathParam("uri") String uri) {
        //return feedService.forceGetFeed(uri);
        log.severe("addFeed(\"" + uri + "\") called");
        return feedService.forceGetFeed(uri);
    }

    @DELETE
    @Path("{uri}")
    public Feed removeFeed(@PathParam("id") int id) {
        log.severe("removeFeed(" + id + ") called");
        return new Feed();
    }
}
