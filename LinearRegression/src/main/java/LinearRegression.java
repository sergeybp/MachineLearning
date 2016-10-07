import javafx.util.Pair;
import optimizers.Optimizer;
import optimizers.Optimizers;
import params.Params;
import utils.Data;
import utils.Utils;
import utils.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static optimizers.Optimizers.*;

/**
 * Created by nikita on 13.09.16.
 */
public class LinearRegression {
    public final static int CV_PARAM = 5;

    private final static double EPSILON = 1E-5;
    private final static double MAX_EPSILON = 1E-2;
    private final static double MIN_EPSILON = 1E-8;
    private final static double EPSILON_STEP = 10;

    private final static double STEP = 1E-10;
    private final static double MIN_STEP = 1E-12;
    private final static double MAX_STEP = 1;
    private final static double STEP_STEP = 10;

    private final static int POPULATION = 80;
    private final static int MIN_POPULATION = 80;
    private final static int MAX_POPULATION = 120;
    private final static int POPULATION_STEP = 10;

    private final static int MAX_ITERATIONS = 1000;

    private Data data;

    public LinearRegression(File file) {
        this.data = Utils.getDataFromResource(file);
    }

    public LinearRegression(Data data) {
        this.data = data;
    }

    private double run(int s, Params params) {
        Pair<ArrayList<ArrayList<Integer>>, ArrayList<ArrayList<Integer>>> cv = Utils.crossValidation(data.size(), s);
        ArrayList<ArrayList<Integer>> trainCV = cv.getKey();
        ArrayList<ArrayList<Integer>> testCV = cv.getValue();

        double deviation = 0d;
        for (int i = 0; i < trainCV.size(); i++) {
            Data train = new Data(trainCV.get(i).stream().map(j -> data.get(j)).collect(Collectors.toList()));
            Data test = new Data(testCV.get(i).stream().map(j -> data.get(j)).collect(Collectors.toList()));

            Vector w = solve(train, params);

            deviation += Utils.standardDeviation(test, w);
        }
        return deviation / trainCV.size();
    }

    public static Vector solve(Data data, Params params) {
        Optimizer optimizer = params.optimizers.get(data, params, MAX_ITERATIONS);
        assert optimizer != null;
        return optimizer.optimize();
    }

    public static Params learn(Data data) {
        Params params = new Params(GRADIENT_DESCENT, EPSILON, STEP, POPULATION, Double.MAX_VALUE);
        for (Optimizers optimizers : Optimizers.values())
            switch (optimizers) {
                case GRADIENT_DESCENT: {
                    for (double eps = MIN_EPSILON; eps <= MAX_EPSILON; eps *= EPSILON_STEP)
                        for (double step = MIN_STEP; step <= MAX_STEP; step *= STEP_STEP) {
                            double deviation = new LinearRegression(data).run(CV_PARAM,
                                    new Params(optimizers, eps, step, POPULATION));
                            if (deviation < params.deviation)
                                params = new Params(optimizers, eps, step, POPULATION, deviation);
                        }
                }
                case GENETIC: {
                    for (int p = MIN_POPULATION; p <= MAX_POPULATION; p += POPULATION_STEP) {
                        double deviation = new LinearRegression(data).run(CV_PARAM,
                                new Params(optimizers, EPSILON, STEP, p));
                        if (deviation < params.deviation)
                            params = new Params(optimizers, EPSILON, STEP, POPULATION, deviation);
                    }
                }
            }
        return params;
    }

}
