package com.redhat.osas.util;

public class MathFunctions {
    public static double dtanh(double y) {
        return 1.0 - y * y;
    }

    public static double tanh(double y) {
        return Math.tanh(y);
    }
}
