package net;

import java.util.function.Function;

/**
 * Created by nikita on 03.12.16.
 */
public class Neuron {
    public Function<Double, Double> activation;
    public Function<Double, Double> diffActivation;
    public int n, m;

    public Neuron(int n, int m, Function<Double, Double> activation, Function<Double, Double> diffActivation) {
        this.activation = activation;
        this.diffActivation = diffActivation;
        this.n = n;
        this.m = m;
    }

}
