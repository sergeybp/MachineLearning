import org.knowm.xchart.XYSeries;
import params.KernelRegressionParams;
import params.RobustRegressionParams;
import utils.*;
import utils.Graphics;
import utils.Point;

import java.awt.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nikita on 16.09.16.
 */
public class Main {
    private final static String FILE = "data.txt";

    public static void main(String[] args) {
        Data data = null;
        try {
            data = Utils.getDataFromFile(Paths.get(Main.class.getResource(FILE).toURI()).toFile());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        assert data != null;
        final double minX = Collections.min(data.instances,
                (o1, o2) -> Double.valueOf(o1.point.x).compareTo(o2.point.x)).point.x;
        final double maxX = Collections.max(data.instances,
                (o1, o2) -> Double.valueOf(o1.point.x).compareTo(o2.point.x)).point.x;

        /*final double minY = Collections.min(data.instances,
                (o1, o2) -> Double.valueOf(o1.point.y).compareTo(o2.point.y)).point.y;
        final double maxY = Collections.max(data.instances,
                (o1, o2) -> Double.valueOf(o1.point.y).compareTo(o2.point.y)).point.y;

        Function<Point, Point> norm = p -> new Point((p.x - minX) / (maxX - minX), (p.y - minY) / (maxY - minY));
        Function<Point, Point> unnorm = p -> new Point(p.x * (maxX - minX) + minX, p.y * (maxY - minY) + minY);

        Data normData = new Data(data.instances);
        for (int i = 0; i < normData.size(); i++) {
            Point p = normData.instances.get(i).point;
            normData.instances.set(i, new DataInstance(norm.apply(p)));
        }*/
        KernelRegressionParams kernelBest = KernelNonparametricRegression.learn(data);
        System.out.println(kernelBest.toString());

        Graphics graphics = new Graphics("Kernel regression", "x", "y");

        ArrayList<Point> points = new ArrayList<>();
        for (DataInstance instance: data) {
            points.add((instance.point));
        }

        graphics.addGraphic(points, "test", Color.green, XYSeries.XYSeriesRenderStyle.Scatter);

        points.clear();
        for (double p = minX; p < maxX; p += 0.025) {
            points.add((KernelNonparametricRegression.evaluate(new DataInstance(new Point(p, 0d)), kernelBest).point));
        }

        graphics.addGraphic(points, "kernel regression", Color.orange, XYSeries.XYSeriesRenderStyle.Line);

        graphics.show();

        RobustRegressionParams robustBest = RobustNonparametricRegression.learn(data);
        System.out.println(robustBest.toString());

        graphics = new Graphics("Robust regression", "x", "y");

        points = new ArrayList<>();
        for (DataInstance instance: data) {
            points.add((instance.point));
        }

        graphics.addGraphic(points, "test", Color.green, XYSeries.XYSeriesRenderStyle.Scatter);

        points.clear();
        for (double p = minX; p < maxX; p += 0.05) {
            points.add((RobustNonparametricRegression.evaluate(new DataInstance(new Point(p, 0d)), robustBest).point));
        }

        graphics.addGraphic(points, "robust regression", Color.orange, XYSeries.XYSeriesRenderStyle.Line);

        graphics.show();


    }


}
