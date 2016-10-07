package optimizers;

import params.Params;
import utils.Data;
import utils.Vector;

/**
 * Created by nikita on 08.10.16.
 */
public abstract class Optimizer {
    protected Data data;
    protected Params params;
    protected int maxIterations;

    public Optimizer(Data data, Params params, int maxIterations) {
        this.data = data;
        this.params = params;
        this.maxIterations = maxIterations;
    }

    public abstract Vector optimize();

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
}
