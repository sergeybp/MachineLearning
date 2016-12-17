package net;

import java.util.function.Function;

/**
 * Created by nikita on 04.12.16.
 */
public enum Activation {
    SIGMOID,
    TANH,
    ReLU;

    public Function<Double, Double> get() {
        switch (this) {
            case SIGMOID: return x -> 1d / (1d + Math.exp(-x));
            case TANH: return Math::tanh;
            case ReLU: return x -> Math.max(0d, x);
        }
        return null;
    }

    public Function<Double, Double> getDiff() {
        final Function<Double, Double> sigma = this.get();
        assert sigma != null;
        switch (this) {
            case SIGMOID: return x -> sigma.apply(x) * (1d - sigma.apply(x));
            case TANH: return x -> 1d - Math.pow(sigma.apply(x), 2);
            case ReLU: return x -> x < 0 ? 0d : 1d;
        }
        return null;
    }


}
