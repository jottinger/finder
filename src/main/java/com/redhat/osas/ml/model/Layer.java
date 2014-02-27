package com.redhat.osas.ml.model;

public enum Layer {
    SOURCE(-0.2),
    HIDDEN(0.0),;
    double defaultStrength;

    Layer(double strength) {
        defaultStrength = strength;
    }

    public double getStrength() {
        return defaultStrength;
    }
}
