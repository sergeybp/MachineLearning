import params.Params;
import utils.Data;
import utils.Vector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by nikita on 16.09.16.
 */
public class Main {
    private final static String FILE = "prices.txt";

    public static void printResult(Data data, Params best) {
        Vector w = LinearRegression.solve(data, best);
        System.out.println(String.format("Standard deviation = %.6f", LinearRegression.score(data, w)));
        System.out.println("w = " + LinearRegression.solve(data, best));
    }

    public static void main(String[] args) {
        Data data = LinearRegression.getDataFromFile(FILE);
        Params best = LinearRegression.learn(data);
        System.out.println(best.toString());
        printResult(data, best);
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
                    int area = Integer.parseInt(splitted[0]);
                    int rooms = Integer.parseInt(splitted[1]);
                    int price = Integer.parseInt(splitted[2]);
                    data.add(area, rooms, price);
                }
            }
        }
        /*System.out.print(w);
        Graphics graphics = new Graphics("test", "area", "price");
        ArrayList<Point> points = new ArrayList<>();
        for (DataInstance i: data) {
            points.add(new Point(i.area, i.prices));s
        }
        Function<Double, Double> regression = x -> w.get(1) * x + w.get(0);
        graphics.addGraphic(points, "test", Color.BLUE, XYSeries.XYSeriesRenderStyle.Scatter);

        points.clear();
        double scale = 2;
        double min = Collections.min(data.instances, (o1, o2) -> Integer.valueOf(o1.area).compareTo(o2.area)).area - scale;
        double max = Collections.max(data.instances, (o1, o2) -> Integer.valueOf(o1.area).compareTo(o2.area)).area + scale;
        double step = (max - min) / 10;
        for (double x = min; x <= max; x += step) {
            points.add(new Point(x, regression.apply(x)));
        }
        graphics.addGraphic(points, "regression", Color.MAGENTA, XYSeries.XYSeriesRenderStyle.Line);
        graphics.show();
        //Params optimalParams = LinearRegression.learn(data, Measures.F1MEASURE);
        */

    }


}
