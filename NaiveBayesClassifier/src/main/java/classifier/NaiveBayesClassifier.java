package classifier;

import measures.Measures;
import params.LikelihoodFunction;
import params.Params;
import params.PriorProbability;
import rjm.BigDecimalMath;
import utils.Data;
import utils.DataInstance;
import utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.log;

/**
 * Created by nikita on 13.09.16.
 */
public class NaiveBayesClassifier {

    private static final BigDecimal EDGE = new BigDecimal("0.99999999999999999999999999999999999999999999999999999999" +
            "99999999999999966502352573503298256639114873627815878552000000000000000000000000000000112209238310988110" +
            "52262116451068634988993552534998129578709944425851540158548712010627740077450508181586348005453357757132" +
            "61410460117318253784449593497336244429025855407387837912584050219695208368627520921415928128322239157582" +
            "57669005842307757815834357255452343723759067627271973047567892861204073414531798948047917423707028404879" +
            "6314090143178985342826348717");

    private static final int CV_PARAM = 5;

    private ArrayList<Data> data;

    public NaiveBayesClassifier(Path file) {
        this.data = Utils.getDataFromFile(file);
    }

    public NaiveBayesClassifier(ArrayList<Data> data) {
        this.data = data;
    }

    public static Classes classify(DataInstance dataInstance, Params params, boolean useEdge) {

        Classes result = Classes.HAM;
        double maxR = -Double.MAX_VALUE;

        ArrayList<Double> q = new ArrayList<>();

        for (Classes y: Classes.values()) {

            double r = log(y.weight() * params.priorProbability.get(y)) + dataInstance.all.stream().
                    mapToDouble(s -> log(params.likelihoodFunction.get(y, s))).
                    sum();
            q.add(r);
            if (r > maxR) {
                result = y;
                maxR = r;
            }
        }

        if (useEdge) {
            BigDecimal p = BigDecimal.ONE.divide(BigDecimal.ONE.add(
                    BigDecimalMath.exp(
                            new BigDecimal(q.get(0)).
                                    subtract(
                                            new BigDecimal(q.get(1))
                                    )
                    )
                    )
                    , Utils.SCALE, RoundingMode.FLOOR);

            if (p.compareTo(EDGE) > 0) result = Classes.SPAM;
            else result = Classes.HAM;
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
                    result.add(instance.title, instance.body, classify(instance, params, false), instance.file);
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
                new LikelihoodFunction(data.get(0), true),
                run(CV_PARAM, data)
        );
    }

}
