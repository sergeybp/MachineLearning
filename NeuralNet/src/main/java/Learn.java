import net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Created by nikita on 11.12.16.
 */
public class Learn {
    private static final String TRAIN_IMAGES = "train-images.idx3-ubyte";
    private static final String TRAIN_LABELS = "train-labels.idx1-ubyte";
    private static final String TEST_IMAGES = "t10k-images.idx3-ubyte";
    private static final String TEST_LABELS = "t10k-labels.idx1-ubyte";

    public static final Logger logger = LoggerFactory.getLogger(Learn.class);

    private static final double RATE = 0.07d;
    private static final double REG = 0d;

    public static void cleanFailed() {
        File dir = new File("./failed");
        for (File file : dir.listFiles())
            if (!file.isDirectory())
                file.delete();
    }

    public static void main(String[] args) {
        logger.debug("Hello");

        try {
            File testFeatures = Paths.get(Main.class.getResource(TEST_IMAGES).toURI()).toFile();
            File testLabels = Paths.get(Main.class.getResource(TEST_LABELS).toURI()).toFile();
            Data testData = Utils.readData(testFeatures, testLabels);

            Net net = new Net(new int[]{28 * 28, 25, 10}, new Activation[]{Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID});
            net.initWeights();
            Integer index = 1757;
            Label must = testData.get(index).label;

            Feature feature = testData.get(index).feature;
           // Feature feature = new Feature(ImageIO.read(new File("test.png")));
            ImageIO.write(feature.toImage(), "png", new File("image.png"));
            Label result = net.classify(feature);
           if (!result.equals(must)) {
                logger.debug("Before: {}", result);
                while (!net.classify(feature).equals(must))
                    net.learnStep(new DataInstance(feature, must), new Params(RATE, REG, 1, 0));
                //}
                result = net.classify(feature);
                logger.debug("After: {}", result);
            }
        //    net.w1.writeFile("w1.txt");
        //    net.w2.writeFile("w2.txt");

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void learnOnImage(Net net, String file, Label must) throws IOException {
        String[] split = file.split("_");
        BufferedImage image = ImageIO.read(new File(file));
        Feature feature = new Feature(image);
        ImageIO.write(feature.toImage(), "png", new File("image.png"));
        Label result = net.classify(feature);
        if (!result.equals(must)) {
            logger.debug("Before: {}", result);
            for (int t = 0; t < 10; t++) {
                net.learnStep(new DataInstance(feature, must), new Params(RATE, REG, 1, 0));
            }
            result = net.classify(feature);
            logger.debug("After: {}", result);
        }
       // net.w1.writeFile("w1.txt");
      //  net.w2.writeFile("w2.txt");
    }
}
