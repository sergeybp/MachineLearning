package params;
import java.util.function.Function;

/**
 * Created by nikita on 17.09.16.
 */
public enum Kernels {
    UNIFORM("UNIFORM"),
    TRIANGULAR("TRIANGULAR"),
    EPANECHNIKOV("EPANECHNIKOV"),
    QUARTIC("QUARTIC"),
    GAUSSIAN("GAUSSIAN");

    private final String name;

    Kernels(String s) {
        this.name = s;
    }

    public boolean equalsName(String otherName) {
        return otherName != null && name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

    public Function<Double, Double> get() {
        switch (this) {
            case UNIFORM:
                return u -> 0.5 * (Math.abs(u) <= 1d ? 1 : 0);
            case TRIANGULAR:
                return u -> (1 - Math.abs(u)) * (Math.abs(u) <= 1d ? 1 : 0);
            case EPANECHNIKOV:
                return u -> 3d / 4d * (1 - u * u) * (Math.abs(u) < 1d ? 1 : 0);
            case QUARTIC:
                return u -> 15d / 16d * Math.pow(1 - u * u, 2) * (Math.abs(u) < 1d ? 1 : 0);
            case GAUSSIAN:
                return u -> 1d / Math.sqrt(2 * Math.PI) * Math.exp(-0.5 * u * u);
        }
        return u -> 0d;
    }
}
