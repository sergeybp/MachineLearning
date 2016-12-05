package params;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikita on 16.11.16.
 */
public class Params {
    public double mu;
    public Map<Long, Double> bu, bi;
    public double lambda;
    public double gamma;
    public Map<Long, double[]> pu, qi;
    public int f;
    public Map<Long, HashMap<Long, Integer>> ratings;
    public double error;

    public Params() {
        bu = new HashMap<>();
        bi = new HashMap<>();
        pu = new HashMap<>();
        qi = new HashMap<>();
        ratings = new HashMap<>();
    }

    public Params(double lambda, int f, double gamma, double mu) {
        bu = new HashMap<>();
        bi = new HashMap<>();
        pu = new HashMap<>();
        qi = new HashMap<>();
        ratings = new HashMap<>();
        this.lambda = lambda;
        this.f = f;
        this.gamma = gamma;
        this.mu = mu;
    }

    @Override
    public String toString() {
        return String.format("lambda = %.5f, gamma = %.5f, f = %d", lambda, gamma, f);
    }
}
