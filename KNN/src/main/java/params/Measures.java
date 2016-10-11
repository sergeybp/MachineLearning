package params;

import utils.Data;

import java.util.function.BiFunction;

/**
 * Created by nikita on 17.09.16.
 */
public enum Measures {
    ACCURACY("ACCURACY"),
    F1MEASURE("F1MEASURE");

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
                    double result = 0d;
                    for (int i = 0; i < real.size(); i++) {
                        if (real.get(i).clazz.equals(answer.get(i).clazz))
                            result += 1;

                    }
                    return result / real.size();
                };
            case F1MEASURE:
                return (real, answer) -> {
                    int[][] flags = new int[2][2];
                    for (int i = 0; i < real.size(); i++) {
                        flags[answer.get(i).clazz][real.get(i).clazz]++;
                    }
                    if (flags[1][1] == 0) {
                        return 0d;
                    }
                    double precision = 1.0 * flags[1][1] / (flags[1][1] + flags[1][0]),
                            recall = 1.0 * flags[1][1] / (flags[1][1] + flags[0][1]);

                    return 2d * precision * recall / (precision + recall);
                };
        }
        return (real, answer) -> 0d;
    }
}
