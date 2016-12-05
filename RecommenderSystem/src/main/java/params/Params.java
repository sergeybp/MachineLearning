package params;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikita on 16.11.16.
 */
public class Params {
    public double mu;
    public Map<Long, Double> bu, bi;
    public double lambda1;
    public double lambda2;
    public double gamma;
    public Map<Long, double[]> pu, qi;
    public int f;
    public double rmse;

    public Params() {
        bu = new HashMap<>();
        bi = new HashMap<>();
        pu = new HashMap<>();
        qi = new HashMap<>();
    }

    @Override
    public String toString() {
        return String.format("lambda1 = %.5f, lambda2 = %.5f, gamma = %.5f, f = %d", lambda1, lambda2, gamma, f);
    }
}
