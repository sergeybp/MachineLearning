package classifiers.svm.params;

import classifiers.params.Distances;
import utils.PointND;

import java.util.function.BiFunction;

/**
 * Created by nikita on 17.09.16.
 */
public enum SVMKernels {
//    QUADRIC("QUADRIC"),
//    POLINOMIAL("POLINOMIAL"),
//    LINEAR("LINEAR"),
    GAUSSIAN("GAUSSIAN");

    private final String name;

    SVMKernels(String s) {
        this.name = s;
    }

    public boolean equalsName(String otherName) {
        return otherName != null && name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

    public BiFunction<PointND, PointND, Double> get() {
        switch (this) {
            /*case LINEAR:
                return PointND::scalarProduct;
            case QUADRIC:
                return (a, b) -> Math.pow(a.scalarProduct(b), 2d);
            case POLINOMIAL:
                return (a, b) -> Math.pow(a.scalarProduct(b) + 1d, 3d);*/
            case GAUSSIAN:
                return (a, b) -> Math.exp(-Math.pow(Distances.EUCLIDEAN.get().apply(a, b), 2d) / 2d);
        }
        return (a, b) -> 0d;
    }
}
