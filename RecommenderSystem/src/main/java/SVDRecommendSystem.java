import params.Params;
import utils.DataInstance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.DoubleStream;

/**
 * Created by nikita on 24.11.16.
 */
public class SVDRecommendSystem {

    private File file;
    private File validation;
    private boolean VALIDATION;

    public static final double MU = 3.6033;

    public static final int MAX_ITERATIONS = 20;
    public static final double EPSILON = 1E-4;

    public static final double MIN_LAMBDA = 0.002;
    public static final double MAX_LAMBDA = 0.008;
    public static final double STEP_LAMBDA = 0.001;

    public static final int MIN_F = 10;
    public static final int MAX_F = 15;
    public static final int STEP_F = 5;

    public static final double MIN_GAMMA = 0.005;

    public SVDRecommendSystem(File file) {
        this.file = file;
        this.VALIDATION = false;
    }

    public void setValidation(File validation) {
        this.validation = validation;
        this.VALIDATION = true;
    }

    public static double[] getRandomArray(int f) {
        Random random = new Random();
        double[] a = new double[f];
        double min = 0d;
        double max = 1d / (double) f;

        for (int i = 0; i < f; i++) {
            a[i] = min + (max - min) * random.nextGaussian();
        }
        return a;
    }

    public static double dotProduct(double[] qi, double[] pu) {
        double res = 0;
        for (int i = 0; i < qi.length; i++) {
            res += qi[i] * pu[i];
        }
        return res;
    }

    public Params solve(Params params) {
        Main.logger.debug("Solving with params: {}", params.toString());

        double error = 0;
        double prevError = 1;

        Params newParams = new Params(params.lambda, params.f, params.gamma, params.mu);

        int i = 0;
        while (i < MAX_ITERATIONS && Math.abs(error - prevError) > EPSILON) {
            int size = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] splitted = line.split(",");

                    long userID = Long.parseLong(splitted[0].trim());
                    long itemID = Long.parseLong(splitted[1].trim());
                    int rate = Integer.parseInt(splitted[2].trim());

                    newParams.pu.putIfAbsent(userID, SVDRecommendSystem.getRandomArray(newParams.f));
                    newParams.qi.putIfAbsent(itemID, SVDRecommendSystem.getRandomArray(newParams.f));
                    newParams.bu.putIfAbsent(userID, 0d);
                    newParams.bi.putIfAbsent(itemID, 0d);
                    newParams.ratings.putIfAbsent(userID, new HashMap<>());
                    newParams.ratings.get(userID).putIfAbsent(itemID, rate);

                    size++;

                    double cbu = newParams.bu.get(userID), cbi = newParams.bi.get(itemID);
                    double[] cqi = newParams.qi.get(itemID), cpu = newParams.pu.get(userID);

                    double predictedRate = newParams.mu + cbi + cbu + dotProduct(cqi, cpu);
                    double e = rate - predictedRate;

                    newParams.bu.put(userID, cbu + newParams.gamma * (e - newParams.lambda * cbu));
                    newParams.bi.put(itemID, cbi + newParams.gamma * (e - newParams.lambda * cbi));

                    for (int k = 0; k < newParams.f; k++) {
                        double qi = cqi[k], pu = cpu[k];
                        cqi[k] = qi + newParams.gamma * (e * pu - newParams.lambda * qi);
                        cpu[k] = pu + newParams.gamma * (e * qi - newParams.lambda * pu);
                    }
                }
                newParams.gamma *= 0.9;

                prevError = error;
                error = countError(newParams);

            } catch (IOException ex) {
                Main.logger.error("Check file: {}", file);
                ex.printStackTrace();
            }
            Main.logger.debug("Iteration {}, Error: {}, diff: {}, step: {}", i, error, prevError - error, newParams.gamma);
            i++;
        }
        newParams.error = error;
        return newParams;

    }

    private double countError(Params params) {
        Main.logger.debug("Counting error...");
        double error = 0d;
        for (Long userId : params.ratings.keySet()) {
            for (Long itemId : params.ratings.get(userId).keySet()) {
                DataInstance instance = new DataInstance(userId, itemId, params.ratings.get(userId).get(itemId));
                double predictedRate = getRate(instance, params);
                double diff = instance.rate - predictedRate;
                error += diff * diff;
                error += params.lambda * (normSqr(params.pu.get(userId)) + normSqr(params.qi.get(itemId)) +
                        Math.pow(params.bu.get(userId), 2) + Math.pow(params.bi.get(itemId), 2));
            }
        }
        return error;
    }

    private double normSqr(double[] x) {
        return DoubleStream.of(x).map(v -> v * v).sum();
    }

    public static int getRate(DataInstance instance, Params params) {
        double result = params.mu + params.bu.getOrDefault(instance.userID, 0d) +
                params.bi.getOrDefault(instance.itemID, 0d) +
                dotProduct(
                        params.pu.getOrDefault(instance.userID, new double[params.f]),
                        params.qi.getOrDefault(instance.itemID, new double[params.f]));
        int rate = (int) Math.round(result);
        if (rate < 1) return 1;
        if (rate > 5) return 5;
        return rate;
    }

    private double run(Params params) {
        Params result = solve(params);
        if (VALIDATION) {
            double rmse = 0d;
            int size = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(validation))) {
                String line = br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] splitted = line.split(",");
                    long userID = Long.parseLong(splitted[0].trim());
                    long itemID = Long.parseLong(splitted[1].trim());
                    int rate = Integer.parseInt(splitted[2].trim());
                    size++;
                    DataInstance instance = new DataInstance(userID, itemID, rate);
                    double e = getRate(instance, params) - instance.rate;
                    rmse += e * e;
                }

            } catch (IOException e) {
                Main.logger.debug("Validation file not found: {}", validation);
            }
            return Math.sqrt(rmse / size);
        }
        return result.error;

    }

    public Params learn() {
        Main.logger.debug("Start learning...");

        Params params = new Params();
        params.error = Double.MAX_VALUE;
        params.mu = MU;

        for (double lambda = MIN_LAMBDA; lambda <= MAX_LAMBDA; lambda += STEP_LAMBDA) {
            for (int f = MIN_F; f <= MAX_F; f *= STEP_F) {
                Params temp = new Params(lambda, f, MIN_GAMMA, MU);
                double error = run(temp);
                if (params.error > error) {
                    params = new Params(lambda, f, MIN_GAMMA, MU);
                    params.error = error;
                }
            }
        }

        return solve(params);
    }
}
