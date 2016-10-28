package classifiers.knn.params;

import classifiers.params.Distances;
import classifiers.params.Measures;

/**
 * Created by nikita on 17.09.16.
 */
public class KNNParams {
    public Distances distance;
    public KNNKernels kernel;
    public int k;
    public double accuracy;
    public Measures measure;
    public SpaceTransformations transformation;

    public KNNParams() {
        distance = Distances.EUCLIDEAN;
        kernel = KNNKernels.UNIFORM;
        k = 0;
        measure = Measures.ACCURACY;
        accuracy = 0d;
        transformation = SpaceTransformations.ID;
    }

    public KNNParams(Distances distance, KNNKernels kernel, SpaceTransformations transformation, int k, double accuracy, Measures measure) {
        this.distance = distance;
        this.kernel = kernel;
        this.k = k;
        this.accuracy = accuracy;
        this.measure = measure;
        this.transformation = transformation;
    }

    @Override
    public String toString() {
        return String.format("Params: k = %d, kernel = %s, distance = %s, space tranformation = %s, accuracy measure = %s with value = %.6f", k, kernel, distance, transformation, measure, accuracy);
    }
}