package classifier;

import measures.Measures;
import params.*;
import utils.Data;
import utils.DataInstance;
import utils.Utils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.log;

/**
 * Created by nikita on 13.09.16.
 */
public class NaiveBayesClassifier {

    private static final int CV_PARAM = 5;

    private ArrayList<Data> data;

    public NaiveBayesClassifier(Path file) {
        this.data = Utils.getDataFromFile(file);
    }

    public NaiveBayesClassifier(ArrayList<Data> data) {
        this.data = data;
    }

    public static Classes classify(DataInstance dataInstance, Params params) {

        Classes result = Classes.HAM;
        double maxR = -Double.MAX_VALUE;

        for (Classes y: Classes.values()) {
            double r = log(y.weight() * params.priorProbability.get(y)) + dataInstance.all.stream().
                    mapToDouble(s -> log(params.likelihoodFunction.get(y, s))).
                    sum();

            if (r > maxR) {
                result = y;
                maxR = r;
            }
        }
        return result;
    }

    private double run(int cv, ArrayList<Data> data) {
        double accuracy = 0d;

        for (int t = 0; t < cv; t++) {
            for (Data div : data) {

                Collections.shuffle(div.instances);
                Data test = new Data(div.instances.subList(0, div.size() / cv));
                Data train = new Data(div.instances.subList(div.size() / cv, div.size()));

                Params params = new Params(train);

                Data result = new Data();

                for (DataInstance instance : test) {
                    result.add(instance.title, instance.body, classify(instance, params), instance.file);
                }

                accuracy += Measures.F1MEASURE.get().apply(test, result);
            }

        }
        return accuracy / (data.size() * cv);
    }

    public Params learn() {
        return new Params(
                Data.CLASS_NUMBER,
                new PriorProbability(data.get(0)),
                new LikelihoodFunction(data.get(0)),
                run(CV_PARAM, data)
        );
    }

}
