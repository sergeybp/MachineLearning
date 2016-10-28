package classifiers.svm;

import classifiers.params.Classes;
import classifiers.params.Measures;
import classifiers.svm.params.SVMKernels;
import classifiers.svm.params.SVMParams;
import classifiers.svm.smo.SMO;
import dividers.CVDivider;
import dividers.Division;
import utils.Data;
import utils.DataInstance;
import utils.PointND;
import utils.Utils;

import java.io.File;

/**
 * Created by nikita on 13.09.16.
 */
public class SVM {
    private Data data;
    public final static int CV_PARAM = 5;

    public final static double MIN_C = 60;
    public final static double MAX_C = 60;
    public final static double STEP_OF_C = 10;

    public final static double MIN_TOL = 1E-3;
    public final static double MAX_TOL = 1E-3;
    public final static double STEP_OF_TOL = 10;

    public final static int MIN_PASSES = 5;
    public final static int MAX_PASSES = 5;
    public final static int STEP_OF_PASSES = 2;

    public SVM(File file) {
        this.data = Utils.getDataFromFile(file);
    }

    public SVM(Data data) {
        this.data = data;
    }

    public static Data evaluate(Data train, Data test, SVMParams params) {
        Data answer = new Data();
        SMO smo = new SMO();
        SMO.SMOSolution solution = smo.solve(train, params);
        for (DataInstance instance: test) {
            double f = 0d;
            for (int i = 0; i < train.size(); i++) {
                PointND x = train.get(i).point;
                f += solution.alphas[i] * train.get(i).clazz.value() * params.kernel.get().apply(x, instance.point);
            }
            f += solution.b;
            answer.add(instance.point, f >= 0d ? Classes.SECOND : Classes.FIRST);
        }
        return answer;
    }

    private double run(int s, SVMParams SVMParams) {
        CVDivider divider = new CVDivider(data, s);

        double accuracy = 0d;
        for (Division div : divider) {
            Data answer = evaluate(div.train, div.test, SVMParams);
            accuracy += SVMParams.measure.get().apply(div.test, answer);
        }
        return accuracy / divider.size();
    }

    public static SVMParams learn(Data data, Measures measure) {
        SVMParams SVMParams = new SVMParams();

        for (SVMKernels kernel: SVMKernels.values()) {
            for (double c = MIN_C; c <= MAX_C; c += STEP_OF_C) {
                for (double tol = MIN_TOL; tol <= MAX_TOL; tol *= STEP_OF_TOL) {
                    for (int passes = MIN_PASSES; passes <= MAX_PASSES; passes += STEP_OF_PASSES) {

                        double result = new SVM(data).run(CV_PARAM,
                                new SVMParams(kernel, 0d, measure, c, tol, passes));

                        if (SVMParams.accuracy < result)
                            SVMParams = new SVMParams(kernel, result, measure, c, tol, passes);
                    }
                }
            }
        }
        SVMParams.measure = measure;
        return SVMParams;
    }

}
