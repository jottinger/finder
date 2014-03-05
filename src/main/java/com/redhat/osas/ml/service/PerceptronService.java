package com.redhat.osas.ml.service;

import com.redhat.osas.ml.model.Layer;
import com.redhat.osas.ml.model.Token;
import com.redhat.osas.ml.service.data.PerceptronData;
import com.redhat.osas.util.Pair;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;

import static com.redhat.osas.util.MathFunctions.dtanh;
import static java.lang.Math.tanh;

@Stateless
public class PerceptronService {
    @Inject
    NodeService nodeService;
    @Inject
    TokenService tokenService;
    @Inject
    CorpusService corpusService;

    protected void backPropagate(PerceptronData perceptronData, Map<Integer, Double> targets) {
        backPropagate(perceptronData, targets, 0.5);
    }

    protected void backPropagate(PerceptronData perceptronData, Map<Integer, Double> targets, double N) {
        double error, change;

        // calculate errors for output layer
        Map<Integer, Double> outputDeltas = new HashMap<>();
        for (Integer k : perceptronData.getOutputs()) {
            error = targets.get(k) - perceptronData.ao(k);
            outputDeltas.put(k, dtanh(perceptronData.ao(k)) * error);
        }

        //System.out.println("output deltas: "+outputDeltas);

        // calculate errors for hidden layer
        Map<Integer, Double> hiddenDeltas = new HashMap<>();
        for (Integer j : perceptronData.getHiddenids()) {
            error = 0.0;
            for (Integer k : perceptronData.getOutputs()) {
                error += outputDeltas.get(k) * perceptronData.wo(j, k);
            }
            hiddenDeltas.put(j, dtanh(perceptronData.ah(j)) * error);
        }

        //System.out.println("hidden deltas: "+hiddenDeltas);
        //System.out.println("wo was: \n"+perceptronData.getWo());

        // update output weights
        for (Integer j : perceptronData.getHiddenids()) {
            for (Integer k : perceptronData.getOutputs()) {
                change = outputDeltas.get(k) * perceptronData.ah(j);
                perceptronData.wo(j).put(k, perceptronData.wo(j, k) + N * change);
            }
        }
        //System.out.println("wo is: \n"+perceptronData.getWo());

        // update input weights
        for (Integer i : perceptronData.getInputs()) {
            for (Integer j : perceptronData.getHiddenids()) {
                change = hiddenDeltas.get(j) * perceptronData.ai(i);
                perceptronData.wi(i).put(j, perceptronData.wi(i, j) + N * change);
            }
        }
    }

    protected void feedForward(PerceptronData perceptronData) {
        // returns a map; map is [output token id-> strength],
        // in perceptronData.ao
        for (Integer j : perceptronData.getAh().keySet()) {
            double sum = 0.0;
            for (Integer i : perceptronData.getAi().keySet()) {
                sum = sum + perceptronData.wi(i, j);
            }
            perceptronData.getAh().put(j, tanh(sum));
        }

        for (Integer k : perceptronData.getAo().keySet()) {
            double sum = 0.0;
            for (Integer j : perceptronData.getAh().keySet()) {
                sum = sum + perceptronData.ah(j) * perceptronData.wo(j, k);
            }
            perceptronData.getAo().put(k, tanh(sum));
        }
    }

    private void updateDatabase(PerceptronData perceptronData) {
        for (Integer i : perceptronData.getInputs()) {
            for (Integer j : perceptronData.getHiddenids()) {
                nodeService.setStrength(i, j, Layer.SOURCE, perceptronData.wi(i, j));
            }
        }

        for (Integer j : perceptronData.getHiddenids()) {
            for (Integer k : perceptronData.getOutputs()) {
                nodeService.setStrength(j, k, Layer.HIDDEN, perceptronData.wo(j, k));
            }
        }
    }

    public Map<Integer, Double> getResults(List<Token> inputs, List<Token> outputs) {
        PerceptronData perceptronData = setupNetwork(inputs, outputs);
        //System.out.println(perceptronData);
        feedForward(perceptronData);
        //System.out.println(perceptronData.format());
        return perceptronData.getAo();
    }

    public Map<Token, Double> mapResultsToTokens(Map<Integer, Double> perceptronResults) {
        Map<Token, Double> results = new HashMap<>();
        for (Integer k : perceptronResults.keySet()) {
            results.put(tokenService.findToken(k), perceptronResults.get(k));
        }
        return results;
    }

    private PerceptronData setupNetwork(List<Token> inputs, List<Token> outputs) {
        PerceptronData perceptronData = new PerceptronData();
        List<Token> hiddenIds = nodeService.getAllHiddenIds(inputs, outputs);

        for (Token input : inputs) {
            perceptronData.getAi().put(input.getId(), 1.0);
            perceptronData.getInputs().add(input.getId());
        }

        for (Token hidden : hiddenIds) {
            perceptronData.getAh().put(hidden.getId(), 1.0);
            perceptronData.getHiddenids().add(hidden.getId());
        }

        for (Token output : outputs) {
            perceptronData.getAo().put(output.getId(), 1.0);
            perceptronData.getOutputs().add(output.getId());
        }

        for (Token input : inputs) {
            Map<Integer, Double> temp = new HashMap<>();
            for (Token hidden : hiddenIds) {
                temp.put(hidden.getId(), nodeService.getStrength(input, hidden, Layer.SOURCE));
            }
            perceptronData.getWi().put(input.getId(), temp);
        }

        for (Token hidden : hiddenIds) {
            Map<Integer, Double> temp = new HashMap<>();
            for (Token output : outputs) {
                temp.put(output.getId(), nodeService.getStrength(hidden, output, Layer.HIDDEN));
            }
            perceptronData.getWo().put(hidden.getId(), temp);
        }
        return perceptronData;
    }

    public Queue<Pair<Token, Double>> search(String corpus, List<Token> outputs) {
        List<Token> corpora = corpusService.getTokensForCorpus(corpus);
        return search(corpora, outputs);
    }

    public Queue<Pair<Token, Double>> search(String corpus) {
        return search(corpus, nodeService.findAllOutputs());
    }

    public void train(List<Token> inputs, List<Token> outputs, Token target) {
        nodeService.generateHiddenNodes(inputs, outputs);
        PerceptronData perceptronData = setupNetwork(inputs, outputs);

        feedForward(perceptronData);
        Map<Integer, Double> targets = new HashMap<>();
        for (Token output : outputs) {
            targets.put(output.getId(), 0.0);
        }
        targets.put(target.getId(), 1.0);
        backPropagate(perceptronData, targets);
        //System.out.println(perceptronData);
        updateDatabase(perceptronData);
    }

    public void train(String corpora, String targetCorpora, String target) {
        List<Token> inputs = corpusService.getTokensForCorpus(corpora);
        List<Token> targets = corpusService.getTokensForCorpus(targetCorpora);
        train(inputs, targets, tokenService.findToken(target, true));
    }

    public Queue<Pair<Token, Double>> search(List<Token> corpora, List<Token> targets) {
        Queue<Pair<Token, Double>> queue = new PriorityQueue<>(5, new Comparator<Pair<Token, Double>>() {
            @Override
            public int compare(Pair<Token, Double> o1, Pair<Token, Double> o2) {
                return Double.compare(o2.getV(), o1.getV());
            }
        });

        //System.out.println(corpora);
        Map<Integer, Double> results = getResults(corpora, targets);
        //System.out.println(results);
        Map<Token, Double> mappedResults = mapResultsToTokens(results);
        for (Map.Entry<Token, Double> entry : mappedResults.entrySet()) {
            queue.add(new Pair<>(entry.getKey(), entry.getValue()));
        }
        return queue;
    }
}
