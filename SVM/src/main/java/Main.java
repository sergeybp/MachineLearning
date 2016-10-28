import classifiers.knn.KNN;
import classifiers.knn.params.KNNParams;
import classifiers.params.Classes;
import classifiers.params.Measures;
import classifiers.svm.SVM;
import classifiers.svm.params.SVMParams;
import dividers.CVDivider;
import dividers.Division;
import utils.*;
import utils.Graphics;
import utils.Point;

import java.awt.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by nikita on 16.09.16.
 */
public class Main {
    private final static String FILE = "chips.txt";
    private final static double SCALE = 0.1;
    private final static double STEP = 0.005;

    private static void draw(Data data, SVMParams svmParams) {
        double scale = SCALE;

        Comparator<DataInstance> comparatorX = (o1, o2) -> ((Double) ((Point) o1.point).x).compareTo(((Point) o2.point).x);
        Comparator<DataInstance> comparatorY = (o1, o2) -> ((Double) ((Point) o1.point).y).compareTo(((Point) o2.point).y);

        double minX = ((Point) Collections.min(data.instances, comparatorX).point).x - scale;

        double maxX = ((Point) Collections.max(data.instances, comparatorX).point).x + scale;

        double minY = ((Point) Collections.min(data.instances, comparatorY).point).y - scale;

        double maxY = ((Point) Collections.max(data.instances, comparatorY).point).y + scale;

        double step = STEP;

        Data test = new Data();

        for (double x = minX; x < maxX; x += step) {
            for (double y = minY; y < maxY; y += step) {
                test.add(new Point(x, y), Classes.FIRST);
            }
        }

        Data answer = SVM.evaluate(data, test, svmParams);

        Graphics graphics1 = new Graphics(svmParams.toString(), "x", "y");

        graphics1.addScatterGraphic(splitBy(answer, Classes.FIRST), "result of class = 0", Color.BLUE);
        graphics1.addScatterGraphic(splitBy(answer, Classes.SECOND), "result of class = 1", Color.PINK);
        graphics1.addScatterGraphic(splitBy(data, Classes.FIRST), "known of class = 0", Color.CYAN);
        graphics1.addScatterGraphic(splitBy(data, Classes.SECOND), "known of class = 1", Color.MAGENTA);

        graphics1.show();
    }

    private static List<Point> splitBy(Data data, Classes clazz) {
        return data.instances
                    .stream()
                    .filter(x -> x.clazz == clazz)
                    .map(y -> (Point) y.point)
                    .collect(Collectors.toList());
    }

    private static class Score implements Comparable<Score> {
        public double value;
        public String method;
        public int index;

        public Score(double value, String method, int index) {
            this.value = value;
            this.method = method;
            this.index = index;
        }

        @Override
        public int compareTo(Score o) {
            return Double.compare(this.value, o.value);
        }

    }

    public static void main(String[] args) {
        Data data = null;
        try {
            data = Utils.getDataFromFile(Paths.get(Main.class.getResource(FILE).toURI()).toFile());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        assert data != null;

        SVMParams svmParams = SVM.learn(data, Measures.F1SCORE);
        KNNParams knnParams = KNN.learn(data, Measures.F1SCORE);

        System.out.println(knnParams);
        System.out.println(svmParams);

        CVDivider divider = new CVDivider(data, 8);

        ArrayList<Score> knnScores = new ArrayList<>();
        ArrayList<Score> svmScores = new ArrayList<>();

        int i = 0;
        for (Division div: divider) {
            Data answer = KNN.evaluate(div.train, div.test, knnParams);
            knnScores.add(new Score(knnParams.measure.get().apply(div.test, answer), "KNN", i));
            answer = SVM.evaluate(div.train, div.test, svmParams);
            svmScores.add(new Score(svmParams.measure.get().apply(div.test, answer), "SVM", i));
            i++;
        }
        int n = knnScores.size();
        int m = n;
        System.out.println("n = m = " + m);

        ArrayList<Score> var = new ArrayList<>(Stream.concat(knnScores.stream(), svmScores.stream()).collect(Collectors.toList()));
        Collections.sort(var);

        ArrayList<Integer> rx = new ArrayList<>(knnScores.stream().map(var::indexOf).collect(Collectors.toList()));
        ArrayList<Integer> ry = new ArrayList<>(svmScores.stream().map(var::indexOf).collect(Collectors.toList()));

        double Rx = rx.stream().mapToDouble(x -> x).sum();
        double Ry = ry.stream().mapToDouble(x -> x).sum();
        double W = Rx;
        System.out.println("W = " + W);
        double alpha = 0.05; // importance
        double Fa = 1.960; //  quantile (1-a/2) of standard normal distribution
        double Wc = (W - m * (m + n + 1d) / 2d) / (Math.sqrt(m * n * (m + n + 1d) / 12d));
        System.out.println("Wc = " + Math.abs(Wc) + "  ?>=  " + Fa);

        double Wcx = 0.5 * Wc  * (1d + Math.sqrt((n + n - 2d) / (n + m - 1d - Wc * Wc)));

        double xa = 1.645; // quantile (1-a) of standard normal distribution
        double ya = 2.1448; // quantile (1-a) of Student distribution of (n + m - 2) degree
        System.out.println("Wcx = " + Math.abs(Wcx) + "  ?>=  " + (xa + ya) / 2d);
       // draw(data, svmParams);
    }
}
