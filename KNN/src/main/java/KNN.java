import javafx.util.Pair;
import params.*;
import utils.*;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by nikita on 13.09.16.
 */
public class KNN {
    private Data data;
    public final static int CROSS_VALIDATION_PARAM = 5;
    public static int MIN_K = 5;
    public static int MAX_K = 15;
    public static int LEARN_STEP_OF_K = 1;

    public KNN(String file) {
        this.data = getDataFromFile(file);
    }

    public static Data getDataFromFile(String file) {
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
            int clazz = Integer.parseInt(splitted[2]);
            data.add(new Point(x, y), clazz);
        }
        return data;
    }

    public KNN(Data data) {
        this.data = data;
    }

    public static Data evaluate(Data train, Data test, Params params) {
        Data answer = new Data();
        test = params.transformation.get().to.apply(test);
        train = params.transformation.get().to.apply(train);
        for (DataInstance t : test) {
            PriorityQueue<Pair<Double, Integer>> queue = new PriorityQueue<>((Comparator<Pair<Double, Integer>>) (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
            double biggestDistance = 0d;
            for (DataInstance tr : train) {
                double d = params.distance.get().apply(t.point, tr.point);
                queue.add(new Pair<>(d, tr.clazz));
                if (biggestDistance < d) biggestDistance = d;
            }
            double numZero = 0, numOne = 0;
            for (int i = 0; i < params.k; i++) {
                Pair<Double, Integer> point = queue.poll();
                double importance = params.kernel.get().apply(point.getKey() / biggestDistance);
                if (point.getValue() == 0) numZero += importance;
                else numOne += importance;
            }
            int clazz = numZero > numOne ? 0 : 1;
            answer.add(t.point, clazz);
        }
        return params.transformation.get().from.apply(answer);
    }

    private double run(int s, Params params) {
        Pair<ArrayList<ArrayList<Integer>>, ArrayList<ArrayList<Integer>>> cv = crossValidation(data.size(), s);
        ArrayList<ArrayList<Integer>> trainCV = cv.getKey();
        ArrayList<ArrayList<Integer>> testCV = cv.getValue();

        Pair<Pair<Data, Data>, Double> result = new Pair<>(new Pair<>(new Data(), new Data()), 0d);
        double accuracy = 0d;
        for (int i = 0; i < trainCV.size(); i++) {
            Data train = new Data(trainCV.get(i).stream().map(j -> data.get(j)).collect(Collectors.toList()));
            Data test = new Data(testCV.get(i).stream().map(j -> data.get(j)).collect(Collectors.toList()));
            System.out.println(train.size() + " " + test.size());
            Data answer = evaluate(train, test, params);

            accuracy += params.measure.get().apply(test, answer);
            //if (result.getValue() < accuracy)
              //  result = new Pair<>(new Pair<>(train, answer), accuracy);
        }
        return accuracy / trainCV.size();
    }

    public static Params learn(Data data, Measures measure) {
        Params params = new Params();
        for (int k = MIN_K; k < MAX_K; k += LEARN_STEP_OF_K) {
            for (Distances distance : Distances.values()) {
                for (Kernels kernel : Kernels.values()) {
                    for (SpaceTransformations transformation: SpaceTransformations.values()) {
                        double result = new KNN(data).run(CROSS_VALIDATION_PARAM, new Params(distance, kernel, transformation, k, 0d, measure));
                        if (params.accuracy < result) {
                            params = new Params(distance, kernel, transformation, k, result, measure);
                        }
                    }
                }
            }
        }
        params.measure = measure;
        return params;
    }

    private Pair<ArrayList<ArrayList<Integer>>, ArrayList<ArrayList<Integer>>> crossValidation(int l, int s) {
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
                tmpTrain.addAll(index.subList(0, i - 2));
                tmpTest.addAll(index.subList(i - 2, index.size()));
            }
            trainIndies.add(tmpTrain);
            testIndices.add(tmpTest);
        }
        return new Pair<>(trainIndies, testIndices);
    }

}
