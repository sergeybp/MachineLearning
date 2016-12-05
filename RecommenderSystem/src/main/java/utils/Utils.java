package utils;


import java.io.*;
import java.util.Random;

/**
 * Created by nikita on 08.10.16.
 */
public class Utils {

    public static Double random(double rangeMin, double rangeMax) {
        Random r = new Random();
        return  rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }

    public static Data getDataFromFile(File file) {
        Data data = new Data();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                String[] splitted = line.split(",");
                long userID = Long.parseLong(splitted[0].trim());
                long itemID = Long.parseLong(splitted[1].trim());
                int rate = Integer.parseInt(splitted[2].trim());
                data.add(new DataInstance(userID, itemID, rate));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(file.getName() + " has been read");
        return data;
    }

    public static int findMaxIndex(double[] data) {
        double max = Double.MIN_VALUE;
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > max) {
                max = data[i];
                result = i;
            }
        }
        return result;
    }

}
