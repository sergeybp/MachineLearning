package params;

import classifier.Classes;
import utils.Data;
import utils.DataInstance;

/**
 * Created by nikita on 14.10.16.
 */
public class PriorProbability {
    public double[] priorProbability;

    public PriorProbability() {
        this.priorProbability = new double[0];
    }

    public PriorProbability(int classNumber) {
        this.priorProbability = new double[classNumber];
    }

    public PriorProbability(Data data) {
        priorProbability = new double[Data.CLASS_NUMBER];
        for (DataInstance mail: data) {
            priorProbability[mail.clazz.get()]++;
        }
        for (int i = 0; i < Data.CLASS_NUMBER; i++)
            priorProbability[i] /= data.size();
    }

    public double get(Classes clazz) {
        return priorProbability[clazz.get()];
    }
}
