package utils;

import classifiers.params.Classes;

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
            double x = Double.parseDouble(splitted[0].trim());
            double y = Double.parseDouble(splitted[1].trim());
            int clazz = Integer.parseInt(splitted[2].trim());
            data.add(new Point(x, y), clazz == 0 ? Classes.FIRST : Classes.SECOND);
        }
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
