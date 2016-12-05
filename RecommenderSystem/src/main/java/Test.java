import params.Params;
import utils.DataInstance;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Created by nikita on 25.11.16.
 */
public class Test {

    public static void main(String[] args) {
        System.out.println("Party!!!");

        int f = 15;
        double lambda1 = 0.005;
        double lambda2 = 0.005;
        double gamma = 0.005;

        Params newParams = new Params();
        newParams.f = f;
        newParams.lambda1 = lambda1;
        newParams.lambda2 = lambda2;
        newParams.mu = 3.6033;
        newParams.gamma = gamma;
        final int MAX_ITERATIONS = 25;
        final double EPSILON = 1E-4;

        double rmse = 0;
        double prevRMSE = 1;

        int i = 0;
        while (i < MAX_ITERATIONS && Math.abs(rmse - prevRMSE) > EPSILON) {
            int size = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(Paths.get(Main.class.getResource("train.csv").toURI()).toFile()))) {
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

            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
            rmse = Math.sqrt(rmse / size);
            System.out.println("#" + i + " RMSE: " + rmse + ", diff: " + (prevRMSE - rmse) + ", step: " + newParams.gamma);
           // newParams.gamma *= 0.7;
            i++;
        }

        try {
            PrintWriter out = new PrintWriter(new File("submission.csv"));
            out.println("Id,Prediction");
            try (BufferedReader br = new BufferedReader(
                    new FileReader(Paths.get(Main.class.getResource("test-ids.csv").toURI()).toFile()))) {
                String line = br.readLine();
                while((line = br.readLine()) != null) {
                    String[] splitted = line.split(",");
                    long testID = Long.parseLong(splitted[0].trim());
                    long userID = Long.parseLong(splitted[1].trim());
                    long itemID = Long.parseLong(splitted[2].trim());
                    out.println(testID + "," + SVDRecommendSystem.getRate(new DataInstance(userID, itemID, 0), newParams));
                }

            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
