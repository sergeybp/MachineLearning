import params.Params;
import utils.Data;
import utils.DataInstance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 * Created by nikita on 24.11.16.
 */
public class SVDRecommendSystem {

    private File file;
    private File validation;
    private boolean VALIDATION;

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

        double rmse = 0;
        double prevRMSE = 1;

        Params newParams = new Params();
        newParams.f = params.f;
        newParams.lambda1 = params.lambda1;
        newParams.lambda2 = params.lambda2;
        newParams.gamma = params.gamma;
        newParams.mu = params.mu;

        int i = 0;
        while (i < MAX_ITERATIONS && Math.abs(rmse - prevRMSE) > EPSILON) {
            int size = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                prevRMSE = rmse;
                rmse = 0;
                while ((line = br.readLine()) != null) {
                    String[] splitted = line.split(",");
                    DataInstance instance;
                    {
                        long userID = Long.parseLong(splitted[0].trim());
                        long itemID = Long.parseLong(splitted[1].trim());
                        int rate = Integer.parseInt(splitted[2].trim());
                        newParams.pu.putIfAbsent(userID, SVDRecommendSystem.getRandomArray(newParams.f));
                        newParams.qi.putIfAbsent(itemID, SVDRecommendSystem.getRandomArray(newParams.f));
                        newParams.bu.putIfAbsent(userID, 0d);
                        newParams.bi.putIfAbsent(itemID, 0d);
                        instance = new DataInstance(userID, itemID, rate);
                    }
                    size++;

                    long item = instance.itemID, user = instance.userID;
                    int rate = instance.rate;
                    double cbu = newParams.bu.get(user), cbi = newParams.bi.get(item);
                    double[] cqi = newParams.qi.get(item), cpu = newParams.pu.get(user);
                    double predictedRate = newParams.mu + cbi + cbu + SVDRecommendSystem.dotProduct(cqi, cpu);
                    double error = rate - predictedRate;
                    rmse += error * error;
                    newParams.bu.put(user, cbu + newParams.gamma * (error - newParams.lambda1 * cbu));
                    newParams.bi.put(item, cbi + newParams.gamma * (error - newParams.lambda1 * cbi));

                    for (int k = 0; k < newParams.f; k++) {
                        double qi = cqi[k], pu = cpu[k];
                        cqi[k] = qi + newParams.gamma * (error * pu - newParams.lambda2 * qi);
                        cpu[k] = pu + newParams.gamma * (error * qi - newParams.lambda2 * pu);
                    }
                }

            } catch (IOException ex) {
                Main.logger.error("Check file: {}", file);
                ex.printStackTrace();
            }
            rmse = Math.sqrt(rmse / size);
            Main.logger.debug("Iteration {}, RMSE: {}, diff: {}, step: {}", i, rmse, prevRMSE - rmse, newParams.gamma);
            // newParams.gamma *= 0.7;
            i++;
        }
        newParams.rmse = rmse;
        return newParams;

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

    public static double RMSE(Data test, Params params) {
        double result = 0d;
        for (DataInstance instance: test) {
            double e = getRate(instance, params) - instance.rate;
            result += e * e;
        }
        return Math.sqrt(result / test.size());
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
        return result.rmse;
        
    }

    public Params learn() {
        Main.logger.debug("Start learning...");

        Params params = new Params();
        params.rmse = Double.MAX_VALUE;
        params.mu = 3.6033;

        for (double lambda1 = MIN_LAMBDA; lambda1 <= MAX_LAMBDA; lambda1 += STEP_LAMBDA) {
            for (double lambda2 = MIN_LAMBDA; lambda2 <= MIN_LAMBDA; lambda2 += STEP_LAMBDA) {
                for (int f = MIN_F; f <= MAX_F; f *= STEP_F) {
                    Params temp = new Params();
                    temp.lambda1 = lambda1;
                    temp.lambda2 = lambda2;
                    temp.f = f;
                    temp.gamma = MIN_GAMMA;
                    double error = run(temp);
                    if (params.rmse > error) {
                        params.lambda1 = temp.lambda1;
                        params.lambda2 = temp.lambda2;
                        params.f = temp.f;
                        params.gamma = MIN_GAMMA;
                        params.rmse = error;
                    }
                }
            }
        }

        return solve(params);
    }
}
