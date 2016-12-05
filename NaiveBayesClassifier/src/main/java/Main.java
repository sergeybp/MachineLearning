import classifier.NaiveBayesClassifier;
import classifier.Classes;
import params.Params;
import utils.Data;
import utils.DataInstance;
import utils.Utils;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by nikita on 16.09.16.
 */
public class Main {
    private final static String FILE = "./pu1";

    public static void main(String[] args) {
        ArrayList<Data> data = null;
        try {
            data = Utils.getDataFromFile(Paths.get(Main.class.getResource(FILE).toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Params params = new NaiveBayesClassifier(data).learn();
        System.out.println("Accuracy: " + params.accuracy);

        int classifiedHams = 0;
        int totalOfHams = 0;
        int classifiedSpams = 0;
        int totalOfSpams = 0;

        assert data != null;

        for (Data data1: data) {
            for (DataInstance mail : data1) {
                Classes clazz = NaiveBayesClassifier.classify(mail, params, true                                );
                if (mail.clazz == Classes.HAM) {
                    totalOfHams++;
                    if (clazz == Classes.HAM)
                        classifiedHams++;
                }
                if (mail.clazz == Classes.SPAM) {
                    totalOfSpams++;
                    if (clazz == Classes.SPAM)
                        classifiedSpams++;
                }
            }
        }
        System.out.println("Classified hams: " + classifiedHams);
        System.out.println("Total hams: " + totalOfHams);
        System.out.println("Classified spams: " + classifiedSpams);
        System.out.println("Total spams: " + totalOfSpams);

    }


}
