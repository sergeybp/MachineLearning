package optimizers;

import params.Params;
import utils.Data;
import utils.Matrix;
import utils.Utils;
import utils.Vector;

/**
 * Created by nikita on 08.10.16.
 */
public class GradientDescentOptimizer extends Optimizer {

    private final static int MARK_NUMBER = 2;

    public GradientDescentOptimizer(Data data, Params params, int maxIterations) {
        super(data, params, maxIterations);
    }

    private Matrix getX() {
        double[][] components = new double[data.size()][MARK_NUMBER];
        for (int j = 0; j < data.size(); j++) {
            components[j][0] = data.get(j).area;
            components[j][1] = data.get(j).rooms;
        }
        Matrix data = new Matrix(components);
        int d = data.getWidth();
        int l = data.getHeight();
        double[][] newComponents = new double[l][d + 1];
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < d + 1; j++) {
                newComponents[i][j] = j == 0 ? 1d : data.get(i, j - 1);
            }
        }
        return new Matrix(newComponents);
    }

    private Vector getY() {
        double[] y = new double[data.size()];
        for (int j = 0; j < data.size(); j++) {
            y[j] = data.get(j).prices;
        }
        return new Vector(y);
    }

    @Override
    public Vector optimize() {
        Matrix X = getX();
        Vector y = getY();

        int d = X.getWidth() - 1;
        int l = X.getHeight();

        double[] wInit = new double[d + 1];
        for (int i = 1; i < d + 1; i++) {
            wInit[i] = Utils.random(-0.5 / l, 0.5 / l);
        }
        wInit[0] = 0d;

        Vector w = new Vector(wInit);
        Vector prevW;
        int i = 0;
        do {
            i++;
            prevW = new Vector(w);
            Vector grad = X.transponed().multiply(X.multiply(prevW).subtract(y)).multiply(2d / l);
            w = prevW.subtract(grad.multiply(params.step / i));
        } while (new Vector(w).subtract(new Vector(prevW)).norm() >= params.epsilon && i < maxIterations);

        return w;
    }
}
