package net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by nikita on 05.12.16.
 */
public class Weight {

    private static final Logger logger = LoggerFactory.getLogger(Weight.class);
    public double[][] w;
    public int inputSize, outputSize;

    public Weight(int inputSize, int outputSize, File file) {
        logger.debug("Reading weights from: {}", file);
        Scanner scanner = null;
        DataInputStream is = null;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        w = new double[inputSize][outputSize];
        try {
            //is = new DataInputStream(new FileInputStream(file));
            scanner = new Scanner(new FileInputStream(file));
            for (int i = 0; i < inputSize; i++) {
                for (int j = 0; j < outputSize; j++) {
                    set(i, j, Double.parseDouble(scanner.next()));
                }
            }
            scanner.close();
        } catch (IOException e) {
            logger.error("File not found: {} ", file);
        }
    }

    public Weight(int inputSize, int outputSize) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        w = new double[inputSize][outputSize];
        Random random = new Random();
        double min = -1d / (2d * inputSize);
        double max = -min;
        for (int i = 0; i < inputSize; i++) {
            for (int k = 0; k < outputSize; k++) {
                w[i][k] = min + (max - min) * random.nextGaussian();
            }
        }
    }

    public void randomModify(int n) {
        Random random = new Random();
        double min = -1d / (2d * inputSize);
        double max = -min;
        for (int t = 0; t < n; t++) {
            int i = random.nextInt(inputSize);
            int j = random.nextInt(outputSize);
            w[i][j] = min + (max - min) * random.nextGaussian();
        }

    }

    public double get(int i, int j) {
        return w[i][j];
    }

    public void set(int i, int j, double v) {
        w[i][j] = v;
    }


    public void writeFile(String fileName) {
        logger.debug("Writing weight to file: {}", fileName);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.print(this.toString());
        writer.close();
    }

    @Override
    public String toString() {
        StringBuilder answer = new StringBuilder();
        String result = "";
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                answer.append(String.format("%.50f", w[i][j]).replace(",", "."));
                if (j != outputSize - 1) answer.append(" ");
            }
            answer.append("\n");
        }
        return answer.toString();
    }
}