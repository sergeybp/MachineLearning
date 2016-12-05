package net;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by nikita on 03.12.16.
 */
public class Net {

    public static final Logger logger = LoggerFactory.getLogger(Net.class);
    public final int N, H, M;
    public Weight w1, w2;
    public final Activation sigma;
    private static final double EPSILON = 1E-6;
    private static final int DELTA_SIZE = 5;

    public Net(int N, int H, int M, Activation activation) {
        this.N = N;
        this.H = H;
        this.M = M;
        this.sigma = activation;
        logger.debug("Net was created with: N = {}, H = {}, M = {}", N, H, M);
    }

    public void initWeights() {
        logger.debug("Initializing weights");
        initWeights(new Weight(N + 1, H + 1), new Weight(H + 1, M));
    }

    public void initWeights(Weight w1, Weight w2) {
        this.w1 = w1;
        this.w2 = w2;
    }

    public void initWeights(File file1, File file2) {
        w1 = new Weight(N + 1, H + 1, file1);
        w2 = new Weight(H + 1, M, file2);
    }


    public double calculateDelta(CircularFifoBuffer stab) {
        if (stab.size() < DELTA_SIZE) return Double.MAX_VALUE;
        return (Double) Collections.max(stab) - (Double) Collections.min(stab);
    }

    private class Data {
        public Feature feature;
        public Label label;
        public double probability;

        public Data(Feature feature, Label label, double probability) {
            this.feature = feature;
            this.label = label;
            this.probability = probability;
        }
    }

    public void learn(ArrayList<Feature> x, ArrayList<Label> y, double step, double lambda, double reg) {
        logger.debug("Start learning with step: {}", step);
        Random random = new Random();
        int k = 0;
        double q = 0d;
        ArrayList<Data> data = new ArrayList<>();
        ArrayList<Integer> probabilities = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            data.add(new Data(x.get(i), y.get(i), 1d));
            probabilities.add(i);
        }
        Collections.shuffle(data);
        Collections.shuffle(probabilities);

        CircularFifoBuffer buf = new CircularFifoBuffer(DELTA_SIZE);
        double delta;
        do {
            k++;

            int i = probabilities.get(random.nextInt(probabilities.size()));

            double qi = learnStep(data.get(i).feature, data.get(i).label, step, reg);
            q = q * (1d - lambda) + qi * lambda;

            Label result = classify(data.get(i).feature);
            if (!result.equals(data.get(i).label)) {
                data.get(i).probability ++;
                probabilities.add(i);
                Collections.shuffle(probabilities);
            }

            buf.add(q);
            delta = calculateDelta(buf);
            logger.debug("Step {}: Q = {}, delta = {}, index: {}", k, q, delta, i);
        } while (delta > EPSILON);

    }

    public double learnStep(Feature xi, Label yi, double step, double reg) {
        // forward step
        Feature xit = new Feature(xi);
        xit.addHead(-1);
        double[] ui = new double[H + 1];
        ui[0] = -1d;
        for (int h = 1; h <= H; h++) {
            double sum = 0d;
            for (int j = 0; j < xit.size(); j++) {
                sum += w1.get(j, h) * xit.get(j);
            }
            ui[h] = sigma.get().apply(sum);
        }
        double[] ai = new double[M];
        for (int m = 0; m < M; m++) {
            double sum = 0d;
            for (int h = 0; h <= H; h++) {
                sum += w2.get(h, m) * ui[h];
            }
            ai[m] = sigma.get().apply(sum);
        }
        double[] eim = new double[M];
        for (int m = 0; m < M; m++) {
            eim[m] = ai[m] - (yi.label == m ? 1d : 0d);
        }
        double qi = 0d;
        for (int m = 0; m < M; m++) {
            qi += eim[m] * eim[m];
        }

        // backward step

        double[] eih = new double[H + 1];
        for (int h = 1; h <= H; h++) {
            double sum = 0d;

            for (int m = 0; m < M; m++) {
                double s = 0d;
                for (int hs = 0; hs <= H; hs++) {
                    s += w2.get(hs, m) * ui[hs];
                }
                s = sigma.getDiff().apply(s);
                sum += eim[m] * s * w2.get(h, m);
            }
            eih[h] = sum;
        }
        // gradient step

        for (int h = 0; h <= H; h++) {
            for (int m = 0; m < M; m++) {
                double s = 0d;
                for (int hs = 0; hs <= H; hs++) {
                    s += w2.get(hs, m) * ui[hs];
                }
                s = sigma.getDiff().apply(s);
                w2.set(h, m, w2.get(h, m) * (1 - step * reg) - step * eim[m] * s * ui[h]);
            }
        }

        for (int j = 0; j < xit.size(); j++) {
            for (int h = 1; h <= H; h++) {
                double s = 0d;
                for (int hs = 0; hs <= H; hs++) {
                    s += w1.get(j, hs) * xit.get(j);
                }
                s = sigma.getDiff().apply(s);
                w1.set(j, h, w1.get(j, h) * (1 - step * reg) - step * eih[h] * s * xit.get(j));
            }
        }
        return qi;
    }

    public Label classify(Feature x) {

        Feature xt = new Feature(x);
        xt.addHead(-1);
        double[] ui = new double[H + 1];
        ui[0] = -1;
        for (int h = 1; h <= H; h++) {
            double sum = 0d;
            for (int j = 0; j < xt.size(); j++) {
                sum += w1.get(j, h) * xt.get(j);
            }
            ui[h] = sigma.get().apply(sum);
        }
        double[] a = new double[M];
        for (int m = 0; m < M; m++) {
            double sum = 0d;
            for (int h = 0; h <= H; h++) {
                sum += w2.get(h, m) * ui[h];
            }
            a[m] = sigma.get().apply(sum);
        }
        int maxM = 0;
        double maxV = Double.MIN_VALUE;
        for (int m = 0; m < M; m++) {
            if (a[m] > maxV) {
                maxV = a[m];
                maxM = m;
            }
        }
        return new Label(maxM);


    }


}
