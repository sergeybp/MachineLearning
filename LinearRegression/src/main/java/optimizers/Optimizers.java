package optimizers;

import params.Params;
import utils.Data;

/**
 * Created by nikita on 23.09.16.
 */
public enum Optimizers {
    GRADIENT_DESCENT ("GRADIENT_DESCENT"),
    GENETIC ("GENETIC");

    private final String name;

    Optimizers(String s) {
        this.name = s;
    }

    public boolean equalsName(String otherName) {
        return otherName != null && name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

    public Optimizer get(Data data, Params params, int maxIterations) {
        switch (this) {
            case GRADIENT_DESCENT: return new GradientDescentOptimizer(data, params, maxIterations);
            case GENETIC: return new GeneticOptimizer(data, params, maxIterations);
        }
        return null;
    }

}
