import params.Kernels;
import params.KernelRegressionParams;
import utils.Data;
import utils.DataInstance;
import utils.Point;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nikita on 05.11.16.
 */
public class KernelNonparametricRegression {
    public final static double MIN_H = 1E-9;
    public final static double MAX_H = 1E9;
    public final static double STEP_H = 10;

    public final static int MIN_K = 5;
    public final static int MAX_K = 20;
    public final static int STEP_K = 1;

    private Data data;

    public KernelNonparametricRegression(File file) {
        this.data = Utils.getDataFromFile(file);
    }

    public KernelNonparametricRegression(Data data) {
        this.data = data;
    }

    public static DataInstance evaluate(DataInstance point, KernelRegressionParams kernelRegressionParams) {
        double sum1 = 0d;
        double sum2 = 0d;
        double h = kernelRegressionParams.h;
        if (!kernelRegressionParams.isHConstant) {
            ArrayList<Double> distances = new ArrayList<>();
            for (DataInstance tr : kernelRegressionParams.train)
                distances.add(Math.abs(point.point.x - tr.point.x));
            Collections.sort(distances, Double::compareTo);
            h = distances.get(kernelRegressionParams.k + 1);
        }

        for (DataInstance l : kernelRegressionParams.train) {
            sum1 += l.point.y * kernelRegressionParams.kernel.get().apply(Math.abs(point.point.x - l.point.x) / h);
            sum2 += kernelRegressionParams.kernel.get().apply(Math.abs(point.point.x - l.point.x) / h);
        }
        return new DataInstance(new Point(point.point.x, sum1 / sum2));
    }

    public static KernelRegressionParams learn(Data data) {
        KernelRegressionParams kernelRegressionParams = new KernelRegressionParams(MIN_H, Kernels.GAUSSIAN, Double.MAX_VALUE);
        Collections.shuffle(data.instances);

        for (Kernels kernel : Kernels.values()) {
            for (double h = MIN_H; h <= MAX_H; h *= STEP_H) {
                double mse = 0d;
                for (int i = 0; i < data.size(); i++) {
                    DataInstance xi = data.get(i);
                    Data train = new Data(data.instances);
                    train.instances.remove(i);
                    DataInstance a = evaluate(xi, new KernelRegressionParams(h, kernel, train, true, MIN_K, 0d));
                    mse += Math.pow(a.point.y - xi.point.y, 2d);
                }
                mse /= data.size();
                if (!Double.isNaN(mse) && mse < kernelRegressionParams.mse) {
                    kernelRegressionParams = new KernelRegressionParams(h, kernel, data, true, MIN_K, mse);
                }
            }
            for (int k = MIN_K; k <= MAX_K; k += STEP_K) {
                double mse = 0d;
                for (int i = 0; i < data.size(); i++) {
                    DataInstance xi = data.get(i);
                    Data train = new Data(data.instances);
                    train.instances.remove(i);
                    DataInstance a = evaluate(xi, new KernelRegressionParams(MIN_H, kernel, train, false, k, 0d));
                    mse += Math.pow(a.point.y - xi.point.y, 2d);
                }
                mse /= data.size();
                if (!Double.isNaN(mse) && mse < kernelRegressionParams.mse) {
                    kernelRegressionParams = new KernelRegressionParams(MIN_H, kernel, data, false, k, mse);
                }
            }
        }
        return kernelRegressionParams;
    }
}
