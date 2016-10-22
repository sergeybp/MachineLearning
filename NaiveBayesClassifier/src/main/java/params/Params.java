package params;

import utils.Data;

/**
 * Created by nikita on 13.10.16.
 */
public class Params {
    public PriorProbability priorProbability;
    public LikelihoodFunction likelihoodFunction;
    public double accuracy;
    private final int classNumber;

    public Params(int classNumber) {
        this.classNumber = classNumber;
        priorProbability = new PriorProbability(classNumber);
        likelihoodFunction = new LikelihoodFunction(classNumber);
    }

    public Params(Data data) {
        this.classNumber = Data.CLASS_NUMBER;
        priorProbability = new PriorProbability(data);
        likelihoodFunction = new LikelihoodFunction(data, true);
    }

    public Params(int classNumber, PriorProbability priorProbability, LikelihoodFunction likelihoodFunction) {
        this.classNumber = classNumber;
        this.priorProbability = priorProbability;
        this.likelihoodFunction = likelihoodFunction;
    }

    public Params(int classNumber, PriorProbability priorProbability, LikelihoodFunction likelihoodFunction, double accuracy) {
        this.classNumber = classNumber;
        this.priorProbability = priorProbability;
        this.likelihoodFunction = likelihoodFunction;
        this.accuracy = accuracy;
    }
}
