package com.redhat.osas.finder.rest;

import com.redhat.osas.finder.model.Entry;
import com.redhat.osas.finder.service.EntryService;
import com.redhat.osas.ml.model.Token;
import com.redhat.osas.ml.service.CorpusService;
import com.redhat.osas.ml.service.PerceptronService;
import com.redhat.osas.ml.service.TokenService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
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
    PerceptronService perceptronService;
    @Inject
    CorpusService corpusService;
    @Inject
    TokenService tokenService;

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

    @PUT
    @Path("/train/{id}/{classification}")
    public String train(@PathParam("id") int id, @PathParam("classification") String classification) {
        classification = classification.trim().toLowerCase();
        if (!"wGood wNeutral wBad".contains(classification)) {
            return "UNKNOWN CLASSIFICATION";
        }
        List<Token> targets = corpusService.getTokensForCorpus("wGood wNeutral wBad");
        List<Token> corpora = entryService.getTokensForEntry(id);
        perceptronService.train(corpora, targets, tokenService.findToken(classification));
        return "SUCCESS";
    }

}
