package com.redhat.osas.ml.test;

import com.redhat.osas.Resources;
import com.redhat.osas.util.MathFunctions;
import com.redhat.osas.ml.model.Layer;
import com.redhat.osas.ml.model.Node;
import com.redhat.osas.ml.model.Token;
import com.redhat.osas.ml.service.CorpusService;
import com.redhat.osas.ml.service.NodeService;
import com.redhat.osas.ml.service.PerceptronService;
import com.redhat.osas.ml.service.TokenService;
import com.redhat.osas.ml.service.data.PerceptronData;
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
                .addClasses(CorpusService.class, Token.class, Resources.class)
                .addClasses(Node.class)
                .addClasses(MathFunctions.class, Pair.class)
                .addClasses(NodeService.class, Layer.class)
                .addClasses(PerceptronData.class, PerceptronService.class)
                .addClasses(TokenService.class)
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
        String corpus = "Now is the time for all good men to come to the aid of their country.";
        System.out.println(corpusService.getTokensForCorpus(corpus));

        List<Token> from = corpusService.getTokensForCorpus("wWorld wRiver wBank");
        from = corpusService.getTokensForCorpus("wWorld wBank");
        List<Token> to = corpusService.getTokensForCorpus("uWorldBank uRiver uEarth");
        nodeService.generateHiddenNode(from, to);
        //showAll("from Token");
        //showAll("from Node");

        //System.out.println("---------------------------------------------------------");
        //System.out.println("*********************************************************");
        //System.out.println("---------------------------------------------------------");
        //System.out.println(perceptronService.getResults(from, to));
        //System.out.println("---------------------------------------------------------");
        //System.out.println("*********************************************************");
        //System.out.println("---------------------------------------------------------");

        //perceptronService.train(from, to, corpusService.findToken("uWorldBank"));

        for (int i = 0; i < 30; i++) {
            perceptronService.train(corpusService.getTokensForCorpus("wWorld wBank"),
                    to, tokenService.findToken("uWorldBank"));
            perceptronService.train(corpusService.getTokensForCorpus("wRiver wBank"),
                    to, tokenService.findToken("uRiver"));
            perceptronService.train(corpusService.getTokensForCorpus("wWorld"),
                    to, tokenService.findToken("uEarth"));
        }
        System.out.println("---------------------------------------------------------");
        System.out.println("*********************************************************");
        System.out.println("---------------------------------------------------------");
        Queue<Pair<Token, Double>> queue=perceptronService.search("wWorld wBank");
        while(!queue.isEmpty()) {
            System.out.println(queue.poll());
        }
        System.out.println(perceptronService.search("wWorld wBank", to));
        System.out.println(perceptronService.mapResultsToTokens(perceptronService.getResults(corpusService.getTokensForCorpus("wRiver wBank"),
                to)));
        System.out.println(perceptronService.mapResultsToTokens(perceptronService.getResults(corpusService.getTokensForCorpus("wBank"),
                to)));
        System.out.println("---------------------------------------------------------");
        System.out.println("*********************************************************");
        System.out.println("---------------------------------------------------------");

        showAll("from Token");
        showAll("from Node");

    }

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
