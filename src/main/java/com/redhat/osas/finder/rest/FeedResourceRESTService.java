package com.redhat.osas.finder.rest;

import com.redhat.osas.finder.model.Feed;
import com.redhat.osas.finder.service.FeedService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
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
    @Produces(MediaType.APPLICATION_JSON)
    public List<Feed> getFeeds() {
        //return feedService.listAllFeeds();
        log.severe("getFeeds() called");
        return new ArrayList<>();
    }

    @PUT
    @Path("{uri}")
    @Produces(MediaType.APPLICATION_JSON)
    public Feed addFeed(@PathParam("uri") String uri) {
        //return feedService.forceGetFeed(uri);
        log.severe("addFeed(" + uri + ") called");
        return new Feed();
    }

    @DELETE
    @Path("{uri}")
    @Produces(MediaType.APPLICATION_JSON)
    public Feed removeFeed(@PathParam("uri") String uri) {
        log.severe("removeFeed(" + uri + ") called");
        return new Feed();
    }
}
