import params.Kernels;
import params.RobustRegressionParams;
import utils.Data;
import utils.DataInstance;
import utils.Point;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiFunction;

/**
 * Created by nikita on 05.11.16.
 */
public class RobustNonparametricRegression {
    public final static int MIN_K = 5;
    public final static int MAX_K = 20;
    public final static int STEP_K = 1;

    private Data data;

    public RobustNonparametricRegression(File file) {
        this.data = Utils.getDataFromFile(file);
    }

    public RobustNonparametricRegression(Data data) {
        this.data = data;
    }

    public static double[] lowess(Data data, RobustRegressionParams params) {
        int l = data.size();
        double[] gammas = new double[l];
        double[] prevGammas;

        final double epsilon = 1E-5;

        BiFunction<double[], double[], Boolean> stop = (a, b) -> {
            for (int i = 0; i < a.length; i++) {
                if (Math.abs(a[i] - b[i]) > epsilon) {
                    return false;
                }
            }
            return true;
        };

        for (int i = 0; i < l; i++) {
            gammas[i] = 1d;
        }

        do {
            prevGammas = Arrays.copyOf(gammas, gammas.length);
            double[] as = new double[l];
            for (int i = 0; i < l; i++) {
                Point pi = data.instances.get(i).point;
                ArrayList<Double> distances = new ArrayList<>();
                for (int j = 0; j < l; j++) {
                    if (j != i)
                        distances.add(Math.abs(pi.x - data.instances.get(j).point.x));
                }
                Collections.sort(distances, Double::compareTo);
                double h = distances.get(params.k + 1);
                double sum1 = 0d;
                double sum2 = 0d;
                for (int j = 0; j < l; j++) {
                    if (j != i) {
                        Point pj = data.instances.get(j).point;
                        sum1 += pj.y * gammas[j] * params.kernel.get().apply(Math.abs(pj.x - pi.x) / h);
                        sum2 += gammas[j] * params.kernel.get().apply(Math.abs(pj.x - pi.x) / h);
                    }
                }
                as[i] = Math.abs(pi.y - (sum1 / sum2));
            }
            double[] var = Arrays.copyOf(as, as.length);
            double med;
            if (l % 2 == 0) {
                med = (var[(l - 1) / 2] + var[(l - 1) / 2 + 1]) / 2d;
            } else med = var[(l - 1) / 2];
            Arrays.sort(var);
            for (int i = 0; i < l; i++) {
                gammas[i] = Kernels.QUARTIC.get().apply(Math.abs(as[i]) / (6 * med));
            }

        } while (stop.apply(gammas, prevGammas));
        return gammas;
    }


    public static DataInstance evaluate(DataInstance point, RobustRegressionParams params) {
        double sum1 = 0d;
        double sum2 = 0d;
        ArrayList<Double> distances = new ArrayList<>();
        for (DataInstance tr : params.train)
            distances.add(Math.abs(point.point.x - tr.point.x));
        Collections.sort(distances, Double::compareTo);
        double h = distances.get(params.k + 1);

        for (int i = 0; i < params.train.size(); i++) {
            DataInstance l = params.train.get(i);
            sum1 += l.point.y * params.gamma[i] * params.kernel.get().apply(Math.abs(point.point.x - l.point.x) / h);
            sum2 += params.gamma[i] * params.kernel.get().apply(Math.abs(point.point.x - l.point.x) / h);
        }
        return new DataInstance(new Point(point.point.x, sum1 / sum2));
    }

    public static RobustRegressionParams learn(Data data) {
        RobustRegressionParams robustRegressionParams = new RobustRegressionParams();
        robustRegressionParams.mse = Double.MAX_VALUE;
        Collections.shuffle(data.instances);

        for (Kernels kernel : Kernels.values()) {
            for (int k = MIN_K; k <= MAX_K; k += STEP_K) {
                double mse = 0d;
                double[] gammas = lowess(data, new RobustRegressionParams(kernel, data, k, null, mse));
                for (int i = 0; i < data.size(); i++) {
                    DataInstance xi = data.get(i);
                    Data train = new Data(data.instances);
                    train.instances.remove(i);
                    DataInstance a = evaluate(xi, new RobustRegressionParams(kernel, train, k, gammas, mse));
                    mse += Math.pow(a.point.y - xi.point.y, 2d);
                }
                mse /= data.size();
                if (!Double.isNaN(mse) && mse < robustRegressionParams.mse) {
                    robustRegressionParams = new RobustRegressionParams(kernel, data, k, gammas, mse);
                }
            }
        }
        return robustRegressionParams;
    }
}
