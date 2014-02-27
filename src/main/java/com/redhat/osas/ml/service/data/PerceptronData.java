package com.redhat.osas.ml.service.data;

import lombok.Data;
import lombok.ToString;
import sun.org.mozilla.javascript.internal.Scriptable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Double wo(Integer j, Integer k) {
        return getWo().get(j).get(k);
    }

    public double ao(Integer k) {
        return getAo().get(k);
    }

    public Map<Integer, Double> wo(Integer j) {
        return getWo().get(j);
    }

    public Map<Integer, Double> wi(Integer j) {
        return getWi().get(j);
    }

    public Double wi(Integer i, Integer j) {
        return getWi().get(i).get(j);
    }

    public Double ai(Integer i) {
        return getAi().get(i);
    }
}
