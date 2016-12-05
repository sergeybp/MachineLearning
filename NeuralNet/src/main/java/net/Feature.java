package net;

import java.awt.image.BufferedImage;

/**
 * Created by nikita on 04.12.16.
 */
public class Feature {
    public double[] x;
    public int width, height;

    public Feature(int width, int height) {
        this.x = new double[width * height];
        this.width = width;
        this.height = height;
    }

    public void set(int i, double v) {
        x[i] = v;
    }

    public void setStraight(int i, int k, double v) {
        x[i + width * k] = v;
    }

    public Feature(BufferedImage image) {
        this.width = image.getWidth();
        this. height = image.getHeight();
        x = new double[width * height];
        for (int k = 0; k < height; k++) {
            for (int i = 0; i < width; i++) {
                x[i + width * k] = image.getRGB(i, k) / 255;
            }
        }
    }

    public BufferedImage toImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int k = 0; k < height; k++) {
            for (int i = 0; i < width; i++) {
                image.setRGB(i, k, (byte) (x[i + width * k] * 255));
            }
        }
        return image;
    }

    public Feature(Feature other) {
        this.width = other.width;
        this.height = other.height;
        x = new double[other.size()];
        System.arraycopy(other.x, 0, x, 0, size());
    }

    public int size() {
        return x.length;
    }
    public double get(int i) {
        return x[i];
    }

    public void addHead(int v) {
        double[] newX = new double[size() + 1];
        newX[0] = v;
        System.arraycopy(x, 0, newX, 1, x.length);
        x = newX;
    }
}
