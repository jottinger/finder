package com.redhat.osas.finder.rest;

import com.redhat.osas.finder.model.Entry;
import com.redhat.osas.finder.service.EntryService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: jottinge
 * Date: 3/3/14
 * Time: 12:26 PM
 */
@RequestScoped
@Produces("application/json")
@Path("/entry")
public class EntryResourceRESTService {
    @Inject
    EntryService entryService;
    @Inject
    Logger log;

    @GET
    public List<Entry> getEntries() {
        return entryService.getEntries();
    }

    @GET
    @Path("/page/{page}")
    public List<Entry> getEntries(@PathParam("page") int page) {
        log.severe("getEntries(" + page + ") called");
        return entryService.getEntries(page);
    }

}
