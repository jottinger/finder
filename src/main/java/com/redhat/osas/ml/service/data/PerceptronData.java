package com.redhat.osas.ml.service.data;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString
public class PerceptronData {

    List<Integer> wordids=new ArrayList<>();
    List<Integer> urlids=new ArrayList<>();
    List<Integer> hiddenids=new ArrayList<>();

    Map<Integer, Double> ai=new HashMap<>();
    Map<Integer, Double> ah=new HashMap<>();
    Map<Integer, Double> ao=new HashMap<>();

    Map<Integer, Map<Integer, Double>> wi=new HashMap<>();
    Map<Integer, Map<Integer, Double>> wo=new HashMap<>();
}
