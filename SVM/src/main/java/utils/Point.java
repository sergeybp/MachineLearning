package utils;

/**
 * Created by nikita on 13.09.16.
 */
public class Point extends PointND {
    public double x;
    public double y;

    public Point(double x, double y) {
        super(2);
        this.values[0] = x;
        this.values[1] = y;
        this.x = x;
        this.y = y;
    }
}
