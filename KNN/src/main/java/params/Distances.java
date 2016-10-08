package params;

import utils.PointND;

import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static java.lang.Math.*;

/**
 * Created by nikita on 16.09.16.
 */
public enum Distances {
    EUCLIDEAN("EUCLIDEAN"),
    MANHATTAN("MANHATTAN"),
    COSINE_SIMILARITY("COSINE_SIMILARITY"),
    CHEBYSHEV("CHEBYSHEV"),
    PEARSON("PEARSON");


    private final String name;

    Distances(String s) {
        this.name = s;
    }

    public String toString() {
        return this.name;
    }

    public BiFunction<PointND, PointND, Double> get() {
        switch (this) {
            case EUCLIDEAN:
                return (a, b) -> sqrt(IntStream.range(0, a.n).mapToDouble(i -> pow(a.get(i) - b.get(i), 2)).sum());
            case MANHATTAN:
                return (a, b) -> IntStream.range(0, a.n).mapToDouble(i -> abs(a.get(i) - b.get(i))).sum();

            case COSINE_SIMILARITY:
                return (a, b) -> IntStream.range(0, a.n).mapToDouble
                        (i -> a.get(i) * b.get(i)
                        ).sum() /
                        (sqrt(
                                IntStream.range(0, a.n).mapToDouble(i -> a.get(i) * a.get(i)).sum())
                         * sqrt(
                                IntStream.range(0, a.n).mapToDouble(i -> b.get(i) * b.get(i)).sum())
                        );
            case CHEBYSHEV:
                return (a, b) -> IntStream.range(0, a.n).mapToDouble(i -> abs(a.get(i) - b.get(i))).max().getAsDouble();
            case PEARSON:
                return (a, b) -> {
                    double Ea = IntStream.range(0, a.n).mapToDouble(a::get).average().getAsDouble();
                    double Eb = IntStream.range(0, b.n).mapToDouble(b::get).average().getAsDouble();
                    return IntStream.range(0, a.n).mapToDouble(i -> (a.get(i) - Ea) * (b.get(i) - Eb)).sum() /
                            sqrt(IntStream.range(0, a.n).mapToDouble(i -> pow((a.get(i) - Ea), 2)).sum() *
                                    IntStream.range(0, b.n).mapToDouble(i -> pow((b.get(i) - Eb), 2)).sum());
                };
        }
        return (a, b) -> 0d;
    }
}
