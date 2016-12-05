package classifiers.svm.params;

import classifiers.params.Measures;
import classifiers.params.Params;

/**
 * Created by nikita on 17.09.16.
 */
public class SVMParams extends Params {
    public SVMKernels kernel;
    public double accuracy;
    public Measures measure;
    public double c;
    public double tol;
    public int maxPasses;

    public SVMParams() {
        kernel = SVMKernels.GAUSSIAN;
        measure = Measures.F1SCORE;
        accuracy = 0d;
        c = 0;
        maxPasses = 5;
        tol = 1E-3;
    }

    public SVMParams(SVMKernels kernel, double accuracy, Measures measure, double c, double tol, int maxPasses) {
        this.kernel = kernel;
        this.accuracy = accuracy;
        this.measure = measure;
        this.c = c;
        this.tol = tol;
        this.maxPasses = maxPasses;
    }

    @Override
    public String toString() {
        return String.format("SVM params: kernel = %s, c = %.5f, tol = %.5f, maxPasses = %d, accuracy measure = %s with value = %.5f", kernel, c, tol, maxPasses, measure, accuracy);
    }
}