import dividers.CVDivider;
import dividers.Division;
import javafx.util.Pair;
import params.*;
import utils.Data;
import utils.DataInstance;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nikita on 13.09.16.
 */
public class KNN {
    private Data data;
    public final static int CV_PARAM = 5;
    public final static int MIN_K = 5;
    public final static int MAX_K = 15;
    public final static int STEP_OF_K = 1;

    public KNN(File file) {
        this.data = Utils.getDataFromFile(file);
    }

    public KNN(Data data) {
        this.data = data;
    }

    public static Data evaluate(Data train, Data test, Params params) {
        Data answer = new Data();

        test = params.transformation.get().to.apply(test);
        train = params.transformation.get().to.apply(train);

        for (DataInstance t : test) {

            ArrayList<Pair<Double, Integer>> distances = new ArrayList<>();
            for (DataInstance tr : train)
                distances.add(new Pair<>(params.distance.get().apply(t.point, tr.point), tr.clazz));

            Collections.sort(distances, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));

            double biggestDistance = distances.get(params.k + 1).getKey();
            double[] voices = new double[train.numberOfClasses];

            for (int i = 0; i < params.k; i++) {
                Pair<Double, Integer> point = distances.get(i);
                double importance = params.kernel.get().apply(point.getKey() / biggestDistance);
                voices[point.getValue()] += importance;
            }

            int clazz = Utils.findMaxIndex(voices);
            answer.add(t.point, clazz);
        }
        return params.transformation.get().from.apply(answer);
    }

    private double run(int s, Params params) {
        CVDivider divider = new CVDivider(data, s);

        double accuracy = 0d;
        for (Division div: divider) {
            Data answer = evaluate(div.train, div.test, params);
            accuracy += params.measure.get().apply(div.test, answer);
        }
        return accuracy / divider.size();
    }

    public static Params learn(Data data, Measures measure) {
        Params params = new Params();

        for (int k = MIN_K; k <= MAX_K; k += STEP_OF_K) {
            for (Distances distance : Distances.values()) {
                for (Kernels kernel : Kernels.values()) {
                    for (SpaceTransformations transformation : SpaceTransformations.values()) {

                        double result = new KNN(data).run(CV_PARAM,
                                new Params(distance, kernel, transformation, k, 0d, measure));

                        if (params.accuracy < result)
                            params = new Params(distance, kernel, transformation, k, result, measure);
                    }
                }
            }
        }

        params.measure = measure;
        return params;
    }

}
