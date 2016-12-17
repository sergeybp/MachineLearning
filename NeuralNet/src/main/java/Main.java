import net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Utils;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Created by nikita on 03.12.16.
 */
public class Main {

    private static final String TRAIN_IMAGES = "train-images.idx3-ubyte";
    private static final String TRAIN_LABELS = "train-labels.idx1-ubyte";
    private static final String TEST_IMAGES = "t10k-images.idx3-ubyte";
    private static final String TEST_LABELS = "t10k-labels.idx1-ubyte";

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final double RATE = 0.07d;
    private static final double REG = 0d;
    private static final double PERCENTAGE = 100;

    public static void cleanFailed() {
        File dir = new File("./failed");
        for (File file : dir.listFiles())
            if (!file.isDirectory())
                file.delete();
    }

    public static void main(String[] args) {
        logger.debug("Hello");

        try {
            File trainFeatures = Paths.get(Main.class.getResource(TRAIN_IMAGES).toURI()).toFile();
            File trainLabels = Paths.get(Main.class.getResource(TRAIN_LABELS).toURI()).toFile();
            File testFeatures = Paths.get(Main.class.getResource(TEST_IMAGES).toURI()).toFile();
            File testLabels = Paths.get(Main.class.getResource(TEST_LABELS).toURI()).toFile();

            Data trainData = Utils.readData(trainFeatures, trainLabels);
            Data testData = Utils.readData(testFeatures, testLabels);

            Net net = new Net(new int[]{28 * 28, 60, 60, 60, 10}, new Activation[]{Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID});
            //net.initWeights(new File[]{new File("w0.txt"), new File("w1.txt"), new File("w2.txt"), new File("w3.txt")});
            Params params = net.learn(trainData, testData);

            Main.logger.info("Best params: {}", params.toString());

            int failed = 0;
            cleanFailed();
            failed = 0;
            for (int i = 0; i < testData.size(); i++) {
                Feature f = testData.get(i).feature;
                Label result = net.classify(f);
                if (!result.equals(testData.get(i).label)) {
                    failed++;
               //     net.learnStep(testData.get(i), new Params(RATE, REG, 1));
  //                  File file = new File("./failed/" + i + "_" + result.value + "_" + testData.get(i).label + "_image.png");
//                    ImageIO.write(testData.get(i).feature.toImage(), "png", file);
                }
            }

            Main.logger.info("Accuracy: {}%", (1d - (double) failed / testData.size()) * PERCENTAGE);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
