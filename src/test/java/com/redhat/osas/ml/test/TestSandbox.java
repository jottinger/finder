package com.redhat.osas.ml.test;

import com.redhat.osas.Resources;
import com.redhat.osas.ml.model.Token;
import com.redhat.osas.ml.service.CorpusService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.testng.Assert.assertNotNull;

public class TestSandbox extends Arquillian {
    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(CorpusService.class, Token.class, Resources.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                        // Deploy our test datasource
                .addAsWebInfResource("test-ds.xml");
    }

    @Inject
    CorpusService corpusBuilder;

    @Test
    public void testPerceptron() {
        assertNotNull(corpusBuilder);
    }
}

