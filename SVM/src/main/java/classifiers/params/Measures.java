package classifiers.params;

import utils.Data;

import java.util.function.BiFunction;

/**
 * Created by nikita on 17.09.16.
 */
public enum Measures {
    ACCURACY("ACCURACY"),
    F1SCORE("F1SCORE");

    private final String name;

    Measures(String s) {
        this.name = s;
    }

    public boolean equalsName(String otherName) {
        return otherName != null && name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

    public BiFunction<Data, Data, Double> get() {
        switch (this) {
            case ACCURACY:
                return (real, answer) -> {
                    ConfusionMatrix matrix = new ConfusionMatrix(real, answer);
                    return ((double) matrix.TP() + matrix.TN()) / ((double) matrix.P() + matrix.N());

                };
            case F1SCORE:
                return (real, answer) -> {
                    ConfusionMatrix matrix = new ConfusionMatrix(real, answer);

                    double precision = 1.0 * matrix.TP() / (matrix.TP() + matrix.FP()),
                            recall = 1.0 * matrix.TP() / (matrix.TP() + matrix.FN());

                    return 2d * precision * recall / (precision + recall);
                };
        }
        return (real, answer) -> 0d;
    }
}
