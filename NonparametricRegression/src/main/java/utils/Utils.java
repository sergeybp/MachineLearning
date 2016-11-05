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

    public static Double MSE(Data real, Data result) {
        double sum = 0d;
        for (int i = 0; i < real.size(); i++) {
            sum += Math.pow(real.get(i).point.y - result.get(i).point.y, 2d);
        }
        return sum / real.size();
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
            double x = Double.parseDouble(splitted[0]);
            double y = Double.parseDouble(splitted[1]);
            data.add(new Point(x, y));
        }
        return data;
    }

}
