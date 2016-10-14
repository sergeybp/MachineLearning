package params;

import classifier.Classes;
import utils.Data;
import utils.DataInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikita on 14.10.16.
 */
public class LikelihoodFunction {
    public static final double DEFAULT_VALUE = 1E-12;

    public ArrayList<HashMap<String, Double>> likelihood;

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

    public LikelihoodFunction(Data data) {
        init(Data.CLASS_NUMBER);

        for (DataInstance mail: data) {
            addAllToMap(likelihood.get(mail.clazz.get()), mail.all);
        }

        for (HashMap<String, Double> map : likelihood) {
            double total = map.values().stream().mapToDouble(x -> x).sum();

            for (Map.Entry<String, Double> entry : map.entrySet())
                map.computeIfPresent(entry.getKey(), (k, v) -> v / total);
        }
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
        return likelihood.get(clazz.get()).getOrDefault(word, DEFAULT_VALUE);
    }
}
