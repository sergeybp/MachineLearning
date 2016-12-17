package utils;

import net.Data;
import net.Feature;
import net.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by nikita on 03.12.16.
 */
public class Utils {

    public static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static ArrayList<Feature> readImages(File file) {
        logger.debug("Reading images file: {}", file);
        DataInputStream is = null;
        try {
            is = new DataInputStream(new FileInputStream(file));
            int magic = is.readInt();
            assert magic == 0x00000803;

            int count = is.readInt();
            int rows = is.readInt();
            int columns = is.readInt();

            logger.debug("Count: {}, Rows: {}, Columns: {}", count, rows, columns);

            ArrayList<Feature> images = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                Feature feature = new Feature(columns, rows);
                for (int p = 0; p < columns * rows; p++) {
                    int b = is.readUnsignedByte();
                    feature.set(p, b / 255d);
                }
                images.add(feature);
            }

            logger.debug("Images have been read from file: {}", file);


            return images;
        } catch (IOException e) {
            logger.error("File not found: {} ", file);
            throw new UncheckedIOException(e);
        }


    }

    public static ArrayList<Label> readLabels(File file) {
        logger.debug("Reading labels file: {}", file);
        DataInputStream is = null;
        try {
            is = new DataInputStream(new FileInputStream(file));
            int magic = is.readInt();
            assert magic == 0x00000801;

            int count = is.readInt();
            logger.debug("Count: {}", count);
            ArrayList<Label> labels = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                labels.add(new Label(is.readByte()));
            }
            logger.debug("Labels have been read from file: {}", file);
            return labels;
        } catch (IOException e) {
            logger.error("File not found: {} ", file);
            throw new UncheckedIOException(e);
        }

    }

    public static Data readData(File images, File labels) {
        return new Data(readImages(images), readLabels(labels));
    }

    public static int argMax(double[] a) {
        int maxM = 0;
        double maxV = Double.MIN_VALUE;
        for (int m = 0; m < a.length; m++) {
            if (a[m] > maxV) {
                maxV = a[m];
                maxM = m;
            }
        }
        return maxM;
    }
}
