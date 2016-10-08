import params.Measures;
import params.Params;
import utils.*;
import utils.Graphics;
import utils.Point;

import java.awt.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nikita on 16.09.16.
 */
public class Main {
    private final static String FILE = "chips.txt";
    private final static double SCALE = 0.1;
    private final static double STEP = 0.005;

    private static void draw(Data data, Params params) {
        double scale = SCALE;

        Comparator<DataInstance> comparatorX = (o1, o2) -> ((Double) ((Point) o1.getPoint()).x).compareTo(((Point) o2.getPoint()).x);
        Comparator<DataInstance> comparatorY = (o1, o2) -> ((Double) ((Point) o1.getPoint()).y).compareTo(((Point) o2.getPoint()).y);

        double minX = ((Point) Collections.min(data.instances, comparatorX).getPoint()).x - scale;

        double maxX = ((Point) Collections.max(data.instances, comparatorX).getPoint()).x + scale;

        double minY = ((Point) Collections.min(data.instances, comparatorY).getPoint()).y - scale;

        double maxY = ((Point) Collections.max(data.instances, comparatorY).getPoint()).y + scale;

        double step = STEP;

        Data test = new Data();

        for (double x = minX; x < maxX; x += step) {
            for (double y = minY; y < maxY; y += step) {
                test.add(new Point(x, y), 0);
            }
        }

        Data answer = KNN.evaluate(data, test, params);

        Graphics graphics1 = new Graphics(params.toString(), "x", "y");

        graphics1.addScatterGraphic(splitBy(answer, 0), "result of class = 0", Color.BLUE);
        graphics1.addScatterGraphic(splitBy(answer, 1), "result of class = 1", Color.PINK);
        graphics1.addScatterGraphic(splitBy(data, 0), "known of class = 0", Color.CYAN);
        graphics1.addScatterGraphic(splitBy(data, 1), "known of class = 1", Color.MAGENTA);

        graphics1.show();
    }

    private static List<Point> splitBy(Data data, int clazz) {
        return data.instances
                    .stream()
                    .filter(x -> x.clazz == clazz)
                    .map(y -> (Point) y.point)
                    .collect(Collectors.toList());
    }



    public static void main(String[] args) {
        Data data = null;
        try {
            data = Utils.getDataFromFile(Paths.get(Main.class.getResource(FILE).toURI()).toFile());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Params optimalParams = KNN.learn(data, Measures.F1MEASURE);
        System.out.print(optimalParams);
        draw(data, optimalParams);
    }
}
