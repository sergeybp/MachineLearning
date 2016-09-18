import params.Measures;
import params.Params;
import utils.Data;
import utils.Graphics;
import utils.Point;

import java.awt.*;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by nikita on 16.09.16.
 */
public class Main {
    private final static String FILE = "chips.txt";

    public static void main(String[] args) {
        Data data = KNN.getDataFromFile(FILE);
        Params optimalParams = KNN.learn(data, Measures.F1MEASURE);
        System.out.print(optimalParams);

        double scale = 0.1;

        double minX = ((Point) Collections.min(data.instances, (o1, o2) -> ((Double) ((Point) o1.getPoint()).x).compareTo(((Point) o2.getPoint()).x)).getPoint()).x - scale;

        double maxX = ((Point) Collections.min(data.instances, (o1, o2) -> -((Double) ((Point) o1.getPoint()).x).compareTo(((Point) o2.getPoint()).x)).getPoint()).x + scale;

        double minY = ((Point) Collections.min(data.instances, (o1, o2) -> ((Double) ((Point) o1.getPoint()).y).compareTo(((Point) o2.getPoint()).y)).getPoint()).y - scale;

        double maxY = ((Point) Collections.min(data.instances, (o1, o2) -> -((Double) ((Point) o1.getPoint()).y).compareTo(((Point) o2.getPoint()).y)).getPoint()).y + scale;

        double step = 0.005;

        Data test = new Data();

        for (double x = minX; x < maxX; x += step) {
            for (double y = minY; y < maxY; y += step) {
                test.add(new Point(x, y), 0);
            }
        }

        Data answer = KNN.evaluate(data, test, optimalParams);

        Graphics graphics1 = new Graphics(optimalParams.toString(), "x", "y");

        graphics1.addGraphic(answer.instances.stream().filter(x -> x.clazz == 0).map(y -> (Point) y.point).collect(Collectors.toList()), "result of class = 0", Color.BLUE);
        graphics1.addGraphic(answer.instances.stream().filter(x -> x.clazz == 1).map(y -> (Point) y.point).collect(Collectors.toList()), "result of class = 1", Color.PINK);
        graphics1.addGraphic(data.instances.stream().filter(x -> x.clazz == 0).map(y -> (Point) y.point).collect(Collectors.toList()), "known of class = 0", Color.CYAN);
        graphics1.addGraphic(data.instances.stream().filter(x -> x.clazz == 1).map(y -> (Point) y.point).collect(Collectors.toList()), "known of class = 1", Color.MAGENTA);

        graphics1.show();
    }


}
