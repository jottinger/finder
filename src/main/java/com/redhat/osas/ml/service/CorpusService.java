package com.redhat.osas.ml.service;

import com.redhat.osas.ml.model.Token;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.*;

@Stateless
public class CorpusService {
    @Inject
    EntityManager em;
    @Inject
    TokenService tokenService;

    static Set<String> stopWords;

    @PostConstruct
    synchronized public void checkStopWords() {
        if (stopWords == null) {
            stopWords = new HashSet<>();
            InputStream is = CorpusService.class.getResourceAsStream("/stop_words.txt");
            Scanner scanner = new Scanner(is);
            while (scanner.hasNext()) {
                stopWords.add(scanner.next().trim());
            }
        }
    }

    public List<Token> getTokensForCorpus(String corpus, boolean stem) {
        List<String> tokenTexts;
        if (stem) {
            tokenTexts = stemText(corpus.toLowerCase());
        } else {
            tokenTexts = new ArrayList<>();
            for (String s : corpus.toLowerCase().split(" ")) {
                tokenTexts.add(s);
            }
        }
        List<Token> tokens = convertTextToTokens(tokenTexts);
        return tokens;
    }

    public List<Token> getTokensForCorpus(String corpus) {
        return getTokensForCorpus(corpus, false);
    }

    private List<String> stemText(String corpus) {
        List<String> tokenTexts = new ArrayList<>();
        TokenStream tokenStream = new StandardTokenizer(
                Version.LUCENE_36, new StringReader(corpus));

        tokenStream = new StopFilter(Version.LUCENE_36, tokenStream, stopWords);
        tokenStream = new PorterStemFilter(tokenStream);

        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        CharTermAttribute charTermAttr = tokenStream.getAttribute(CharTermAttribute.class);
        try {
            while (tokenStream.incrementToken()) {
                tokenTexts.add(charTermAttr.toString());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return tokenTexts;
    }

    private List<Token> convertTextToTokens(List<String> tokenTexts) {
        List<Token> tokens = new ArrayList<>();
        for (String tokenText : tokenTexts) {
            Token token;

            if ((token = tokenService.findToken(tokenText)) == null) {
                token = tokenService.saveToken(tokenText);
            }
            tokens.add(token);
        }
        return tokens;
    }

}
