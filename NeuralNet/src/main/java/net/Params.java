package net;

/**
 * Created by nikita on 05.12.16.
 */
public class Params {
    public double rate;
    public double reg;
    public double lambda;
    public double improve;

    public Params() {};

    public Params(double rate, double reg, double lambda, double improve) {
        this.rate = rate;
        this.reg = reg;
        this.lambda = lambda;
        this.improve = improve;
    }

    public Params(Params params) {
        this(params.rate, params.reg, params.lambda, params.improve);
    }

    @Override
    public String toString() {
        return String.format("Params: rate = %.6f, reg = %.6f, lambda = %.9f, improve = %.6f", rate, reg, lambda, improve);
    }
}
