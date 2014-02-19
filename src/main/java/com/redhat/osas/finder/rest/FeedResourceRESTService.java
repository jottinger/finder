package com.redhat.osas.finder.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.redhat.osas.finder.model.Feed;
import com.redhat.osas.finder.service.FeedService;

@Path("/feeds")
@RequestScoped
public class FeedResourceRESTService {
	@Inject
	Logger log;
	
	@Inject
	FeedService feedService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Feed> listAllFeeds() {
		return feedService.listAllFeeds();
	}

	@GET
	@Path("/uri:.*")
	@Produces(MediaType.APPLICATION_JSON)
	public Feed addFeed(String uri) {
		return feedService.forceGetFeed(uri);
	}

}
