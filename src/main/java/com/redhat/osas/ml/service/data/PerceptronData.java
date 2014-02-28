package com.redhat.osas.ml.service.data;

import lombok.Data;
import lombok.ToString;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

@Data
@ToString
public class PerceptronData {
    List<Integer> inputs = new ArrayList<>();
    List<Integer> outputs = new ArrayList<>();
    List<Integer> hiddenids = new ArrayList<>();

    Map<Integer, Double> ai = new HashMap<>();
    Map<Integer, Double> ah = new HashMap<>();
    Map<Integer, Double> ao = new HashMap<>();

    Map<Integer, Map<Integer, Double>> wi = new HashMap<>();
    Map<Integer, Map<Integer, Double>> wo = new HashMap<>();

    public double ah(Integer j) {
        return getAh().get(j);
    }

    public double wo(Integer j, Integer k) {
        return getWo().get(j).get(k);
    }

    public double ao(Integer k) {
        return getAo().get(k);
    }

    public Map<Integer, Double> wo(Integer j) {
        return getWo().get(j);
    }

    public Map<Integer, Double> wi(Integer i) {
        return getWi().get(i);
    }

    public double wi(Integer i, Integer j) {
        return getWi().get(i).get(j);
    }

    public double ai(Integer i) {
        return getAi().get(i);
    }

    public String format() {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);

        out.printf("PerceptronData: %n");

        showList(out, "Inputs", getInputs());
        showList(out, "Hidden Layer", getHiddenids());
        showList(out, "Outputs", getOutputs());

        showMap(out, "Input Strength (ai)", getAi());
        showMap(out, "Hidden Strengths (ah)", getAh());
        showMap(out, "Output Strengths (ao)", getAo());

        showMatrix(out, "Hidden Weights (wi)", getWi());
        showMatrix(out, "Output Weights (wo)", getWo());
        out.println();

        return sw.toString();
    }

    private void showMatrix(PrintWriter out, String title, Map<Integer, Map<Integer, Double>> weights) {
        out.printf("%s%n", title);
        List<Integer> sortedPrimaryKeys = new ArrayList<>(weights.keySet());
        Collections.sort(sortedPrimaryKeys);
        // calculate how many sets of data we have; we can fit 5 sets in a row
        int rows = sortedPrimaryKeys.size() / 5 + ((sortedPrimaryKeys.size() % 5 == 0) ? 0 : 1);

        List<Integer> secondLayerKeys = new ArrayList<>(weights.get(sortedPrimaryKeys.get(0)).keySet());
        Collections.sort(secondLayerKeys);

        while (rows > 0) {
            // now let's calculate how many columns we have..
            int cols = Math.min(5, sortedPrimaryKeys.size());

            // let's get the set of columns for this row
            List<Integer> printKeys = sortedPrimaryKeys.subList(0, cols - 1);
            // print the tit les
            out.printf("        ");
            for (Integer p : printKeys) {
                out.printf(" [%11d]", p);
            }
            out.printf("%n");
            // the weights' keys are ALL THE SAME SIZE (and the same entries)
            for (Integer q : secondLayerKeys) {
                out.printf("[%4d] ", q);
                for (Integer p : printKeys) {
                    out.printf(" %13f", weights.get(p).get(q));
                }
                out.printf("%n");
            }
            sortedPrimaryKeys.remove(printKeys);
            rows--;
        }
    }

    private void showMap(PrintWriter out, String title, Map<Integer, Double> strengths) {
        out.printf("%s:%n", title);
        List<Integer> sorted = new ArrayList<>(strengths.keySet());
        Collections.sort(sorted);
        for (Integer i : sorted) {
            out.printf("   %4d -> %f%n", i, strengths.get(i));
        }
    }

    private void showList(PrintWriter out, String title, List<Integer> data) {
        List<Integer> sorted = new ArrayList<>(data);
        Collections.sort(sorted);
        String separator = "";
        out.printf("%s: [", title);
        for (Integer i : sorted) {
            out.printf("%s%d", separator, i);
            separator = ", ";
        }
        out.printf("]%n");
    }
}
