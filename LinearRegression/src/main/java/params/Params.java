package params;

import optimizers.Optimizers;

/**
 * Created by nikita on 23.09.16.
 */
public class Params {
    public Optimizers optimizers;
    public double epsilon;
    public double step;
    public int sizeOfPopulation;
    public double deviation;

    public Params(Optimizers optimizers) {
        this.optimizers = optimizers;
    }

    public Params(Optimizers optimizers, double epsilon, double step, int sizeOfPopulation) {
        this.optimizers = optimizers;
        this.epsilon = epsilon;
        this.step = step;
        this.sizeOfPopulation = sizeOfPopulation;
    }

    public Params(Optimizers optimizers, double epsilon, double step, int sizeOfPopulation, double deviation) {
        this.optimizers = optimizers;
        this.epsilon = epsilon;
        this.step = step;
        this.sizeOfPopulation = sizeOfPopulation;
        this.deviation = deviation;
    }

    public Optimizers getOptimizers() {
        return optimizers;
    }

    public void setOptimizers(Optimizers optimizers) {
        this.optimizers = optimizers;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public String toString() {
        switch (optimizers) {
            case GRADIENT_DESCENT:
                return String.format("Params: method = %s, deviation = %.6f, epsilon = %.10f, step = %.13f", "GRADIENT_DESCENT", deviation, epsilon, step);
            case GENETIC:
                return String.format("Params: method = %s, deviation = %.6f, size of population = %d", "GENETIC", deviation, sizeOfPopulation);
        }
        return "";
    }
}
