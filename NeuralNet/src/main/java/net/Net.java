package net;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Created by nikita on 03.12.16.
 */
public class Net {
    public static final Logger logger = LoggerFactory.getLogger(Net.class);

    private static final int CV = 5;

    private static final double EPSILON = 1E-7;
    private static final int DELTA_SIZE = 5;
    private static final int MAX_ITERATIONS = 1000000;

    private static final double[] RATES = new double[]{7E-1};
    private static final double[] REGS = new double[]{0};
    private static final double[] IMPROVE = new double[]{0};

    public final int[] sizes;
    public Weight[] weights;
    public final Activation[] sigmas;

    public Net(int[] sizes, Activation[] sigmas) {
        this.sizes = sizes;
        this.sigmas = sigmas;
        logger.debug("Net was created with sizes: {}", Arrays.toString(sizes));
    }

    public void initWeights() {
        logger.debug("Initializing weights");
        weights = new Weight[sizes.length - 1];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = new Weight(sizes[i] + 1, sizes[i + 1] + 1);
        }
    }

    public void initWeights(Weight[] weights) {
        this.weights = weights;
    }

    public void initWeights(File[] files) {
        logger.debug("Initializing weights");
        weights = new Weight[sizes.length - 1];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = new Weight(sizes[i] + 1, sizes[i + 1] + 1, files[i]);
        }
    }


    public double calculateDelta(CircularFifoBuffer stab) {
        if (stab.size() < DELTA_SIZE) return Double.MAX_VALUE;
        return (Double) Collections.max(stab) - (Double) Collections.min(stab);
    }


    public void SGD(Data data, Data test, Params params) {
        logger.debug("Running SGD with {}", params.toString());

        Random random = new Random();
        int k = 0;
        double q = 0d;

        data.shuffle();
        CircularFifoBuffer buf = new CircularFifoBuffer(DELTA_SIZE);
        double delta;

        ArrayList<Integer> probabilities = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            probabilities.add(i);
        }
        Collections.shuffle(probabilities);


        do {
            k++;
            int i = probabilities.get(random.nextInt(probabilities.size()));
            if (k % 250000 == 0) {
                params.rate /= 2;
            }

            double qi = learnStep(data.get(i), params);
            q = q * (1d - params.lambda) + qi * params.lambda;

          /*  Label result = classify(data.get(i).feature);
            if (!result.equals(data.get(i).label)) {
                for (int t = 0; t < qi * 100; t++)
                    probabilities.add(i);
                Collections.shuffle(probabilities);
            }*/
            if (k % 3000 == 0) {
                double accuracy = test(test);
                logger.debug("Step {}, accuracy = {}, q = {}", k, accuracy, q);
                if (accuracy > 0.97) {
                    for (int w = 0; w < weights.length; w++) {
                        weights[w].writeFile("w" + w + ".txt");
                    }
                    break;
                }
            }
            buf.add(q);
            delta = calculateDelta(buf);
            //logger.debug("Step {}: Q = {}, delta = {}, index: {}", k, q, delta, i);
        } while (true);

    }

    private double run(Data data, Data test, int s, Params params) {
        initWeights();
        SGD(data, test, params);
        double result = test(test);
        logger.debug("Accuracy: {}", result);
        return result;



        /*CVDivider divider = new CVDivider(data, s);

        double accuracy = 0d;
        for (Division div: divider) {
            initWeights();
            SGD(div.train, div.test, params);
            double result = test(div.test);
            logger.debug("Accuracy: {}", result);
            accuracy += result;

        }
        return accuracy / divider.size();*/
    }

    public Params learn(Data data, Data test) {
        Params bestParams = new Params();
        double bestAccuracy = -1;

        for (double rate: RATES) {
            for (double reg: REGS) {
                for (double improve: IMPROVE) {
                    Params temp = new Params(rate, reg, 1d / data.size(), improve);
                    double accuracy = run(data, test, CV, temp);
                    if (bestAccuracy < accuracy) {
                        bestAccuracy = accuracy;
                        bestParams = new Params(temp);
                    }
                }
            }
        }
        return bestParams;
    }

    public double test(Data testData) {
        int failed = 0;
        for (int i = 0; i < testData.size(); i++) {
            Feature f = testData.get(i).feature;
            Label result = classify(f);

            if (!result.equals(testData.get(i).label)) {
                failed++;
            }
        }
        return (1d - (double) failed / testData.size());
    }

    public double[] evaluateLayer(double[] input, Weight w, int outputSize, Activation sigma) {
        double[] ui = new double[outputSize + 1];
        ui[0] = -1d;
        for (int h = 1; h <= outputSize; h++) {
            double sum = 0d;
            for (int j = 0; j < input.length; j++) {
                sum += w.get(j, h) * input[j];
            }
            ui[h] = sigma.get().apply(sum);
        }
        return ui;
    }

    public double learnStep(DataInstance data, Params params) {
        // forward step
        Feature xit = new Feature(data.feature);
        xit.addHead(-1d);

        double[][] ui = new double[sizes.length][];
        ui[0] = xit.x;
        for (int i = 1; i < sizes.length; i++) {
            ui[i] = evaluateLayer(ui[i - 1], weights[i - 1], sizes[i], sigmas[i]);
        }

        double[][] errors = new double[sizes.length][];

        double qi = 0d;
        errors[errors.length - 1] = new double[ui[ui.length - 1].length];
        for (int m = 0; m < ui[ui.length - 1].length; m++) {
            errors[errors.length - 1][m] = ui[ui.length - 1][m] - (data.label.value == (m - 1) ? 1d : 0d);
            qi += errors[errors.length - 1][m] * errors[errors.length - 1][m];
        }

        for (int i = ui.length - 2; i >= 0; i--) {
            double[] sm = new double[sizes[i + 1] + 1];
            for (int m = 0; m < sizes[i + 1] + 1; m++) {
                double s = 0d;
                for (int hs = 0; hs < ui[i].length; hs++) {
                    s += weights[i].get(hs, m) * ui[i][hs];
                }
                sm[m] = sigmas[i + 1].getDiff().apply(s);
            }
            errors[i] = new double[ui[i].length + 1];
            for (int h = 0; h < ui[i].length; h++) {
                double sum = 0d;
                for (int m = 0; m < sizes[i + 1] + 1; m++) {
                    sum += errors[i + 1][m] * sm[m] * weights[i].get(h, m);
                }
                errors[i][h] = sum;
            }
        }

        for (int i = ui.length - 2; i >= 0; i--) {
            double[] sm = new double[sizes[i + 1] + 1];
            for (int m = 0; m < sizes[i + 1] + 1; m++) {
                double s = 0d;
                for (int hs = 0; hs < ui[i].length; hs++) {
                    s += weights[i].get(hs, m) * ui[i][hs];
                }
                sm[m] = sigmas[i + 1].getDiff().apply(s);
            }
            for (int h = 0; h < ui[i].length; h++) {
                for (int m = 0; m < sizes[i + 1] + 1; m++) {
                    weights[i].set(h, m, weights[i].get(h, m) * (1 - params.rate * params.reg) - params.rate * errors[i + 1][m] * sm[m] * ui[i][h]);
                }
            }

        }
        return qi;
    }

    public Label classify(Feature x) {

        Feature xit = new Feature(x);
        xit.addHead(-1d);

        double[][] ui = new double[sizes.length][];
        ui[0] = xit.x;
        for (int i = 1; i < sizes.length; i++) {
            ui[i] = evaluateLayer(ui[i - 1], weights[i - 1], sizes[i], sigmas[i]);
        }

        return new Label(Utils.argMax(ui[sizes.length - 1]) - 1);


    }


}