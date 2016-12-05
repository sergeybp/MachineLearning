import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import params.Params;
import utils.DataInstance;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Created by nikita on 25.11.16.
 */
public class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.debug("Hello");

        try {
            SVDRecommendSystem recommendSystem = new SVDRecommendSystem(Paths.get(Main.class.getResource("train.csv").toURI()).toFile());
            Params bestParams = recommendSystem.learn();
            PrintWriter out = new PrintWriter(new File("submission.csv"));
            out.println("Id,Prediction");
            try (BufferedReader br = new BufferedReader(
                    new FileReader(Paths.get(Main.class.getResource("test-ids.csv").toURI()).toFile()))) {
                String line = br.readLine();
                while((line = br.readLine()) != null) {
                    String[] splitted = line.split(",");
                    long testID = Long.parseLong(splitted[0].trim());
                    long userID = Long.parseLong(splitted[1].trim());
                    long itemID = Long.parseLong(splitted[2].trim());
                    out.println(testID + "," + SVDRecommendSystem.getRate(new DataInstance(userID, itemID, 0), bestParams));
                }

            } catch (IOException | URISyntaxException ex) {
                logger.error("Check file: {}", "test-ids.csv");
            }
            out.close();
        } catch (FileNotFoundException | URISyntaxException e) {
            logger.error("Check files");
        }


    }
}
