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
    TRIWEIGHT("TRIWEIGHT"),
    GAUSSIAN("GAUSSIAN"),
    COSINE("COSINE");

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
            case TRIWEIGHT:
                return u -> 35d / 32d * Math.pow(1 - u * u, 3) * (Math.abs(u) < 1d ? 1 : 0);
            case GAUSSIAN:
                return u -> 1d / Math.sqrt(2 * Math.PI) * Math.exp(-0.5 * u * u);
            case COSINE:
                return u -> Math.PI / 4d * Math.cos(Math.PI / 2 * u) * (Math.abs(u) < 1d ? 1 : 0);
        }
        return u -> 0d;
    }
}
