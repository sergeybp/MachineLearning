import javafx.application.Application;
import javafx.stage.Stage;
import net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ApplePencil;
import utils.Utils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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

    private static Draw draw;

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static Net net;
    private static int nowLabel;
    private static Params params;

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

            net = new Net(new int[]{28 * 28, 60, 60, 60, 10}, new Activation[]{Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID});
            net.initWeights(new File[]{new File("w0.txt"), new File("w1.txt"), new File("w2.txt"), new File("w3.txt")});
            //params = net.learn(trainData, testData);

//            Main.logger.info("Best params: {}", params.toString());

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


        //Just an interface to get pictures from your perfect fingers.
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Usage:\n" +
                        "draw -- creates window to draw in\n" +
                        "get <label> -- gets picture you drawn + real label for it");
                while (true) {
                    try {
                        String line = reader.readLine();
                        if (line == null || line.equals("exit")) {
                            System.exit(0);
                            return;
                        }
                        if(line.equals("draw")){
                            goDraw();
                        }
                        String[] slices = line.trim().split(" +");
                        switch (slices[0]) {
                            case "get":
                                nowLabel = Integer.parseInt(slices[1]);
                                getValues();
                                break;

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    public static void goDraw(){
        draw = new Draw();
        draw.showMe();
        return;
    }

    public static void getValues(){
        //Just show what we got.
        Double[][] a = draw.getValues();
        for(int i = 0 ; i < 28; i++){
            for(int j = 0 ; j < 28 ; j++){
                if(a[j][i].equals(1d))
                    System.out.print("#");
                else
                    System.out.print("*");
            }
            System.out.println();
        }
        draw.setVisible(false);

        //Creating a Feature
        Feature tmp = new Feature(28,28);
        for(int i = 0 ; i < 28; i++){
            for(int j = 0 ; j < 28; j++){
                tmp.x[j + 28 * i] = a[j][i];
            }
        }
        Label l = net.classify(tmp);
        if(l.value == nowLabel){
            System.out.println("[CORRECT]");
        } else {
            System.out.println("[WRONG] Got label = "+l.value);

            //net.learnStep(new DataInstance(tmp,l), params);
        }


    }

    public static void prevMain(){

    }

}
