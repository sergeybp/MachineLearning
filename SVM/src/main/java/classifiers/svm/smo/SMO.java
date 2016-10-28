package classifiers.svm.smo;

import classifiers.svm.params.SVMKernels;
import classifiers.svm.params.SVMParams;
import utils.Data;
import utils.DataInstance;
import utils.PointND;

import java.util.Random;

/**
 * Created by nikita on 28.10.16.
 */

/**
 * Algorithm was taken from http://cs229.stanford.edu/materials/smo.pdf
 */
public class SMO {

    public final static double EPSILON = 1E-3;

    public SMO() {
    }

    public class SMOSolution {
        public double[] alphas;
        public double b;

        public SMOSolution(int n) {
            this.alphas = new double[n];
            this.b = 0d;
        }

        public SMOSolution(double[] alphas, double b) {
            this.alphas = alphas;
            this.b = b;
        }
    }

    private double E(DataInstance point, Data points, double[] alphas, double b, SVMKernels kernel) {
        double f = 0d;
        for (int i = 0; i < points.size(); i++) {
            PointND x = points.get(i).point;
            f += alphas[i] * points.get(i).clazz.value() * kernel.get().apply(x, point.point);
        }
        return f + b - point.clazz.value();
    }

    public SMOSolution solve(Data train, SVMParams params) {
        int m = train.size();
        double c = params.c;
        double tol = params.tol;
        SMOSolution s = new SMOSolution(m);
        Random random = new Random();
        int passes = 0;
        while (passes < params.maxPasses) {
            int numChangedAlphas = 0;
            for (int i = 0; i < m; i++) {
                DataInstance x = train.get(i);
                double Ei = E(x, train, s.alphas, s.b, params.kernel);
                if ((x.clazz.value() * Ei < -tol && s.alphas[i] < c) || (x.clazz.value() * Ei > tol && s.alphas[i] > 0d)) {
                    int j = random.nextInt(m);
                    while (j == i) j = random.nextInt(m);
                    DataInstance y = train.get(j);
                    double Ej = E(y, train, s.alphas, s.b, params.kernel);
                    double oldAi = s.alphas[i];
                    double oldAj = s.alphas[j];
                    double L, H;
                    if (x.clazz.equals(y.clazz)) {
                        L = Math.max(0d, s.alphas[i] + s.alphas[j] - c);
                        H = Math.min(c, s.alphas[i] + s.alphas[j]);
                    } else {
                        L = Math.max(0d, s.alphas[j] - s.alphas[i]);
                        H = Math.min(c, c + s.alphas[j] - s.alphas[i]);
                    }
                    if (Math.abs(H - L) <= EPSILON) {
                        continue;
                    }
                    double eta = 2 * params.kernel.get().apply(x.point, y.point)
                            - params.kernel.get().apply(x.point, x.point)
                            - params.kernel.get().apply(y.point, y.point);

                    if (eta >= 0)
                        continue;

                    s.alphas[j] = s.alphas[j] - (y.clazz.value() * (Ei - Ej)) / eta;
                    if (s.alphas[j] > H) s.alphas[j] = H;
                    else if (s.alphas[j] < L) s.alphas[j] = L;

                    if (Math.abs(s.alphas[j] - oldAj) < EPSILON) continue;
                    s.alphas[i] = s.alphas[i] + x.clazz.value() * y.clazz.value() * (oldAj - s.alphas[j]);

                    double b1 = s.b - Ei - x.clazz.value() * (s.alphas[i] - oldAi) * params.kernel.get().apply(x.point, x.point)
                            - y.clazz.value() * (s.alphas[j] - oldAj) * params.kernel.get().apply(x.point, y.point);

                    double b2 = s.b - Ej - x.clazz.value() * (s.alphas[i] - oldAi) * params.kernel.get().apply(x.point, y.point)
                            - y.clazz.value() * (s.alphas[j] - oldAj) * params.kernel.get().apply(y.point, y.point);

                    if (s.alphas[i] > 0 && s.alphas[i] < c) s.b = b1;
                    else if (s.alphas[j] > 0 && s.alphas[j] < c) s.b = b2;
                    else s.b = (b1 + b2) / 2d;

                    numChangedAlphas++;
                }
            }
            if (numChangedAlphas == 0)
                passes++;
            else passes = 0;
        }
        return s;
    }

}
