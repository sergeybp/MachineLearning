package params;

import utils.Data;

/**
 * Created by nikita on 05.11.16.
 */
public class RobustRegressionParams {
    public Kernels kernel;
    public Data train;
    public double mse;
    public int k;
    public double[] gamma;

    public RobustRegressionParams(Kernels kernel, Data train, int k, double[] gamma, double mse) {
        this.kernel = kernel;
        this.train = train;
        this.mse = mse;
        this.k = k;
        this.gamma = gamma;
    }

    public RobustRegressionParams() {
    }

    @Override
    public String toString() {
        return String.format("Robust regression params: k = %d, kernel = %s, mse = %.10f", k, kernel, mse);
    }
}
