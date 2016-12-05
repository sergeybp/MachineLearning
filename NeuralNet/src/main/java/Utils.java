import net.Feature;
import net.Label;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by nikita on 03.12.16.
 */
public class Utils {
    public static ArrayList<Feature> readImages(File file) throws IOException {
        Main.logger.debug("Reading images file: {}", file);
        DataInputStream is = null;
        try {
            is = new DataInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Main.logger.error("File not found: {} ", file);
            throw new IOException();
        }

        int magic = is.readInt();
        assert magic == 0x00000803;

        int count = is.readInt();
        int rows = is.readInt();
        int columns = is.readInt();

        Main.logger.debug("Count: {}, Rows: {}, Columns: {}", count, rows, columns);

        ArrayList<Feature> images = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Feature feature = new Feature(columns, rows);
            for (int p = 0; p < columns * rows; p++) {
                int b = is.readUnsignedByte();
                feature.set(p, b / 255d);
            }
            images.add(feature);
        }

        Main.logger.debug("Images have been read from file: {}", file);


        return images;
    }

    public static ArrayList<Label> readLabels(File file) throws IOException {
        Main.logger.debug("Reading labels file: {}", file);
        DataInputStream is = null;
        try {
            is = new DataInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Main.logger.error("File not found: {} ", file);
            throw new IOException();
        }

        int magic = is.readInt();
        assert magic == 0x00000801;

        int count = is.readInt();
        Main.logger.debug("Count: {}", count);
        ArrayList<Label> labels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            labels.add(new Label(is.readByte()));
        }
        Main.logger.debug("Labels have been read from file: {}", file);
        return labels;
    }
}
