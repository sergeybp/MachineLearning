package utils;
/**
 * Created by nikita on 18.09.16.
 */
public class PointND {
    public int n;
    public double[] values;

    public PointND(int dimension) {
        this.n = dimension;
        this.values = new double[n];
    }

    public PointND(int n, double[] values) {
        this.n = n;
        this.values = values;
    }

    public double get(int i) {
        return values[i];
    }
}
