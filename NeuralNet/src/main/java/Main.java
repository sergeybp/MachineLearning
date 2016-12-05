import net.Activation;
import net.Feature;
import net.Label;
import net.Net;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by nikita on 03.12.16.
 */
public class Main {

    private static final String TRAIN_IMAGES = "train-images.idx3-ubyte";
    private static final String TRAIN_LABELS = "train-labels.idx1-ubyte";
    private static final String TEST_IMAGES = "t10k-images.idx3-ubyte";
    private static final String TEST_LABELS = "t10k-labels.idx1-ubyte";

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final double RATE = 0.07;
    private static final double RED = 0d;

    public static void cleanFailed() {
        File dir = new File("./failed");
        for (File file : dir.listFiles())
            if (!file.isDirectory())
                file.delete();
    }

    public static void main(String[] args) {
        logger.debug("Hello");

        try {
            //ArrayList<net.Feature> trainFeatures = Utils.readImages(Paths.get(Main.class.getResource(TRAIN_IMAGES).toURI()).toFile());
            //ArrayList<net.Label> trainLabels = Utils.readLabels(Paths.get(Main.class.getResource(TRAIN_LABELS).toURI()).toFile());

            Net net = new Net(28 * 28, 25, 10, Activation.SIGMOID);
            net.initWeights(Paths.get(Main.class.getResource("w1.txt").toURI()).toFile(), Paths.get(Main.class.getResource("w2.txt").toURI()).toFile());
            // net.initWeights();
            //net.learn(trainFeatures, trainLabels, RATE, 1d / trainFeatures.size(), RED);

            ArrayList<Feature> testFeatures = Utils.readImages(Paths.get(Main.class.getResource(TEST_IMAGES).toURI()).toFile());
            ArrayList<Label> testLabels = Utils.readLabels(Paths.get(Main.class.getResource(TEST_LABELS).toURI()).toFile());


            int failed = 0;
            cleanFailed();
            failed = 0;
            for (int i = 0; i < testFeatures.size(); i++) {
                Feature f = testFeatures.get(i);
                Label result = net.classify(f);
                if (!result.equals(testLabels.get(i))) {
                 //   net.learnStep(f, testLabels.get(i), RATE, RED);
                }
               // result = net.classify(f);
                if (!result.equals(testLabels.get(i))) {
                    failed++;
                    File file = new File("./failed/" + i + "_" + result.label + "_" + testLabels.get(i).label + "_image.png");
                    ImageIO.write(testFeatures.get(i).toImage(), "png", file);
                }
            }
//            learnOnImage(net, "./failed/1_5_2_image.png", new Label(2));


            Main.logger.info("Accuracy: {}%", (1d - (double) failed / testFeatures.size()) * 100);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void learnOnImage(Net net, String file, Label must) throws IOException {
        BufferedImage image = ImageIO.read(new File(file));
        Feature feature = new Feature(image);
        Label result = net.classify(feature);
        System.out.println(result);
        if (!result.equals(must)) {
            for (int t = 0; t < 10; t++) {
                net.learnStep(feature, must, RATE, RED);
                result = net.classify(feature);
            }
            System.out.println(result);
        }
        net.w1.writeFile("./src/main/resources/w1.txt");
        net.w2.writeFile("./src/main/resources/w2.txt");
    }
}
