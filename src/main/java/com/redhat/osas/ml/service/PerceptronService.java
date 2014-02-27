package com.redhat.osas.ml.service;

import com.redhat.osas.ml.model.Layer;
import com.redhat.osas.ml.model.Token;
import com.redhat.osas.ml.service.data.PerceptronData;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jottinge
 * Date: 2/26/14
 * Time: 4:01 PM
 */
@Stateless
public class PerceptronService {
    @Inject
    public NodeService nodeService;
    // uses weights of 0 except for target

    protected void backPropagate(PerceptronData perceptronData, int target) {
        backPropagate(perceptronData, target, 0.5);
    }

    protected void backPropagate(PerceptronData perceptronData, int target, double N) {
        double error, change;

        // calculate errors for output layer
        Map<Integer, Double> outputDeltas = new HashMap<>();
        for (Integer k : perceptronData.getUrlids()) {
            error = (k == target ? 1.0 : 0) - perceptronData.getAo().get(k);
            outputDeltas.put(k, dtanh(perceptronData.getAo().get(k)) * error);
        }

        // calculate errors for hidden layer
        Map<Integer, Double> hiddenDeltas = new HashMap<>();
        for (Integer j : perceptronData.getHiddenids()) {
            error = 0.0;
            for (Integer k : perceptronData.getUrlids()) {
                error += outputDeltas.get(k) *
                        perceptronData.getWo().get(j).get(k);
            }
            hiddenDeltas.put(j, dtanh(perceptronData.getAh().get(j)) * error);
        }

        // update output weights
        for (Integer j : perceptronData.getHiddenids()) {
            for (Integer k : perceptronData.getUrlids()) {
                change = outputDeltas.get(k) * perceptronData.getAh().get(j);
                perceptronData.getWo().get(j).put(k, perceptronData.getWo().get(j).get(k) + N * change);
            }
        }

        // update input weights
        for (Integer i : perceptronData.getWordids()) {
            for (Integer j : perceptronData.getHiddenids()) {
                change = hiddenDeltas.get(j) * perceptronData.getAi().get(i);
                perceptronData.getWi().get(i).put(j, perceptronData.getWi().get(i).get(j) + N * change);
            }
        }
    }

    protected void feedForward(PerceptronData perceptronData) {
        // returns a map; map is [outputtoken -> strength],
        // in perceptronData.ao
        for (Integer j : perceptronData.getAh().keySet()) {
            double sum = 0.0;
            for (Integer i : perceptronData.getAi().keySet()) {
                sum = sum + perceptronData.getAi().get(i) * perceptronData.getWi().get(i).get(j);
            }
            perceptronData.getAh().put(j, Math.tanh(sum));
        }

        for (Integer k : perceptronData.getAo().keySet()) {
            double sum = 0.0;
            for (Integer j : perceptronData.getAh().keySet()) {
                sum = sum + perceptronData.getAh().get(j) * perceptronData.getWo().get(j).get(k);
            }
            perceptronData.getAo().put(k, Math.tanh(sum));
        }
    }

    private static double dtanh(double y) {
        return 1.0 - y * y;
    }

    public void train(List<Token> wordids, List<Token> urlids, Token selectedurl) {
        nodeService.generateHiddenNode(wordids, urlids);
        PerceptronData perceptronData = setupNetwork(wordids, urlids);
        feedForward(perceptronData);
        backPropagate(perceptronData, selectedurl.getId());
        updateDatabase(perceptronData);
    }

    private void updateDatabase(PerceptronData perceptronData) {
        for(Integer i:perceptronData.getWordids()) {
            for(Integer j:perceptronData.getHiddenids()) {
                nodeService.setStrength(i,j,Layer.SOURCE,perceptronData.getWi().get(i).get(j));
            }
        }

        for(Integer j:perceptronData.getHiddenids()) {
            for(Integer k:perceptronData.getUrlids()) {
                nodeService.setStrength(j,k,Layer.HIDDEN,perceptronData.getWo().get(j).get(k));
            }
        }
    }

    public Map<Integer, Double> getResults(List<Token> inputs, List<Token> outputs) {
        PerceptronData perceptronData = setupNetwork(inputs, outputs);
        //System.out.println(perceptronData);
        feedForward(perceptronData);
        //System.out.println(perceptronData);
        return perceptronData.getAo();
    }

    private PerceptronData setupNetwork(List<Token> inputs, List<Token> outputs) {
        PerceptronData perceptronData = new PerceptronData();
        List<Token> hiddenIds = nodeService.getAllHiddenIds(inputs, outputs);

        for (Token input : inputs) {
            perceptronData.getAi().put(input.getId(), 1.0);
            perceptronData.getWordids().add(input.getId());
        }

        for (Token hidden : hiddenIds) {
            perceptronData.getAh().put(hidden.getId(), 1.0);
            perceptronData.getHiddenids().add(hidden.getId());
        }

        for (Token output : outputs) {
            perceptronData.getAo().put(output.getId(), 1.0);
            perceptronData.getUrlids().add(output.getId());
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
}
