import org.knowm.xchart.XYSeries;
import params.Params;
import utils.*;
import utils.Graphics;
import utils.Point;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Created by nikita on 16.09.16.
 */
public class Main {
    private final static String FILE = "prices.txt";

    public static Vector printResult(Data data, Params best) {
        Vector w = LinearRegression.solve(data, best);
        System.out.println(String.format("Standard deviation = %.6f", Utils.standardDeviation(data, w)));
        System.out.println("w = " + LinearRegression.solve(data, best));
        return w;
    }

    public static void draw(Data data, Vector w, String on, String fix) {
        Graphics graphics = new Graphics("Projection on " + on, on, "price");
        ArrayList<Point> points = new ArrayList<>();
        for (DataInstance i : data) {
            points.add(new Point(i.getByName(on), i.prices));
        }
        BiFunction<Double, Double, Double> regression = (x1, x2) -> w.get(1) * x1 + w.get(2) * x2 + w.get(0);
        graphics.addGraphic(points, "test", Color.BLUE, XYSeries.XYSeriesRenderStyle.Scatter);
        points.clear();
        double scale = 2;
        double min = Collections.min(data.instances,
                (o1, o2) -> Double.valueOf(o1.getByName(on)).compareTo(o2.getByName(on)))
                .getByName(on) - scale;
        double max = Collections.max(data.instances,
                (o1, o2) -> Double.valueOf(o1.getByName(on)).compareTo(o2.getByName(on)))
                .getByName(on) + scale;
        Random random = new Random();
        double fixedValue = data.instances.get(random.nextInt(data.size())).getByName(fix);
        double step = (max - min) / 10;
        for (double x = min; x <= max; x += step) {
            if (on.equals("area")) {
                points.add(new Point(x, regression.apply(x, fixedValue)));
            } else points.add(new Point(x, regression.apply(fixedValue, x)));
        }
        graphics.addGraphic(points, "regression", Color.MAGENTA, XYSeries.XYSeriesRenderStyle.Line);
        graphics.show();
    }

    public static void printAndDrawResult(Data data, Params best) {
        Vector w = printResult(data, best);
        draw(data, w, "area", "rooms");
        draw(data, w, "rooms", "area");
    }


    private static void interactive(Data data, Params best) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = "";
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch (line) {
                case "q": {
                    System.exit(0);
                }
                case "exec": {
                    printResult(data, best);
                    break;
                }
                default: {
                    String[] splitted = line.split(",");
                    int area = Integer.parseInt(splitted[0].trim());
                    int rooms = Integer.parseInt(splitted[1].trim());
                    Vector w = LinearRegression.solve(data, best);
                    BiFunction<Integer, Integer, Object> regression = (x1, x2) -> w.get(1) * x1 + w.get(2) * x2 + w.get(0);
                    System.out.println(regression.apply(area, rooms));
                }
            }
        }
    }

    public static void main(String[] args) {
        Data data = null;
        try {
            data = Utils.getDataFromFile(Paths.get(Main.class.getResource(FILE).toURI()).toFile());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Params best = LinearRegression.learn(data);
        System.out.println(best.toString());
     //   printResult(data, best);
       // interactive(data, best);
        printAndDrawResult(data, best);
    }


}
