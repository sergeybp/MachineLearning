package utils;

import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            data.add(new Point(x, y), clazz);
        }
        return data;
    }

    public static Pair<ArrayList<ArrayList<Integer>>, ArrayList<ArrayList<Integer>>> crossValidation(int l, int s) {
        int count = (int) Math.ceil(((double) (l)) / ((double) (s)));
        ArrayList<Integer> index = new ArrayList<>(IntStream.range(0, l).boxed().collect(Collectors.toList()));
        Collections.shuffle(index);
        ArrayList<ArrayList<Integer>> trainIndies = new ArrayList<>();
        ArrayList<ArrayList<Integer>> testIndices = new ArrayList<>();
        for (int i = 0; i < l; i += count) {
            ArrayList<Integer> tmpTrain = new ArrayList<>();
            ArrayList<Integer> tmpTest = new ArrayList<>();
            if (i + count < l) {
                tmpTrain.addAll(index.subList(0, i));
                tmpTrain.addAll(index.subList(i + count, index.size()));
                tmpTest.addAll(index.subList(i, i + count));
            } else {
                tmpTrain.addAll(index.subList(0, i));
                tmpTest.addAll(index.subList(i, index.size()));
            }
            trainIndies.add(tmpTrain);
            testIndices.add(tmpTest);
        }
        return new Pair<>(trainIndies, testIndices);
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
