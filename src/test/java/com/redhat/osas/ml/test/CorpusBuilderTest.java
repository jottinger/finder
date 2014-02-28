package com.redhat.osas.ml.test;

import com.redhat.osas.Resources;
import com.redhat.osas.ml.model.Layer;
import com.redhat.osas.ml.model.Node;
import com.redhat.osas.ml.model.Token;
import com.redhat.osas.ml.service.CorpusService;
import com.redhat.osas.ml.service.NodeService;
import com.redhat.osas.ml.service.PerceptronService;
import com.redhat.osas.ml.service.TokenService;
import com.redhat.osas.ml.service.data.PerceptronData;
import com.redhat.osas.util.MathFunctions;
import com.redhat.osas.util.Pair;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import static org.testng.Assert.assertNotNull;

public class CorpusBuilderTest extends Arquillian {
    @Deployment
    public static Archive<?> createTestArchive() {
        File[] luceneDependencies = Maven.resolver().
                loadPomFromFile("pom.xml").importRuntimeDependencies()
                .resolve().withTransitivity().asFile();
        Archive archive = ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(CorpusService.class, Token.class, Resources.class, Node.class, MathFunctions.class, Pair.class)
                .addClasses(NodeService.class, Layer.class, PerceptronData.class, PerceptronService.class, TokenService.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("stop_words.txt")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(luceneDependencies)
                .addAsWebInfResource("test-ds.xml");

        System.out.println(archive.toString(true));

        return archive;
    }

    @Inject
    CorpusService corpusService;
    @Inject
    TokenService tokenService;
    @Inject
    NodeService nodeService;
    @Inject
    PerceptronService perceptronService;
    @Inject
    EntityManager em;

    @Test
    public void testCorpusBuilderConfiguration() {
        assertNotNull(corpusService);
    }

    @Test
    public void testParsing() {
        //String corpus = "Now is the time for all good men to come to the aid of their country.";
        //System.out.println(corpusService.getTokensForCorpus(corpus));
        List<Token> from = corpusService.getTokensForCorpus("wWorld wBank wRiver");
        List<Token> to = corpusService.getTokensForCorpus("uWorldBank uRiver uEarth");
        nodeService.generateHiddenNodes(from, to);
        for (int i = 31; i > 0; i--) {
            perceptronService.train(corpusService.getTokensForCorpus("wWorld wBank"), to, tokenService.findToken("uWorldBank"));
            perceptronService.train(corpusService.getTokensForCorpus("wRiver wBank"), to, tokenService.findToken("uRiver"));
            perceptronService.train(corpusService.getTokensForCorpus("wWorld"), to, tokenService.findToken("uEarth"));
        }
        //for (int i = 0; i < 30; i++) {
        //    perceptronService.train("wWorld wBank", "uWorldBank uRiver uEarth", "uWorldBank");
        //    perceptronService.train("wRiver wBank", "uWorldBank uRiver uEarth", "uRiver");
        //    perceptronService.train("wWorld", "uWorldBank uRiver uEarth", "uEarth");
        //}

        //System.out.println(perceptronService.getResults(from, to));
        Queue<Pair<Token, Double>> queue = query("wWorld wBank", true);
        queue = query("wRiver wBank", true);
        queue = query("wBank", true);
/*
        System.out.println(perceptronService.mapResultsToTokens(perceptronService.getResults(corpusService.getTokensForCorpus("wRiver wBank"),
                to)));
        System.out.println(perceptronService.mapResultsToTokens(perceptronService.getResults(corpusService.getTokensForCorpus("wBank"),
                to)));
  */
        showAll("from Token");
        showAll("from Node");
    }

    private Queue<Pair<Token, Double>> query(String corpus, boolean display) {
        Queue<Pair<Token, Double>> queue = perceptronService.search(corpus);
        if (display) {
            System.out.println("Query: " + corpus);
            Iterator<Pair<Token, Double>> e = queue.iterator();
            while (e.hasNext()) {
                Pair<Token, Double> d = e.next();
                System.out.println(d);
            }
        }
        return queue;
    }

    @SuppressWarnings("UnusedDeclaration")
    private void showAll(String queryText) {
        Query query = em.createQuery(queryText);
        System.out.println("---------------------------------------------------------");
        System.out.println(queryText);
        System.out.println("---------------------------------------------------------");
        for (Object o : query.getResultList()) {
            System.out.println(o);
        }
        System.out.println("---------------------------------------------------------");
    }
}
