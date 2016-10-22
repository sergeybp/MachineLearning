package params;

import classifier.Classes;
import utils.Data;
import utils.DataInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by nikita on 14.10.16.
 */
public class LikelihoodFunction {
    public static final double DEFAULT_VALUE = 1E-12;

    public ArrayList<HashMap<String, Double>> likelihood;
    private ArrayList<Double> totals = new ArrayList<>();

    public LikelihoodFunction() {
        this.likelihood = new ArrayList<>();
    }

    public LikelihoodFunction(int classNumber) {
        init(classNumber);
    }

    private void init(int classNumber) {
        this.likelihood = new ArrayList<>();
        for (int i = 0; i < classNumber; i++) {
            this.likelihood.add(new HashMap<>());
        }
    }

    private void initNonNominal(Data data) {
        for (DataInstance mail: data) {
            HashSet<String> set = new HashSet<>(mail.all);
            for (String word: set) {
                HashMap<String, Double> map = likelihood.get(mail.clazz.get());
                if (map.containsKey(word)) {
                    map.put(word, map.get(word) + 1d / data.size());
                } else map.put(word, 1d / data.size());
            }
        }

        PriorProbability priorProbability = new PriorProbability(data);

        for (int i = 0; i < likelihood.size(); i++) {
            for (Map.Entry<String, Double> entry : likelihood.get(i).entrySet()) {
                final int finalI = i;
                likelihood.get(i).computeIfPresent(entry.getKey(), (k, v) -> (v + 1) / (2d + priorProbability.priorProbability[finalI]));
            }
        }
    }

    private void initNominal(Data data) {
        for (DataInstance mail: data) {
            for (String word: mail.body) {
                HashMap<String, Double> map = likelihood.get(mail.clazz.get());
                if (map.containsKey(word)) {
                    map.put(word, map.get(word) + 1d);
                } else map.put(word, 1d);
            }
            for (String word: mail.title) {
                HashMap<String, Double> map = likelihood.get(mail.clazz.get());
                if (map.containsKey(word)) {
                    map.put(word, map.get(word) + 2d);
                } else map.put(word, 2d);
            }
        }

        int words = likelihood.stream().mapToInt(x -> x.keySet().size()).sum();


        for (int i = 0; i < Data.CLASS_NUMBER; i++) {
            totals.add(likelihood.get(i).values().stream().mapToDouble(x -> x).sum());
        }

        for (int i = 0; i < likelihood.size(); i++) {
            for (Map.Entry<String, Double> entry : likelihood.get(i).entrySet()) {
                final int finalI = i;
                likelihood.get(i).computeIfPresent(entry.getKey(), (k, v) -> (1d + v) / (words + totals.get(finalI)));
            }
        }
    }

    public LikelihoodFunction(Data data, boolean isNominal) {
        init(Data.CLASS_NUMBER);
        if (isNominal) initNominal(data);
        else initNonNominal(data);


    }

    private void addToMap(HashMap<String, Double> map, String word) {
        if (map.containsKey(word)) {
            map.put(word, map.get(word) + 1d);
        } else map.put(word, 1d);
    }

    private void addAllToMap(HashMap<String, Double> map, ArrayList<String> words) {
        words.forEach(w -> addToMap(map, w));
    }

    public int classNumber() {
        return likelihood.size();
    }

    public double get(Classes clazz, String word) {
        int words = likelihood.stream().mapToInt(x -> x.keySet().size()).sum();

        return likelihood.get(clazz.get()).getOrDefault(word, DEFAULT_VALUE);
    }
}
