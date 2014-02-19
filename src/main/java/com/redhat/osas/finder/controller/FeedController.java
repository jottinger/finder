package com.redhat.osas.finder.controller;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

import com.redhat.osas.finder.model.Feed;
import com.redhat.osas.finder.service.FeedService;

@Model
public class FeedController {
	@Inject
	private FacesContext facesContext;

	@Inject
	Logger log;

	@Produces
	@Named
	@Getter
	@Setter
	String uri;

	@Inject
	FeedService feedService;

	public void addFeed() {
		log.severe("Trying to add " + uri);
		feedService.forceGetFeed(uri);
	}

	public List<Feed> getAllFeeds() {
		return feedService.listAllFeeds();
	}
}
