import javafx.util.Pair;
import params.Method;
import params.Params;
import utils.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by nikita on 13.09.16.
 */
public class LinearRegression {
    private Data data;
    public final static int CROSS_VALIDATION_PARAM = 5;

    private final static double DEFAULT_EPSILON = 1E-5;
    private final static double MAX_EPSILON = 1E-2;
    private final static double MIN_EPSILON = 1E-8;
    private final static double EPSILON_STEP = 10;

    private final static double DEFAULT_STEP = 1E-10;
    private final static double MIN_STEP = 1E-12;
    private final static double MAX_STEP = 1;
    private final static double STEP_STEP = 10;

    private static final int MAX_ITERATIONS = 1000;

    private static final int DEFAULT_SIZE_OF_POPULATION = 50;
    private static final int MIN_SIZE_OF_POPULATION = 10;
    private static final int MAX_SIZE_OF_POPULATION = 200;
    private static final int SIZE_OF_POPULATION_STEP = 80;

    private static final double GENETIC_BOUND = 100_000d;

    private final static int MARK_NUMBER = 2;

    private static Random r = new Random();
    private static BiFunction<Double, Double, Double> random = (rangeMin, rangeMax) -> rangeMin + (rangeMax - rangeMin) * r.nextDouble();

    public LinearRegression(String file) {
        this.data = getDataFromFile(file);
    }

    public static Data getDataFromFile(String file) {
        InputStream is = null;
        try {
            is = new FileInputStream(Paths.get(LinearRegression.class.getResource(file).toURI()).toFile());
        } catch (FileNotFoundException | URISyntaxException e) {
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

    public LinearRegression(Data data) {
        this.data = data;
    }

    public static Vector genetic(Data data, int p) {
        int d = MARK_NUMBER;
        ArrayList<Vector> populations = new ArrayList<>();
        for (int k = 0; k < p; k++) {
            double[] init = new double[d + 1];
            for (int i = 0; i < d + 1; i++)
                init[i] = random.apply(-GENETIC_BOUND, GENETIC_BOUND);
            populations.add(new Vector(init));
        }
        Function<Vector, Double> fitness = w -> standardDeviation(data, w);
        BiFunction<Vector, Vector, Vector> crossover = (mother, father) -> {
            Vector son = new Vector(mother.getComponents());
            int n = mother.getDimensions();
            for (int i = 0; i < n; i++) {
                if (r.nextBoolean())
                    son.set(i, mother.get(i));
                else son.set(i, father.get(i));
            }
            return son;
        };
        Function<Vector, Vector> mutate = a -> {
            int n = a.getDimensions();
            for (int i = 0; i < n; i++) {
                if (r.nextBoolean()) {
                    a.set(i, random.apply(-GENETIC_BOUND, GENETIC_BOUND));
                }
            }
            return a;
        };

        int i = 0;
        do {
            Collections.sort(populations, (o1, o2) -> fitness.apply(o1).compareTo(fitness.apply(o2)));
            ArrayList<Vector> newPopulation = new ArrayList<>();
            for (int k = 0; k < p / 2; k++) {
                int m = 0;
                int f = 0;
                while (m == f) {
                    m = r.nextInt(p / 2);
                    f = r.nextInt(p / 2);
                }
                newPopulation.add(crossover.apply(populations.get(m), populations.get(f)));
            }
            newPopulation.addAll(populations.subList(0, p - newPopulation.size()));
            double averageParentFitness = populations.stream().mapToDouble(fitness::apply).sum() / p;
            double averageChildFitness = newPopulation.stream().mapToDouble(fitness::apply).sum() / p;
            if (averageChildFitness >= averageParentFitness) {
                for (int k = 0; k < p / 2; k++) {
                    newPopulation.set(k, mutate.apply(newPopulation.get(k)));
                }
            }
            populations = new ArrayList<>(newPopulation);
            i++;
        } while (i < MAX_ITERATIONS);
        Collections.sort(populations, (o1, o2) -> fitness.apply(o1).compareTo(fitness.apply(o2)));
        return populations.get(0);
    }

    public static Vector solve(Data data, Params params) {
        switch (params.method) {
            case GENETIC:
                return genetic(data, params.sizeOfPopulation);
            case GRADIENT_DESCENT:
                return gradientDescent(data, params.epsilon, params.step);
        }
        return new Vector();
    }

    public static Vector gradientDescent(Data train, double epsilon, double step) {
        double[][] components = new double[train.size()][MARK_NUMBER];
        double[] y = new double[train.size()];
        for (int j = 0; j < train.size(); j++) {
            components[j][0] = train.get(j).area;
            components[j][1] = train.get(j).rooms;
            y[j] = train.get(j).prices;
        }
        Matrix data = new Matrix(components);
        Vector answer = new Vector(y);
        int d = data.getWidth();
        int l = data.getHeight();
        double[][] newComponents = new double[l][d + 1];
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < d + 1; j++) {
                newComponents[i][j] = j == 0 ? 1d : data.get(i, j - 1);
            }
        }
        Matrix newData = new Matrix(newComponents);
        double[] wInit = new double[d + 1];


        for (int i = 1; i < d + 1; i++) {
            wInit[i] = random.apply(-0.5 / l, 0.5 / l);
        }
        wInit[0] = 0d;
        Vector w = new Vector(wInit);
        Vector prevW;
        int t = 0;
        do {
            t++;
            prevW = new Vector(w);
            Vector grad = newData.transponed().multiply(newData.multiply(prevW).subtract(answer)).multiply(2d / l);
            w = prevW.subtract(grad.multiply(step / t));

        } while (new Vector(w).subtract(new Vector(prevW)).norm() >= epsilon && t < MAX_ITERATIONS);
        return new Vector(w);
    }

    public static Double standardDeviation(Data data, Vector w) {
        double sum = 0d;
        for (DataInstance flat : data) {
            sum += Math.pow(w.get(0) + w.get(1) * flat.area + w.get(2) * flat.rooms - flat.prices, 2);
        }
        return Math.sqrt(sum / data.size());

    }

    private double run(int s, Params params) {
        Pair<ArrayList<ArrayList<Integer>>, ArrayList<ArrayList<Integer>>> cv = crossValidation(data.size(), s);
        ArrayList<ArrayList<Integer>> trainCV = cv.getKey();
        ArrayList<ArrayList<Integer>> testCV = cv.getValue();
        double deviation = 0d;
        for (int i = 0; i < trainCV.size(); i++) {
            Data train = new Data(trainCV.get(i).stream().map(j -> data.get(j)).collect(Collectors.toList()));
            Data test = new Data(testCV.get(i).stream().map(j -> data.get(j)).collect(Collectors.toList()));

            Vector w = solve(train, params);
            deviation += standardDeviation(test, w);
        }
        return deviation / trainCV.size();
    }

    public static Params learn(Data data) {
        Params params = new Params(Method.GRADIENT_DESCENT, DEFAULT_EPSILON, DEFAULT_STEP, DEFAULT_SIZE_OF_POPULATION, Double.MAX_VALUE);
        for (Method method : Method.values()) {
            switch (method) {
                case GRADIENT_DESCENT: {
                    for (double eps = MIN_EPSILON; eps <= MAX_EPSILON; eps *= EPSILON_STEP) {
                        for (double step = MIN_STEP; step <= MAX_STEP; step *= STEP_STEP) {
                            double deviation = new LinearRegression(data).run(CROSS_VALIDATION_PARAM, new Params(method, eps, step, DEFAULT_SIZE_OF_POPULATION));
                            if (deviation < params.deviation) {
                                params = new Params(method, eps, step, DEFAULT_SIZE_OF_POPULATION, deviation);
                            }
                        }
                    }
                    break;
                }
                case GENETIC: {
                    for (int p = MIN_SIZE_OF_POPULATION; p <= MAX_SIZE_OF_POPULATION; p += SIZE_OF_POPULATION_STEP) {
                        double deviation = new LinearRegression(data).run(CROSS_VALIDATION_PARAM, new Params(method, DEFAULT_EPSILON, DEFAULT_STEP, p));
                        if (deviation < params.deviation) {
                            params = new Params(method, DEFAULT_EPSILON, DEFAULT_STEP, p, deviation);
                        }
                    }
                }
            }
        }
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
                tmpTrain.addAll(index.subList(0, i));
                tmpTest.addAll(index.subList(i, index.size()));
            }
            trainIndies.add(tmpTrain);
            testIndices.add(tmpTest);
        }
        return new Pair<>(trainIndies, testIndices);
    }

}
