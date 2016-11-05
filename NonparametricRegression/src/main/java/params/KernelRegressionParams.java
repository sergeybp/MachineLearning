package params;

import utils.Data;

/**
 * Created by nikita on 23.09.16.
 */
public class KernelRegressionParams {
    public double h;
    public Kernels kernel;
    public Data train;
    public double mse;
    public boolean isHConstant;
    public int k;

    public KernelRegressionParams(double h, Kernels kernel, double mse) {
        this.h = h;
        this.kernel = kernel;
        this.mse = mse;
    }

    public KernelRegressionParams(double h, Kernels kernel) {
        this.h = h;
        this.kernel = kernel;
    }

    public KernelRegressionParams(double h, Kernels kernel, Data train, double mse) {
        this.h = h;
        this.kernel = kernel;
        this.train = train;
        this.mse = mse;
    }

    public KernelRegressionParams(double h, Kernels kernel, Data train, boolean isHConstant, int k, double mse) {
        this.h = h;
        this.kernel = kernel;
        this.train = train;
        this.mse = mse;
        this.isHConstant = isHConstant;
        this.k = k;
    }

    @Override
    public String toString() {
        if (isHConstant) return "Params: h = " + h + String.format(", kernel = %s, mse = %.10f", kernel, mse);
        else return "Params: h is not constant, " + String.format("k = %d, kernel = %s, mse = %.10f", k, kernel, mse);
    }
}
