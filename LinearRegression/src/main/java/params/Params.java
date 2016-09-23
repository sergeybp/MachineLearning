package params;

/**
 * Created by nikita on 23.09.16.
 */
public class Params {
    public Method method;
    public double epsilon;
    public double step;
    public int sizeOfPopulation;
    public double deviation;

    public Params(Method method) {
        this.method = method;
    }

    public Params(Method method, double epsilon, double step, int sizeOfPopulation) {
        this.method = method;
        this.epsilon = epsilon;
        this.step = step;
        this.sizeOfPopulation = sizeOfPopulation;
    }

    public Params(Method method, double epsilon, double step, int sizeOfPopulation, double deviation) {
        this.method = method;
        this.epsilon = epsilon;
        this.step = step;
        this.sizeOfPopulation = sizeOfPopulation;
        this.deviation = deviation;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public String toString() {
        switch (method) {
            case GRADIENT_DESCENT:
                return String.format("Params: method = %s, deviation = %.6f, epsilon = %.10f, step = %.13f", "GRADIENT_DESCENT", deviation, epsilon, step);
            case GENETIC:
                return String.format("Params: method = %s, deviation = %.6f, size of population = %d", "GENETIC", deviation, sizeOfPopulation);
        }
        return "";
    }
}
