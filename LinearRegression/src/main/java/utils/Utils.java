package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by nikita on 08.10.16.
 */
public class Utils {

    public static Double random(double rangeMin, double rangeMax) {
        Random r = new Random();
        return  rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }

    public static Double standardDeviation(Data data, Vector w) {
        double sum = 0d;
        for (DataInstance flat : data) {
            sum += Math.pow(w.get(0) + w.get(1) * flat.area + w.get(2) * flat.rooms - flat.prices, 2);
        }
        return Math.sqrt(sum / data.size());

    }

    public static Data getDataFromFile(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Data data = new Data();
        FastScanner in = new FastScanner(is);
        while (in.hasNext()) {
            String line = in.nextLine();
            String[] splitted = line.split(",");
            int area = Integer.parseInt(splitted[0]);
            int rooms = Integer.parseInt(splitted[1]);
            int price = Integer.parseInt(splitted[2]);
            data.add(area, rooms, price);
        }
        return data;
    }

}
