package params;
/**
 * Created by nikita on 17.09.16.
 */
public class Params {
    public Distances distance;
    public Kernels kernel;
    public int k;
    public double accuracy;
    public Measures measure;
    public SpaceTransformations transformation;

    public Params() {
        distance = Distances.EUCLIDEAN;
        kernel = Kernels.UNIFORM;
        k = 0;
        measure = Measures.ACCURACY;
        accuracy = 0d;
        transformation = SpaceTransformations.ID;
    }

    public Params(Distances distance, Kernels kernel, SpaceTransformations transformation, int k, double accuracy, Measures measure) {
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